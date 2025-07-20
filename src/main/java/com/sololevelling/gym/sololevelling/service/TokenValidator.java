/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.auth.JwtUtil;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.auth.TokenValidationResult;
import com.sololevelling.gym.sololevelling.repo.AccessTokenRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenValidator {

    private final JwtUtil jwtUtil;
    private final AccessTokenRepository accessTokenRepo;
    private final UserRepository userRepository;

    public TokenValidator(JwtUtil jwtUtil, 
                          AccessTokenRepository accessTokenRepo,
                          UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.accessTokenRepo = accessTokenRepo;
        this.userRepository = userRepository;
    }

    public TokenValidationResult validateToken(String token) {
        // Basic token presence check
        if (token == null || token.isBlank()) {
            return new TokenValidationResult(false, "Token is required", null, "MISSING_TOKEN", false);
        }

        // Database revocation check

        try {
            // Cryptographic validation
            if (!jwtUtil.validateToken(token)) {
                return new TokenValidationResult(false, "Invalid signature", null, "INVALID_SIGNATURE", false);
            }

            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();

            // Expiration check
            if (claims.getExpiration().before(new Date())) {
                return new TokenValidationResult(false, "Token expired", username, "EXPIRED", true);
            }

            User user = userRepository.findByEmail(username).orElse(null);
            if (user == null) {
                return new TokenValidationResult(false, "User not found", username, "USER_NOT_FOUND", false);
            }

            // Account status checks
            if (user.isLocked()) {
                return new TokenValidationResult(false, "Account locked", username, "ACCOUNT_LOCKED", false);
            }

            // Logout timestamp validation
            Date issuedAt = claims.getIssuedAt();
            if (user.getLastLogout() != null && 
                issuedAt.toInstant().isBefore(user.getLastLogout().atZone(ZoneId.systemDefault()).toInstant())) {
                return new TokenValidationResult(false, "Token invalidated by logout", username, "LOGGED_OUT", false);
            }

            if (accessTokenRepo.findByToken(token).isEmpty()) {
                return new TokenValidationResult(false, "Token revoked", null, "REVOKED", false);
            }


            return new TokenValidationResult(true, "Token is valid", username, "VALID", false);

        } catch (ExpiredJwtException ex) {
            return new TokenValidationResult(false, "Token expired", ex.getClaims().getSubject(), "EXPIRED", true);
        } catch (JwtException | IllegalArgumentException ex) {
            return new TokenValidationResult(false, "Invalid token: " + ex.getMessage(), null, "MALFORMED", false);
        }
    }
}
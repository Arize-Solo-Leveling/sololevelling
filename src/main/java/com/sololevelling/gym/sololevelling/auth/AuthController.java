/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.auth;

import com.sololevelling.gym.sololevelling.model.dto.auth.*;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.service.TokenValidator;
import com.sololevelling.gym.sololevelling.service.UserService;
import com.sololevelling.gym.sololevelling.util.exception.AccountLockException;
import com.sololevelling.gym.sololevelling.util.exception.InvalidPasswordException;
import com.sololevelling.gym.sololevelling.util.exception.InvalidRefreshTokenException;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication APIs")  // <-- Swagger Tag
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenValidator tokenValidator;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login existing user")
    @Transactional
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {
        String newToken = userService.refreshAccessToken(request.refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing token");
        }

        String token = authHeader.substring(7);
        userService.logoutUser(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody @Valid TokenValidationRequest request) {
        return ResponseEntity.ok(tokenValidator.validateToken(request.token()));


    }
}

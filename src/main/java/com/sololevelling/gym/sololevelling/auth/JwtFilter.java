/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.auth;

import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.AccessTokenRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

@Component
@Order(1)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AccessTokenRepository accessTokenRepo;
    private final UserRepository userRepository;

    public JwtFilter(UserDetailsService userDetailsService,
                     JwtUtil jwtUtil,
                     AccessTokenRepository accessTokenRepo,
                     UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.accessTokenRepo = accessTokenRepo;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
//        System.out.println("Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Optional: Check if token exists in the DB
                boolean tokenExists = accessTokenRepo.findByToken(token).isPresent();
                if (!tokenExists) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token has been invalidated");
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    // Additional logout-time check
                    User user = userRepository.findByEmail(username).orElse(null);
                    if (user == null) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                        return;
                    }

                    Date issuedAt = jwtUtil.extractIssuedAt(token);
                    if (user.getLastLogout() != null) {
                        var lastLogoutInstant = user.getLastLogout().atZone(ZoneId.systemDefault()).toInstant();
                        if (issuedAt.toInstant().isBefore(lastLogoutInstant)) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token issued before last logout");
                            return;
                        }
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
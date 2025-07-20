/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.auth;

import com.sololevelling.gym.sololevelling.model.dto.auth.TokenValidationResponse;
import com.sololevelling.gym.sololevelling.service.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final TokenValidator tokenValidator;

    public JwtFilter(UserDetailsService userDetailsService, TokenValidator tokenValidator) {
        this.userDetailsService = userDetailsService;
        this.tokenValidator = tokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            TokenValidationResponse result = tokenValidator.validateToken(token);

            if (!result.valid()) {
                handleInvalidToken(response, result);
                return;
            }

            // If valid, proceed with authentication
            String username = result.username();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response,
                                    TokenValidationResponse result) throws IOException {

        int statusCode = HttpStatus.UNAUTHORIZED.value();
        String message = result.message();

        // Special handling for locked accounts
        if ("ACCOUNT_LOCKED".equals(result.status())) {
            statusCode = HttpStatus.LOCKED.value();
        }

        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(
                String.format("{\"error\": \"%s\", \"status\": \"%s\", \"code\": %d}",
                        message, result.status(), statusCode)
        );
    }
}
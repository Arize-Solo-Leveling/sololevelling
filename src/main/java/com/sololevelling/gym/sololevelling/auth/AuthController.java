/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.auth;

import com.sololevelling.gym.sololevelling.model.dto.auth.AuthRequest;
import com.sololevelling.gym.sololevelling.model.dto.auth.LoginRequest;
import com.sololevelling.gym.sololevelling.model.dto.auth.TokenRefreshRequest;
import com.sololevelling.gym.sololevelling.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login existing user")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }
    @PostMapping("/refresh")

    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request) {
        String newToken = userService.refreshAccessToken(request.refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", newToken));
    }
}

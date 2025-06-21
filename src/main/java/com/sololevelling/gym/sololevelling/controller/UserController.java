package com.sololevelling.gym.sololevelling.controller;


import com.sololevelling.gym.sololevelling.model.dto.user.StatAllocationRequest;
import com.sololevelling.gym.sololevelling.model.dto.user.UserDto;
import com.sololevelling.gym.sololevelling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(auth.getName()));
    }

    @PostMapping("/allocate")
    public ResponseEntity<UserDto> allocateStats(Authentication auth, @RequestBody StatAllocationRequest request) {

        return ResponseEntity.ok(userService.allocateStats(auth.getName(), request));
    }
}

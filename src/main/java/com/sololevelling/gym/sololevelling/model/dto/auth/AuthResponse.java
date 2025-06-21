package com.sololevelling.gym.sololevelling.model.dto.auth;

import lombok.Data;

@Data
public class AuthResponse {
    public String token;
    public String username;

    public AuthResponse(String s, String name) {
        token = s;
        username = name;
    }
}

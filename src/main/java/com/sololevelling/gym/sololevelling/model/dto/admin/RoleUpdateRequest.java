package com.sololevelling.gym.sololevelling.model.dto.admin;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String role;  // e.g., "ADMIN", "USER", "MODERATOR"
}

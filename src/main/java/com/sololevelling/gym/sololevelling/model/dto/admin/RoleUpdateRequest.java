/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.admin;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String role;  // e.g., "ADMIN", "USER", "MODERATOR"
}

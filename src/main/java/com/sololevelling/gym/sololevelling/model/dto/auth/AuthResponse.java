/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

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

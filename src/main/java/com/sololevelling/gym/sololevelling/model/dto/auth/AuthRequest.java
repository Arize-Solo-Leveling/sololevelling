/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.auth;

import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import lombok.Data;

@Data
public class AuthRequest {
    public String email;
    public String password;
    public String name; // optional for login
    public UserClass userClass;
}

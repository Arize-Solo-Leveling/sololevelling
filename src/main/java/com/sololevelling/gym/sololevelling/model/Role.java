/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    private String name; // e.g., "ROLE_USER", "ROLE_ADMIN"
}

/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class AccessToken {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, length = 1000)
    private String token;

    @ManyToOne
    private User user;

    private Instant expiryDate;

    // Constructors, Getters, Setters
}

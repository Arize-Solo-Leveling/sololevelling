/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Quest {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private int experienceReward;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime expiresAt;

    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Getters & Setters
}


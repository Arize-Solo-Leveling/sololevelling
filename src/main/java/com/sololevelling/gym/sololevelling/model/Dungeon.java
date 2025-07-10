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

@Entity
@Data
public class Dungeon {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String type; // time-attack, endurance
    private String objective;
    private int expReward;
    private String lootReward;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JsonIgnore
    private User user;
    private boolean completed;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

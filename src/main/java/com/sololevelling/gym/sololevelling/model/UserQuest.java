/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class UserQuest {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    private Quest quest;

    private boolean completed;

    private LocalDateTime assignedAt;
}

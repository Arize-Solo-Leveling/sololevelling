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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Workout {
    public String name;
    @Id
    @GeneratedValue
    private UUID id;
    private LocalDateTime date;
    private int durationMinutes;
    private double totalVolume;
    private int experienceGained;

    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Exercise> exercises = new ArrayList<>();
}

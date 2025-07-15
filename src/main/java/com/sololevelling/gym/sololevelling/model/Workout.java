/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
public class Workout {
    @Id
    private ObjectId id;

    private String name;
    private LocalDateTime date;
    private int durationMinutes;
    private double totalVolume;
    private int experienceGained;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @DBRef
    private List<Exercise> exercises = new ArrayList<>();
}


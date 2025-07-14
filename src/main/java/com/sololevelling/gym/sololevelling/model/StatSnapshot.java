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

import java.time.LocalDate;

@Document
@Data
public class StatSnapshot {
    @Id
    private ObjectId id;

    private LocalDate date;
    private int level;
    private int experience;
    private int strength;
    private int endurance;
    private int agility;
    private int intelligence;
    private int luck;
    private double volume;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private User user;
}

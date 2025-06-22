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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class StatSnapshot {
    @Id
    @GeneratedValue
    private UUID id;

    private LocalDate date;

    private int level;
    private int experience;
    private int strength;
    private int endurance;
    private int agility;
    private int intelligence;
    private int luck;
    private double volume;
    @ManyToOne
    @JsonIgnore
    private User user;

}

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

@Document
@Data
public class Quest {
    @Id
    private ObjectId id;

    private String title;
    private String description;
    private int experienceReward;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean completed;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private User user;
}



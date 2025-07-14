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
public class UserQuest {
    @Id
    private ObjectId id;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private Quest quest;

    private boolean completed;
    private LocalDateTime assignedAt;
}

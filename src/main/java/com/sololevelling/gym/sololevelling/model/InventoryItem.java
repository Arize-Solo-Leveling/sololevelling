/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryRarity;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class InventoryItem {
    @Id
    private ObjectId id;

    private String name;
    private InventoryRarity rarity;
    private String slot;
    private Stats statBoosts;
    private boolean equipped = false;

    @DBRef
    @ToString.Exclude
    @JsonIgnore
    private User user;
}

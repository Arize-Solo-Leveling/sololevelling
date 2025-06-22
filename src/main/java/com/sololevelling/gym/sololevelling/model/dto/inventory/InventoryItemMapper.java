/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.inventory;

import com.sololevelling.gym.sololevelling.model.InventoryItem;

public class InventoryItemMapper {
    public static InventoryItemDto toDto(InventoryItem item) {
        InventoryItemDto dto = new InventoryItemDto();
        dto.id = item.getId();
        dto.name = item.getName();
        dto.rarity = item.getRarity();
        dto.slot = item.getSlot();
        dto.equipped = item.isEquipped();
        dto.statBoosts = item.getStatBoosts();
        return dto;
    }
}

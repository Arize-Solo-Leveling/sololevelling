/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.dungeon;

import com.sololevelling.gym.sololevelling.model.Dungeon;

import java.time.LocalDateTime;

public class DungeonMapper {
    public static DungeonDto toDto(Dungeon dungeon) {
        DungeonDto dto = new DungeonDto();
        dto.id = dungeon.getId();
        dto.name = dungeon.getName();
        dto.type = dungeon.getType();
        dto.objective = dungeon.getObjective();
        dto.expReward = dungeon.getExpReward();
        dto.lootReward = dungeon.getLootReward();
        dto.completed = dungeon.isCompleted();
        dto.expired = LocalDateTime.now().isAfter(dungeon.getExpiresAt());

        return dto;
    }
}

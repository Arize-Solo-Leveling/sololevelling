package com.sololevelling.gym.sololevelling.model.dto.dungeon;

import com.sololevelling.gym.sololevelling.model.Dungeon;

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
        dto.isWeekly = dungeon.isWeekly();
        return dto;
    }
}

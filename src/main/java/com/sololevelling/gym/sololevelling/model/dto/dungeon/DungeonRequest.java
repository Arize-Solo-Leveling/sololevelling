package com.sololevelling.gym.sololevelling.model.dto.dungeon;

import lombok.Data;

@Data
public class DungeonRequest {
    private String name;
    private String type;
    private String objective;
    private int expReward;
    private String lootReward;
    private boolean weekly;
    // Getters and Setters
}

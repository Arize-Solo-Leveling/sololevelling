package com.sololevelling.gym.sololevelling.model.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponse {
    private long userCount;
    private long questCount;
    private long dungeonCount;
    private long workoutCount;
    private long inventoryCount;
}

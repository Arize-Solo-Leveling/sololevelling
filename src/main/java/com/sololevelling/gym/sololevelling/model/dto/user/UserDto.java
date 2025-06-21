package com.sololevelling.gym.sololevelling.model.dto.user;

import com.sololevelling.gym.sololevelling.model.InventoryItem;
import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutSummaryDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserDto {
    public UUID id;
    public String name;
    public String email;
    public int level;
    public int experience;
    public int statPoints;
    public Stats stats;
    public String userClass;
    public List<InventoryItem> inventory = new ArrayList<>();
    public List<WorkoutSummaryDto> workouts = new ArrayList<>();
    public List<Quest> completedQuests = new ArrayList<>();
}

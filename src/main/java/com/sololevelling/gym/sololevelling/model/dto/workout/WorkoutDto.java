package com.sololevelling.gym.sololevelling.model.dto.workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WorkoutDto {
    public UUID id;
    public String name;
    public LocalDateTime date;
    public double totalVolume;
    public int experienceGained;
    public List<ExerciseDto> exercises;
}
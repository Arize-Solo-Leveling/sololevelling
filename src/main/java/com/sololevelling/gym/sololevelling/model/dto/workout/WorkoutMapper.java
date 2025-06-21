package com.sololevelling.gym.sololevelling.model.dto.workout;

import com.sololevelling.gym.sololevelling.model.Workout;

public class WorkoutMapper {
    public static WorkoutDto toDto(Workout w) {
        WorkoutDto dto = new WorkoutDto();
        dto.id = w.getId();
        dto.name = w.getName();
        dto.date = w.getDate();
        dto.totalVolume = w.getTotalVolume();
        dto.experienceGained = w.getExperienceGained();
        dto.exercises = w.getExercises().stream().map(e -> {
            ExerciseDto ed = new ExerciseDto();
            ed.name = e.getName();
            ed.sets = e.getSets();
            ed.reps = e.getReps();
            ed.weight = e.getWeight();
            return ed;
        }).toList();
        return dto;
    }
}

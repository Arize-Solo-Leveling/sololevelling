/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.user;

import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.level = user.getLevel();
        dto.experience = user.getExperience();
        dto.statPoints = user.getStatPoints();
        dto.stats = user.getStats();
        dto.userClass = String.valueOf(user.getUserClass());
        dto.workouts = user.getWorkouts().stream().map(workout -> {
            WorkoutSummaryDto workoutSummaryDto = new WorkoutSummaryDto();
            workoutSummaryDto.id = workout.getId();
            workoutSummaryDto.name = workout.getName();
            workoutSummaryDto.exercises = workout.getExercises().size();
            return workoutSummaryDto;
        }).toList();
        dto.inventory = user.getInventory();
        dto.completedQuests = user.getCompletedQuests();
        return dto;
    }
}

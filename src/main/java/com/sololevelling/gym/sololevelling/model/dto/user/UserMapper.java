/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.user;

import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryItemDto;
import com.sololevelling.gym.sololevelling.model.dto.inventory.ItemSummaryDto;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestSummaryDto;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutSummaryDto;
import com.sololevelling.gym.sololevelling.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class UserMapper {
    private final ExperienceService experienceService;

    @Autowired
    public UserMapper(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

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
        ExperienceService.ExperienceProgress progress = experienceService.getExperienceProgress(user);
        dto.totalExperienceEarned = progress.totalExpEarned();
        dto.experienceToNextLevel = progress.expForNextLevel();
        dto.inventory = user.getInventory().stream().map(item -> {
            ItemSummaryDto itemSummaryDto = new ItemSummaryDto();
            itemSummaryDto.id = item.getId();
            itemSummaryDto.name = item.getName();

            Stats stats = item.getStatBoosts();
            itemSummaryDto.totalPoints = IntStream.of(
                    stats.getStrength(),
                    stats.getEndurance(),
                    stats.getAgility(),
                    stats.getIntelligence(),
                    stats.getLuck()
            ).sum();

            return itemSummaryDto;
        }).toList();

        dto.completedQuests = user.getCompletedQuests().stream().map(quest -> {
            QuestSummaryDto questSummaryDto = new QuestSummaryDto();
            questSummaryDto.id = quest.getId();
            questSummaryDto.title = quest.getTitle();
            questSummaryDto.experienceReward = quest.getExperienceReward();
            questSummaryDto.daily = quest.isDaily();
            return questSummaryDto;
        }).toList();

        return dto;
    }
}

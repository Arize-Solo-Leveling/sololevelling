/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.user;

import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.dto.inventory.ItemSummaryDto;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestSummaryDto;
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
    public int totalExperienceEarned;
    public int experienceToNextLevel;
    public List<ItemSummaryDto> inventory = new ArrayList<>();
    public List<WorkoutSummaryDto> workouts = new ArrayList<>();
    public List<QuestSummaryDto> completedQuests = new ArrayList<>();
}

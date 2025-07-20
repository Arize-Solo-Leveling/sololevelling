/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.Workout;
import com.sololevelling.gym.sololevelling.model.dto.progress.ProgressSummaryDto;
import com.sololevelling.gym.sololevelling.model.dto.progress.StatProgressDto;
import com.sololevelling.gym.sololevelling.model.dto.progress.WorkoutGraphDto;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.repo.WorkoutRepository;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WorkoutRepository workoutRepo;

    public StatProgressDto getStatProgress(String email) {
        SoloLogger.info("📊 Fetching stat progress for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        StatProgressDto dto = new StatProgressDto();
        dto.level = user.getLevel();
        dto.strength = user.getStats().getStrength();
        dto.endurance = user.getStats().getEndurance();
        dto.agility = user.getStats().getAgility();
        dto.intelligence = user.getStats().getIntelligence();

        return dto;
    }

    public List<WorkoutGraphDto> getWorkoutGraph(String email) {
        SoloLogger.info("📅 Generating workout graph data for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        return workoutRepo.findByUser(user).stream().map(w -> {
            WorkoutGraphDto dto = new WorkoutGraphDto();
            dto.date = w.getDate();
            dto.totalVolume = w.getTotalVolume();
            return dto;
        }).toList();
    }

    public ProgressSummaryDto getSummary(String email) {
        SoloLogger.info("📋 Generating progress summary for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        ProgressSummaryDto dto = new ProgressSummaryDto();
        dto.level = user.getLevel();
        dto.experience = user.getExperience();
        dto.strength = user.getStats().getStrength();
        dto.endurance = user.getStats().getEndurance();
        dto.agility = user.getStats().getAgility();
        dto.intelligence = user.getStats().getIntelligence();
        dto.luck = user.getStats().getLuck();
        dto.volume = user.getWorkouts().stream()
                .mapToDouble(Workout::getTotalVolume)
                .sum();
        return dto;
    }
}

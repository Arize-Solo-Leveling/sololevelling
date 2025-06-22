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
import com.sololevelling.gym.sololevelling.model.dto.progress.StatSnapshotDto;
import com.sololevelling.gym.sololevelling.model.dto.progress.WorkoutGraphDto;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.repo.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WorkoutRepository workoutRepo;

    public List<StatSnapshotDto> getStatProgress(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        // You'd track snapshots separately if needed.
        // For demo: mock stat history using workout history dates
        return workoutRepo.findByUser(user).stream().map(w -> {
            StatSnapshotDto dto = new StatSnapshotDto();
            dto.date = w.getDate();
            dto.level = user.getLevel(); // or computed if you track over time
            dto.experience = user.getExperience();
            dto.strength = user.getStats().getStrength(); // optional historic values
            return dto;
        }).toList();
    }

    public List<WorkoutGraphDto> getWorkoutGraph(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        return workoutRepo.findByUser(user).stream().map(w -> {
            WorkoutGraphDto dto = new WorkoutGraphDto();
            dto.date = w.getDate();
            dto.totalVolume = w.getTotalVolume();
            return dto;
        }).toList();
    }

    public ProgressSummaryDto getSummary(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
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

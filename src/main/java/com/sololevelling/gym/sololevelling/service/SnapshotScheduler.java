/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.StatSnapshot;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.Workout;
import com.sololevelling.gym.sololevelling.repo.StatSnapshotRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SnapshotScheduler {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private StatSnapshotRepository snapshotRepo;

    @Scheduled(cron = "0 0 0 * * *")
    public void saveDailySnapshots() {
        SoloLogger.info("📸 Starting daily stat snapshot process");
        List<User> users = userRepo.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
            try {
                StatSnapshot snapshot = new StatSnapshot();
                snapshot.setUser(user);
                snapshot.setDate(today);
                snapshot.setLevel(user.getLevel());
                snapshot.setExperience(user.getExperience());
                snapshot.setStrength(user.getStats().getStrength());
                snapshot.setEndurance(user.getStats().getEndurance());
                snapshot.setAgility(user.getStats().getAgility());
                snapshot.setIntelligence(user.getStats().getIntelligence());
                snapshot.setLuck(user.getStats().getLuck());

                double totalVolume = user.getWorkouts().stream()
                        .mapToDouble(Workout::getTotalVolume)
                        .sum();
                snapshot.setVolume(totalVolume);

                snapshotRepo.save(snapshot);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save snapshot for user: " + user.getEmail(), e);
            }
        }
    }
}

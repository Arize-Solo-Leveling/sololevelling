package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.StatSnapshot;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.Workout;
import com.sololevelling.gym.sololevelling.repo.StatSnapshotRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
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

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void saveDailySnapshots() {
        List<User> users = userRepo.findAll();
        LocalDate today = LocalDate.now();

        for (User user : users) {
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
        }
    }
}

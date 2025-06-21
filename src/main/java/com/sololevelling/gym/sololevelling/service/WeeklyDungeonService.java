package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeeklyDungeonService {

    @Autowired
    private DungeonRepository dungeonRepo;
    @Autowired
    private UserRepository userRepo;

    // Cron: Every Monday at 00:00
    @Scheduled(cron = "0 0 0 * * MON")
    public void generateWeeklyDungeons() {
        List<User> users = userRepo.findAll();

        for (User user : users) {
            // Create new weekly challenges
            List<Dungeon> dungeons = List.of(
                    buildDungeon("HIIT Gauntlet", "endurance", "Complete 5 HIIT rounds", 150, "Sweat Band", user),
                    buildDungeon("Agility Course", "agility", "Cone drills under 90s", 180, "Agility Shoes", user),
                    buildDungeon("Strength Pyramid", "strength", "Max reps of squats & bench", 200, "Power Vest", user)
            );

            dungeonRepo.saveAll(dungeons);
        }

        System.out.println("✅ Weekly gym dungeons generated.");
    }

    private Dungeon buildDungeon(String name, String type, String objective, int exp, String loot, User user) {
        Dungeon d = new Dungeon();
        d.setName(name);
        d.setType(type);
        d.setObjective(objective);
        d.setExpReward(exp);
        d.setLootReward(loot);
        d.setCompleted(false);
        d.setWeekly(true);
        d.setUser(user);
        return d;
    }
}

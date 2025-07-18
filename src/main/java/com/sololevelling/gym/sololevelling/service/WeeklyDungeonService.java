/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class WeeklyDungeonService {

    private final List<Dungeon> baseWeeklyDungeons = List.of(
            createBaseDungeon("HIIT Gauntlet", "endurance", "Complete 5 HIIT rounds", 150, "Sweat Band"),
            createBaseDungeon("Agility Course", "agility", "Cone drills under 90s", 180, "Agility Shoes"),
            createBaseDungeon("Strength Pyramid", "strength", "Max reps of squats & bench", 100, "Power Vest"),
            createBaseDungeon("Core Crusher", "strength", "Complete 100 sit-ups", 100, "Ab Belt"),
            createBaseDungeon("Upper Body Blitz", "strength", "Do 50 push-ups and 30 pull-ups", 140, "Arm Straps"),
            createBaseDungeon("Lower Body Burn", "strength", "Perform 100 lunges and 100 squats", 160, "Leg Wraps"),
            createBaseDungeon("Speed Sprint", "agility", "Sprint 200 meters × 5 rounds", 120, "Speed Shoes"),
            createBaseDungeon("Yoga Flow", "mobility", "Complete a 30-minute yoga session", 80, "Flex Band"),
            createBaseDungeon("Cardio King", "endurance", "Burn 500 calories in one workout", 180, "Heart Monitor"),
            createBaseDungeon("Recovery Dungeon", "recovery", "Do foam rolling & deep stretch for 20 min", 60, "Recovery Roller"),
            createBaseDungeon("Plyometric Madness", "power", "50 jump squats, 50 box jumps, 25 burpees", 170, "Jump Trainer"),
            createBaseDungeon("Balance Challenge", "stability", "Hold 1-minute single-leg stance each leg + BOSU drills", 90, "Balance Pads")
    );
    @Autowired
    private DungeonRepository dungeonRepo;
    @Autowired
    private UserRepository userRepo;

    private static Dungeon createBaseDungeon(String name, String type, String objective, int exp, String loot) {
        Dungeon d = new Dungeon();
        dungeonUSerHelper(name, type, objective, exp, loot, d);
        d.setCreatedAt(LocalDateTime.now());
        d.setExpiresAt(d.getCreatedAt().plusWeeks(1));
        return d;
    }

    static void dungeonUSerHelper(String name, String type, String objective, int expReward, String lootReward, Dungeon clone) {
        clone.setName(name);
        clone.setType(type);
        clone.setObjective(objective);
        clone.setExpReward(expReward);
        clone.setLootReward(lootReward);
        clone.setCompleted(false);
    }

    @Scheduled(cron = "0 0 0 * * MON") // Every Monday at 00:00
    public void generateWeeklyDungeons() {
        List<User> users = userRepo.findAll();

        for (User user : users) {
            List<Dungeon> userDungeons = baseWeeklyDungeons.stream()
                    .map(base -> cloneDungeonForUser(base, user))
                    .toList();

            dungeonRepo.saveAll(userDungeons);
        }

    }

    public List<Dungeon> pickRandomWeeklyDungeon(ObjectId userId) {
        User user = userRepo.findById(userId).orElseThrow();

        List<Dungeon> shuffled = new java.util.ArrayList<>(baseWeeklyDungeons);
        Collections.shuffle(shuffled);

        int count = new Random().nextInt(2) + 2; // Random: 2 to 3
        List<Dungeon> selected = shuffled.subList(0, Math.min(count, shuffled.size()))
                .stream()
                .map(d -> cloneDungeonForUser(d, user))
                .toList();

        dungeonRepo.saveAll(selected);
        return selected;
    }

    private Dungeon cloneDungeonForUser(Dungeon template, User user) {
        Dungeon clone = new Dungeon();
        clone.setName(template.getName());
        clone.setType(template.getType());
        clone.setObjective(template.getObjective());
        clone.setExpReward(template.getExpReward());
        clone.setLootReward(template.getLootReward());
        clone.setCreatedAt(LocalDateTime.now());
        clone.setExpiresAt(clone.getCreatedAt().plusWeeks(1));
        clone.setCompleted(false);
        clone.setUser(user);
        return clone;
    }
}

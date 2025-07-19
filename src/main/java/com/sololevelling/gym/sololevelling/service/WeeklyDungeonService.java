/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonDto;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonMapper;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class WeeklyDungeonService {

    private static final List<Dungeon> BASE_WEEKLY_DUNGEONS = List.of(
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

    @Scheduled(cron = "0 0 0 * * MON")
    public void generateWeeklyDungeons() {
        SoloLogger.info("🏰 Starting weekly dungeon generation");
        List<User> users = userRepo.findAll();
        SoloLogger.debug("Found {} users to generate dungeons for", users.size());

        int totalGenerated = 0;
        for (User user : users) {
            try {
                List<Dungeon> userDungeons = BASE_WEEKLY_DUNGEONS.stream()
                        .map(base -> cloneDungeonForUser(base, user))
                        .toList();

                dungeonRepo.saveAll(userDungeons);
                totalGenerated += userDungeons.size();
            } catch (Exception e) {
                SoloLogger.error("❌ Failed to generate dungeons for user {}: {}", user.getEmail(), e.getMessage());
            }
        }

        SoloLogger.info("✅ Generated {} weekly dungeons for {} users", totalGenerated, users.size());
    }

    public List<DungeonDto> pickRandomWeeklyDungeon(ObjectId userId) {
        SoloLogger.info("🎲 Picking random weekly dungeons for user: {}", userId);
        User user = userRepo.findById(userId).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", userId);
            return new UserNotFoundException("User not found");
        });

        List<Dungeon> shuffled = new ArrayList<>(BASE_WEEKLY_DUNGEONS);
        Collections.shuffle(shuffled);

        int count = new Random().nextInt(2) + 2; // 2-3 dungeons
        List<Dungeon> selected = shuffled.subList(0, Math.min(count, shuffled.size()))
                .stream()
                .map(d -> cloneDungeonForUser(d, user))
                .toList();

        List<Dungeon> saved = dungeonRepo.saveAll(selected);
        SoloLogger.info("📜 Generated {} random dungeons for user {}", saved.size(), userId);
        return saved.stream().map(DungeonMapper::toDto).toList();
    }

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

    private Dungeon cloneDungeonForUser(Dungeon template, User user) {
        Dungeon clone = new Dungeon();
        dungeonUSerHelper(template.getName(), template.getType(), template.getObjective(),
                template.getExpReward(), template.getLootReward(), clone);
        clone.setCreatedAt(LocalDateTime.now());
        clone.setExpiresAt(clone.getCreatedAt().plusWeeks(1));
        clone.setUser(user);
        return clone;
    }
}
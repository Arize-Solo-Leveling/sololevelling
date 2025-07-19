/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestDto;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestMapper;
import com.sololevelling.gym.sololevelling.repo.QuestRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class QuestGenerator {

    private static final List<Quest> NEW_QUESTS = List.of(
            createQuest("Log 1 workout", "Complete at least 1 workout session today.", 10),
            createQuest("Do 20 push-ups", "Perform a total of 20 push-ups today.", 25),
            createQuest("Run 1 kilometers", "Track a 1 km run or walk.", 20),
            createQuest("Run 10 kilometers", "Track a 10 km run or walk.", 100),
            createQuest("Stretch for 10 minutes", "Complete at least 10 minutes of full-body stretching.", 40),
            createQuest("Drink 2 liters of water", "Stay hydrated and log 2L of water intake.", 30),
            createQuest("Burn 300 calories", "Burn 300+ calories in any workout activity.", 90),
            createQuest("Do 3 different exercises", "Log at least 3 distinct exercises (e.g., push-ups, squats, planks).", 60),
            createQuest("Complete a HIIT workout", "High-Intensity Interval Training session completed.", 100)
    );

    @Autowired
    private QuestRepository questRepo;
    @Autowired
    private UserRepository userRepo;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyQuests() {
        SoloLogger.info("🔄 Starting daily quest generation");
        List<User> users = userRepo.findAll();
        SoloLogger.debug("Found {} users to generate quests for", users.size());

        List<Quest> questsToSave = new ArrayList<>();
        for (User user : users) {
            for (Quest template : NEW_QUESTS) {
                Quest userQuest = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward());
                userQuest.setUser(user);
                questsToSave.add(userQuest);
            }
        }

        questRepo.saveAll(questsToSave);
        SoloLogger.info("✅ Generated {} daily quests for {} users",
                questsToSave.size(), users.size());
    }

    public List<QuestDto> pickRandomDailyQuests(ObjectId uuid) {
        SoloLogger.info("🎲 Picking random daily quests for user: {}", uuid);
        Collections.shuffle(NEW_QUESTS);
        int count = new Random().nextInt(2) + 2; // 2-3 quests
        List<Quest> selectedTemplates = NEW_QUESTS.subList(0, Math.min(count, NEW_QUESTS.size()));

        User user = userRepo.findById(uuid).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", uuid);
            return new UserNotFoundException("User not found");
        });

        List<Quest> personalizedQuests = new ArrayList<>();
        for (Quest template : selectedTemplates) {
            Quest clone = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward());
            clone.setUser(user);
            personalizedQuests.add(clone);
        }

        List<Quest> saved = questRepo.saveAll(personalizedQuests);
        SoloLogger.info("📜 Generated {} quests for user {}", saved.size(), uuid);
        return saved.stream()
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }

    private static Quest createQuest(String title, String desc, int xp) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription(desc);
        q.setExperienceReward(xp);
        q.setCreatedAt(LocalDateTime.now());
        q.setExpiresAt(q.getCreatedAt().plusDays(1));
        q.setCompleted(false);
        return q;
    }
}

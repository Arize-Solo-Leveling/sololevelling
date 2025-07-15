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
import com.sololevelling.gym.sololevelling.repo.UserQuestRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
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

    private final List<Quest> newQuests = List.of(
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
    @Autowired
    private UserQuestRepository userQuestRepo;

    @Scheduled(cron = "0 0 0 * * *") // daily at midnight
    public void generateDailyQuests() {
        List<Quest> dailyQuestsTemplate = newQuests.stream().toList();
        List<User> users = userRepo.findAll();

        List<Quest> questsToSave = new ArrayList<>();

        for (User user : users) {
            for (Quest template : dailyQuestsTemplate) {
                Quest userQuest = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward());
                userQuest.setUser(user);
                questsToSave.add(userQuest);
            }
        }

        questRepo.saveAll(questsToSave);
    }

    public List<QuestDto> pickRandomDailyQuests(ObjectId uuid) {
        List<Quest> allTemplates = new ArrayList<>(newQuests);

        Collections.shuffle(allTemplates);
        int count = new Random().nextInt(2) + 2; // Random between 2 and 3
        List<Quest> selectedTemplates = allTemplates.subList(0, Math.min(count, allTemplates.size()));

        User user = userRepo.findById(uuid).orElseThrow();
        List<Quest> personalizedQuests = new ArrayList<>();

        for (Quest template : selectedTemplates) {
            Quest clone = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward());
            clone.setUser(user);
            personalizedQuests.add(clone);
        }
        List<Quest> saved = questRepo.saveAll(personalizedQuests);

        return saved.stream()
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }


    private Quest createQuest(String title, String desc, int xp) {
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

/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.QuestRepository;
import com.sololevelling.gym.sololevelling.repo.UserQuestRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class QuestGenerator {

    private final List<Quest> newQuests = List.of(
            createQuest("Log 1 workout", "Complete at least 1 workout session today.", 10, true),
            createQuest("Do 20 push-ups", "Perform a total of 20 push-ups today.", 25, true),
            createQuest("Run 1 kilometers", "Track a 1 km run or walk.", 20, true),
            createQuest("Run 10 kilometers", "Track a 10 km run or walk.", 100, false),
            createQuest("Stretch for 10 minutes", "Complete at least 10 minutes of full-body stretching.", 40, true),
            createQuest("Drink 2 liters of water", "Stay hydrated and log 2L of water intake.", 30, true),
            createQuest("Burn 300 calories", "Burn 300+ calories in any workout activity.", 90, false),
            createQuest("Do 3 different exercises", "Log at least 3 distinct exercises (e.g., push-ups, squats, planks).", 60, true),
            createQuest("Complete a HIIT workout", "High-Intensity Interval Training session completed.", 100, false)
    );
    @Autowired
    private QuestRepository questRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserQuestRepository userQuestRepo;

    @Scheduled(cron = "0 0 0 * * *") // daily at midnight
    public void generateDailyQuests() {
        List<Quest> dailyQuestsTemplate = newQuests.stream().filter(Quest::isDaily).toList();
        List<User> users = userRepo.findAll();

        List<Quest> questsToSave = new ArrayList<>();

        for (User user : users) {
            for (Quest template : dailyQuestsTemplate) {
                Quest userQuest = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward(), template.isDaily());
                userQuest.setUser(user);
                questsToSave.add(userQuest);
            }
        }

        questRepo.saveAll(questsToSave);
    }

    public List<Quest> pickRandomDailyQuests(UUID uuid) {
        List<Quest> allTemplates = new ArrayList<>(newQuests);

        Collections.shuffle(allTemplates);
        int count = new Random().nextInt(2) + 2; // Random between 2 and 3
        List<Quest> selectedTemplates = allTemplates.subList(0, Math.min(count, allTemplates.size()));

        User user = userRepo.findById(uuid).orElseThrow();
        List<Quest> personalizedQuests = new ArrayList<>();

        for (Quest template : selectedTemplates) {
            Quest clone = createQuest(template.getTitle(), template.getDescription(), template.getExperienceReward(), template.isDaily());
            clone.setUser(user);
            personalizedQuests.add(clone);
        }

        return questRepo.saveAll(personalizedQuests);
    }


    private Quest createQuest(String title, String desc, int xp, boolean daily) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription(desc);
        q.setExperienceReward(xp);
        q.setDaily(daily);
        q.setCreatedAt(LocalDateTime.now());
        if (daily) {
            q.setExpiresAt(q.getCreatedAt().plusDays(1));
        } else {
            q.setExpiresAt(LocalDateTime.now().plusWeeks(1));
        }
        q.setCompleted(false);
        return q;
    }
}

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.repo.QuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class QuestGenerator {

    @Autowired
    private QuestRepository questRepo;

    @Scheduled(cron = "0 0 0 * * *") // daily at midnight
    public void generateDailyQuests() {
        List<Quest> newQuests = List.of(
                createQuest("Log 1 workout", "Complete at least 1 workout", 50),
                createQuest("Do 50 push-ups", "Do a total of 50 push-ups", 70)
        );
        questRepo.saveAll(newQuests);
    }

    private Quest createQuest(String title, String desc, int xp) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription(desc);
        q.setExperienceReward(xp);
        q.setDaily(true);
        q.setCreatedAt(LocalDateTime.now());
        return q;
    }
}

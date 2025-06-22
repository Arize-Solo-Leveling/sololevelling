package com.sololevelling.gym.sololevelling.model.dto.quest;

import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.model.User;

import java.time.LocalDateTime;

public class QuestMapper {
    public static QuestDto toDto(Quest quest, User user) {
        QuestDto dto = new QuestDto();
        dto.id = quest.getId();
        dto.title = quest.getTitle();
        dto.description = quest.getDescription();
        dto.experienceReward = quest.getExperienceReward();
        dto.daily = quest.isDaily();
        dto.completed = user.getCompletedQuests().contains(quest);
        dto.expired = LocalDateTime.now().isAfter(quest.getExpiresAt());
        return dto;
    }

    public static QuestDto toDto(Quest quest, boolean completed) {
        QuestDto dto = new QuestDto();
        dto.id = quest.getId();
        dto.title = quest.getTitle();
        dto.description = quest.getDescription();
        dto.experienceReward = quest.getExperienceReward();
        dto.daily = quest.isDaily();
        dto.completed = completed;
        dto.expired = LocalDateTime.now().isAfter(quest.getExpiresAt());
        return dto;
    }

}

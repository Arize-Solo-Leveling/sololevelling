/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.InventoryItem;
import com.sololevelling.gym.sololevelling.model.Quest;
import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryRarity;
import com.sololevelling.gym.sololevelling.model.dto.quest.CreateQuestRequest;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestDto;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestMapper;
import com.sololevelling.gym.sololevelling.repo.InventoryItemRepository;
import com.sololevelling.gym.sololevelling.repo.QuestRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public List<QuestDto> getAvailableQuests(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        LocalDateTime resetTime = LocalDateTime.now().minusDays(1);
        List<Quest> recentQuests = questRepo.findAllByCreatedAtAfter(resetTime);
        return recentQuests.stream()
                .filter(q -> !user.getCompletedQuests().contains(q)) // skip completed
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }

    public String completeQuest(UUID questId, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Quest quest = questRepo.findById(questId).orElseThrow();
        if (LocalDateTime.now().isAfter(quest.getExpiresAt())) {
            return "Quest expired.";
        }

        if (user.getCompletedQuests().contains(quest)) {
            return "Already completed.";
        }
        InventoryItem rewardItem = generateRandomReward(user);
        inventoryItemRepository.save(rewardItem);
        user.completeQuest(quest);
        userRepo.save(user);
        userService.addExperience(user, quest.getExperienceReward());
        return "Quest completed. EXP +" + quest.getExperienceReward();
    }

    private InventoryItem generateRandomReward(User user) {
        Random rand = new Random();

        // Lighter or more utility-focused gear
        String[] itemNames = {
                "Leather Gloves", "Training Band", "Runner’s Shoes", "Focus Pendant",
                "Apprentice Cloak", "Wooden Buckler", "Scout Hood"
        };
        String[] slots = {"hands", "feet", "ring", "neck", "chest", "head"};

        InventoryItem item = new InventoryItem();
        item.setUser(user);

        item.setName(itemNames[rand.nextInt(itemNames.length)]);
        item.setSlot(slots[rand.nextInt(slots.length)]);

        // Weighted rarity (more common than dungeon loot)
        int roll = rand.nextInt(100);
        InventoryRarity rarity;
        if (roll < 40) rarity = InventoryRarity.COMMON;
        else if (roll < 65) rarity = InventoryRarity.UNCOMMON;
        else if (roll < 85) rarity = InventoryRarity.RARE;
        else rarity = InventoryRarity.EPIC;

        item.setRarity(rarity);

        // Stat generation by rarity
        Stats stats = new Stats();
        switch (rarity) {
            case COMMON -> {
                stats.setStrength(1 + rand.nextInt(1));        // 0
                stats.setEndurance(1 + rand.nextInt(1));
            }
            case UNCOMMON -> {
                stats.setStrength(2 + rand.nextInt(2));    // 1–2
                stats.setEndurance(2 + rand.nextInt(2));
            }
            case RARE -> {
                stats.setStrength(3 + rand.nextInt(2));    // 2–3
                stats.setEndurance(3 + rand.nextInt(2));
            }
            case EPIC -> {
                stats.setStrength(4 + rand.nextInt(2));    // 4–5
                stats.setEndurance(4 + rand.nextInt(2));
            }
        }

        item.setStatBoosts(stats);
        return item;
    }

    public QuestDto createQuest(CreateQuestRequest req, UUID uuid) {
        Quest quest = new Quest();
        User user = userRepository.findById(uuid).orElseThrow();
        quest.setTitle(req.title);
        quest.setDescription(req.description);
        quest.setExperienceReward(req.experienceReward);
        quest.setDaily(req.daily);
        quest.setCreatedAt(LocalDateTime.now());
        if (req.daily) {
            quest.setExpiresAt(quest.getCreatedAt().plusDays(1));
        } else {
            quest.setExpiresAt(quest.getCreatedAt().plusWeeks(1));
        }

        questRepo.save(quest);
        return QuestMapper.toDto(quest, user); // pass null user to skip completed status
    }

    public Object getQuestHistory(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        LocalDateTime resetTime = LocalDateTime.now().minusDays(1);
        List<Quest> recentQuests = questRepo.findAllByCreatedAtAfter(resetTime);
        return recentQuests.stream()
                .filter(q -> user.getCompletedQuests().contains(q)) // skip completed
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }

    public List<QuestDto> getWeeklyQuests(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();

        List<Quest> weeklyQuests = questRepo.findAllByDailyFalse();

        return weeklyQuests.stream()
                .map(quest -> {
                    boolean completed = user.getCompletedQuests().contains(quest);
                    return QuestMapper.toDto(quest, completed);
                })
                .toList();
    }

}

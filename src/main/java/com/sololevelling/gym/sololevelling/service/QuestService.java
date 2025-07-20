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
import com.sololevelling.gym.sololevelling.util.exception.*;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ExperienceService experienceService;

    public List<QuestDto> getAvailableQuests(String email) {
        SoloLogger.info("🗺️ Fetching available quests for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Quest> quests = questRepo.findQuestsByUser_Id(user.getId()).stream()
                .filter(q -> !q.isCompleted())
                .toList();

        return quests.stream()
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }

    public String completeQuest(ObjectId questId, String email) {
        SoloLogger.info("✅ Attempting to complete quest {} for user {}", questId, email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        Quest quest = questRepo.findById(questId).orElseThrow(() -> new QuestNotFoundException("Quest not found"));

        if (!quest.getUser().equals(user)) {
            throw new AccessDeniedException("You don't own this Quest.");
        }
        if (LocalDateTime.now().isAfter(quest.getExpiresAt())) {
            throw new ExpireException("Quest has expired.");
        }

        if (user.getQuests().contains(quest) && quest.isCompleted()) {
            throw new TaskCompletedException("Quest Already completed.");
        }

        InventoryItem rewardItem = generateRandomReward(user);
        inventoryItemRepository.save(rewardItem);

        user.completeQuest(quest);
        List<InventoryItem> currentInventory = user.getInventory();
        if (currentInventory == null) {
            currentInventory = new ArrayList<>();
        }
        currentInventory.add(rewardItem);
        user.setInventory(currentInventory);
        quest.setCompleted(true);

        questRepo.save(quest);
        userRepo.save(user);
        experienceService.addExperience(user, quest.getExperienceReward());

        return "Quest completed. EXP +" + quest.getExperienceReward();
    }

    private InventoryItem generateRandomReward(User user) {
        Random rand = new Random();

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

    public QuestDto createQuest(CreateQuestRequest req, ObjectId uuid) {
        Quest quest = new Quest();
        SoloLogger.info("🛠️ Creating new quest for user {}", uuid);
        User user = userRepo.findById(uuid).orElseThrow(() -> new UserNotFoundException("User not found"));
        quest.setTitle(req.title);
        quest.setDescription(req.description);
        quest.setExperienceReward(req.experienceReward);
        quest.setCreatedAt(LocalDateTime.now());
        quest.setExpiresAt(quest.getCreatedAt().plusDays(1));
        quest.setUser(user);
        questRepo.save(quest);
        Quest saved = questRepo.save(quest);
        return QuestMapper.toDto(saved, user);
    }

    public List<QuestDto> getQuestHistory(String email) {
        SoloLogger.info("📜 Fetching quest history for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Quest> completedQuests = questRepo.findQuestsByUser_Id(user.getId()).stream()
                .filter(Quest::isCompleted)
                .toList();
        return completedQuests.stream()
                .map(q -> QuestMapper.toDto(q, user))
                .toList();
    }

    public List<QuestDto> getAllQuests() {
        SoloLogger.info("🌍 Fetching all quests");
        List<Quest> quests = questRepo.findAll();
        return quests.stream()
                .map(quest -> QuestMapper.toDto(quest, quest.getUser()))
                .toList();
    }


    public QuestDto getQuestById(ObjectId id) {
        SoloLogger.info("🔍 Fetching quest by ID: {}", id);
        Quest quest = questRepo.findById(id).orElseThrow(() -> new QuestNotFoundException("Quest not found"));
        return QuestMapper.toDto(quest, quest.getUser());
    }
}

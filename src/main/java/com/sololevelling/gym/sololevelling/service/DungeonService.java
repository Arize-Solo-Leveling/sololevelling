/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.InventoryItem;
import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonDto;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonMapper;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonRequest;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryRarity;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.InventoryItemRepository;
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
public class DungeonService {

    @Autowired
    private DungeonRepository dungeonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ExperienceService experienceService;


    public List<DungeonDto> getAvailableDungeons(String email) {
        SoloLogger.info("🏰 Fetching available dungeons for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found with email: {}", email);
            return new UserNotFoundException("User not found");
        });
        List<Dungeon> dungeons = dungeonRepository.findByUserAndCompletedFalse(user);
        SoloLogger.debug("Found {} available dungeons for user {}", dungeons.size(), email);
        return dungeons.stream().map(DungeonMapper::toDto).toList();
    }

    public DungeonDto attemptDungeon(ObjectId dungeonId, String email) throws AccessDeniedException, StatsLowException, DungeonNotFoundException {
        SoloLogger.info("⚔️ User {} attempting dungeon {}", email, dungeonId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found with email: {}", email);
            return new UserNotFoundException("User not found");
        });
        Dungeon dungeon = dungeonRepository.findById(dungeonId).orElseThrow(() -> {
            SoloLogger.error("❌ Dungeon not found with ID: {}", dungeonId);
            return new DungeonNotFoundException("Dungeon not found");
        });

        if (!dungeon.getUser().equals(user)) {
            SoloLogger.warn("🚫 Access denied - user {} doesn't own dungeon {}", email, dungeonId);
            throw new AccessDeniedException("You don't own this dungeon.");
        }

        if (dungeon.isCompleted()) {
            SoloLogger.warn("⚠️ Dungeon {} already completed by user {}", dungeonId, email);
            throw new TaskCompletedException("Dungeon already completed.");
        }

        // Fake stat-based calculation
        boolean success = user.getStats().getStrength() + user.getStats().getEndurance() >= 10;
        SoloLogger.debug("Dungeon attempt result - success: {}", success);
        if (success) {
            dungeon.setCompleted(true);
            InventoryItem reward = generateDungeonLoot(dungeon.getLootReward(), user);
            int bonusStatPoints = switch (reward.getRarity()) {
                case InventoryRarity.UNCOMMON -> 2;
                case InventoryRarity.RARE -> 4;
                case InventoryRarity.EPIC -> 7;
                case InventoryRarity.LEGENDARY -> 10;
                default -> 0;
            };
            SoloLogger.info("🎉 Dungeon completed! Reward: {} (Rarity: {})", reward.getName(), reward.getRarity());
            List<InventoryItem> currentInventory = user.getInventory();
            if (currentInventory == null) {
                currentInventory = new ArrayList<>();
            }
            currentInventory.add(reward);
            user.setInventory(currentInventory);
            user.setStatPoints(user.getStatPoints() + bonusStatPoints);
            inventoryItemRepository.save(reward);
            dungeonRepository.save(dungeon);
            userRepository.save(user);
            experienceService.addExperience(user, dungeon.getExpReward());
            SoloLogger.debug("💾 Saved dungeon completion and rewards for user {}", email);
            return DungeonMapper.toDto(dungeon);
        } else {
            SoloLogger.warn("❌ Dungeon attempt failed - stats too low for user {}", email);
            throw new StatsLowException("your stats is low");
        }
    }

    public DungeonDto createDungeonForUser(DungeonRequest request, ObjectId uuid) {
        SoloLogger.info("🛠️ Creating new dungeon for user {}", uuid);
        User user = userRepository.findById(uuid).orElseThrow(() -> {
            SoloLogger.error("User not found: {}", uuid);
            return new UserNotFoundException("User not found");
        });

        Dungeon dungeon = new Dungeon();
        dungeon.setName(request.getName());
        dungeon.setType(request.getType());
        dungeon.setObjective(request.getObjective());
        dungeon.setExpReward(request.getExpReward());
        dungeon.setLootReward(request.getLootReward());
        dungeon.setCompleted(false);
        dungeon.setCreatedAt(LocalDateTime.now());
        dungeon.setExpiresAt(dungeon.getCreatedAt().plusWeeks(1));
        dungeon.setUser(user);

        Dungeon savedDungeon = dungeonRepository.save(dungeon);
        SoloLogger.debug("Created dungeon {} for user {}", savedDungeon.getId(), uuid);
        return DungeonMapper.toDto(savedDungeon);    }

    public List<DungeonDto> getDungeonHistory(String email) {
        SoloLogger.info("📜 Fetching dungeon history for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found with email: {}", email);
            return new UserNotFoundException("User not found");
        });
        List<Dungeon> dungeons = dungeonRepository.findByUser(user);
        List<Dungeon> completedDungeons = dungeons.stream().filter(Dungeon::isCompleted).toList();
        SoloLogger.debug("Found {} completed dungeons for user {}", completedDungeons.size(), email);
        return completedDungeons.stream().map(DungeonMapper::toDto).toList();
    }

    private InventoryItem generateDungeonLoot(String lootHint, User user) {
        SoloLogger.debug("🎁 Generating dungeon loot for user {}", user.getEmail());
        Random rand = new Random();

        // Example item pools
        String[] itemNames = {
                "Steel Gauntlets", "Mystic Helm", "Shadow Cloak", "Dragon Armor",
                "Boots of Agility", "Iron Pauldrons", "Arcane Ring", "Blade of Trials"
        };
        String[] slots = {"head", "chest", "legs", "hands", "feet", "ring", "weapon"};

        // Create item
        InventoryItem item = new InventoryItem();
        item.setUser(user);

        // Random values
        item.setName(lootHint != null ? lootHint : itemNames[rand.nextInt(itemNames.length)]);
        item.setSlot(slots[rand.nextInt(slots.length)]);

        // Assign rarity with weight (Common more likely than Legendary)
        int roll = rand.nextInt(100);
        InventoryRarity rarity;
        if (roll < 40) rarity = InventoryRarity.COMMON;
        else if (roll < 65) rarity = InventoryRarity.UNCOMMON;
        else if (roll < 85) rarity = InventoryRarity.RARE;
        else if (roll < 95) rarity = InventoryRarity.EPIC;
        else rarity = InventoryRarity.LEGENDARY;

        item.setRarity(rarity);

        // Generate stat bonuses based on rarity
        Stats stats = new Stats();
        switch (rarity) {
            case InventoryRarity.COMMON -> {
                stats.setStrength(rand.nextInt(2));     // 0–1
                stats.setEndurance(rand.nextInt(2));
            }
            case InventoryRarity.UNCOMMON -> {
                stats.setStrength(1 + rand.nextInt(2)); // 1–2
                stats.setEndurance(1 + rand.nextInt(2));
            }
            case InventoryRarity.RARE -> {
                stats.setStrength(2 + rand.nextInt(3)); // 2–4
                stats.setEndurance(2 + rand.nextInt(3));
            }
            case InventoryRarity.EPIC -> {
                stats.setStrength(4 + rand.nextInt(3)); // 4–6
                stats.setEndurance(4 + rand.nextInt(3));
            }
            case InventoryRarity.LEGENDARY -> {
                stats.setStrength(7 + rand.nextInt(4)); // 7–10
                stats.setEndurance(7 + rand.nextInt(4));
            }
        }

        item.setStatBoosts(stats);
        SoloLogger.debug("Generated loot: {} (Rarity: {})", item.getName(), item.getRarity());
        return item;
    }

    public List<DungeonDto> getAllDungeons() {
        SoloLogger.info("🌍 Fetching all dungeons");
        List<Dungeon> dungeons = dungeonRepository.findAll();
        SoloLogger.debug("Found {} dungeons total", dungeons.size());
        return dungeons.stream().map(DungeonMapper::toDto).toList();
    }

    public DungeonDto getDungeonById(ObjectId id) throws DungeonNotFoundException {
        SoloLogger.info("🔍 Fetching dungeon by ID: {}", id);
        Dungeon dungeon = dungeonRepository.findById(id)
                .orElseThrow(() -> {
                    SoloLogger.error("Dungeon not found: {}", id);
                    return new DungeonNotFoundException("Dungeon not found");
                });
        return DungeonMapper.toDto(dungeon);
    }
}

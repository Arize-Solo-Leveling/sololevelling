/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.InventoryItem;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryItemDto;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryItemMapper;
import com.sololevelling.gym.sololevelling.repo.InventoryItemRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.exception.ItemNotFoundException;
import com.sololevelling.gym.sololevelling.util.exception.ItemNotOwnedException;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InventoryItemRepository itemRepository;

    public List<InventoryItemDto> getInventory(String email) {
        SoloLogger.info("🎒 Fetching inventory for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        return itemRepository.findByUser(user).stream()
                .map(InventoryItemMapper::toDto)
                .toList();
    }

    public String equipItem(String email, ObjectId itemId) {
        SoloLogger.info("⚔️ User {} attempting to equip item {}", email, itemId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        InventoryItem item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new ItemNotOwnedException("You do not own this item.");
        }

        // Unequip currently equipped item in the same slot
        user.getInventory().stream()
                .filter(i -> i.getSlot().equals(item.getSlot()) && i.isEquipped())
                .forEach(i -> {
                    i.setEquipped(false);
                    user.subtractStats(i.getStatBoosts());
                });

        item.setEquipped(true);
        user.addStats(item.getStatBoosts());

        itemRepository.save(item);
        userRepository.save(user);
        return "Item equipped!";
    }

    public String unequipItem(String email, ObjectId itemId) {
        SoloLogger.info("🛡️ User {} attempting to unequip item {}", email, itemId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        InventoryItem item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.getUser().getId().equals(user.getId()) || !item.isEquipped()) {
            throw new ItemNotOwnedException("Item not equipped or not owned.");
        }

        item.setEquipped(false);
        user.subtractStats(item.getStatBoosts());
        itemRepository.save(item);
        userRepository.save(user);
        return "Item unequipped.";
    }
}

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
        User user = userRepository.findByEmail(email).orElseThrow();
        return itemRepository.findByUser(user).stream()
                .map(InventoryItemMapper::toDto)
                .toList();
    }

    public String equipItem(String email, Long itemId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        InventoryItem item = itemRepository.findById(itemId).orElseThrow();

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not own this item.");
        }

        // Unequip currently equipped item in the same slot
        for (InventoryItem i : user.getInventory()) {
            if (i.getSlot().equals(item.getSlot()) && i.isEquipped()) {
                i.setEquipped(false);
                user.subtractStats(i.getStatBoosts());
            }
        }

        // Equip the new item
        item.setEquipped(true);
        user.addStats(item.getStatBoosts());

        userRepository.save(user);
        return "Item equipped!";
    }

    public String unequipItem(String email, Long itemId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        InventoryItem item = itemRepository.findById(itemId).orElseThrow();

        if (!item.getUser().getId().equals(user.getId()) || !item.isEquipped()) {
            throw new RuntimeException("Item not equipped or not owned.");
        }

        item.setEquipped(false);
        user.subtractStats(item.getStatBoosts());
        userRepository.save(user);
        return "Item unequipped.";
    }
}

package com.sololevelling.gym.sololevelling.model.dto.inventory;

import com.sololevelling.gym.sololevelling.model.Stats;

public class InventoryItemDto {
    public Long id;
    public String name;
    public InventoryRarity rarity;
    public String slot;
    public boolean equipped;
    public Stats statBoosts;
}

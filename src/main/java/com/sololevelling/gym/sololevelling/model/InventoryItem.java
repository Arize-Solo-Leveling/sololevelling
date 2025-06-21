package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sololevelling.gym.sololevelling.model.dto.inventory.InventoryRarity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class InventoryItem {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private InventoryRarity rarity;
    private String slot; // head, hands, etc.

    @Embedded
    private Stats statBoosts;

    private boolean equipped = false;

    @ManyToOne
    @JsonIgnore
    private User user;
}

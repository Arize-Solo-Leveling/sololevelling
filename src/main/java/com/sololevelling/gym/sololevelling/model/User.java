/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String email;
    private String password;

    private Integer level = 1;
    private Integer experience = 0;
    private Integer statPoints = 0;

    @Embedded
    private Stats stats;

    @Enumerated(EnumType.STRING)
    private UserClass userClass;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Quest> quests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Workout> workouts = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InventoryItem> inventory = new ArrayList<>();

    public void completeQuest(Quest quest) {
        if (!this.quests.contains(quest)) {
            this.quests.add(quest);
        }
    }

    public void addStats(Stats stats) {
        this.stats.setStrength(this.stats.getStrength() + stats.getStrength());
        this.stats.setEndurance(this.stats.getEndurance() + stats.getEndurance());
        this.stats.setAgility(this.stats.getAgility() + stats.getAgility());
        this.stats.setIntelligence(this.stats.getIntelligence() + stats.getIntelligence());
        this.stats.setLuck(this.stats.getLuck() + stats.getLuck());
    }

    public void subtractStats(Stats stats) {
        this.stats.setStrength(this.stats.getStrength() - stats.getStrength());
        this.stats.setEndurance(this.stats.getEndurance() - stats.getEndurance());
        this.stats.setAgility(this.stats.getAgility() - stats.getAgility());
        this.stats.setIntelligence(this.stats.getIntelligence() - stats.getIntelligence());
        this.stats.setLuck(this.stats.getLuck() - stats.getLuck());
    }

}

/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document
@Data
@EqualsAndHashCode(exclude = {"quests", "workouts", "inventory", "roles"})
@ToString(exclude = {"quests", "workouts", "inventory", "roles"})
public class User {
    @Id
    private ObjectId id;

    private String name;
    private String email;
    private String password;

    private Integer level = 1;
    private Integer experience = 0;
    private Integer statPoints = 0;

    private Stats stats;
    private UserClass userClass;

    @DBRef
    private Set<Role> roles;

    @DBRef
    private List<Quest> quests = new ArrayList<>();

    @DBRef
    private List<Workout> workouts = new ArrayList<>();

    @DBRef
    private List<InventoryItem> inventory = new ArrayList<>();

    private LocalDateTime lastLogout;
    private int failedLoginAttempts = 0;
    private LocalDateTime lockoutUntil;

    public boolean isLocked() {
        return lockoutUntil != null && lockoutUntil.isAfter(LocalDateTime.now());
    }

    public void incrementFailedAttempts() {
        failedLoginAttempts++;
        if (failedLoginAttempts >= 5) {
            lockoutUntil = LocalDateTime.now().plusMinutes(5);
        }
    }

    public void resetFailedAttempts() {
        failedLoginAttempts = 0;
        lockoutUntil = null;
    }

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

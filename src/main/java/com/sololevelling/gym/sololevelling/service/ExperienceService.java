/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.mongodb.lang.NonNull;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.springframework.stereotype.Service;

@Service
public class ExperienceService {

    private final UserRepository userRepo;

    public ExperienceService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void addExperience(User user, int expGained) {
        SoloLogger.info("✨ Adding {} EXP to user {}", expGained, user.getEmail());
        int exp = user.getExperience() + expGained;
        user.setExperience(exp);

        int level = user.getLevel();
        int statPoints = user.getStatPoints();

        int maxLevel = 100;

        while (level < maxLevel) {
            int requiredExp = getExpForNextLevel(level);
            if (exp < requiredExp) break;

            exp -= requiredExp;
            level++;
            statPoints += 5;
        }
        user.setLevel(level);
        user.setStatPoints(statPoints);
        userRepo.save(user);
    }


    public int getExpForNextLevel(int level) {
        int exp = 100;
        for (int i = 1; i < level; i++) {
            int growthRate;
            if (i <= 5) growthRate = 50;
            else if (i <= 10) growthRate = 45;
            else if (i <= 20) growthRate = 40;
            else if (i <= 30) growthRate = 30;
            else if (i <= 40) growthRate = 20;
            else if (i <= 50) growthRate = 10;
            else growthRate = 5;

            exp += (exp * growthRate / 100);
        }
        return exp;
    }


    public int getTotalExperienceForLevel(int level) {
        int total = 0;
        for (int i = 1; i < level; i++) {
            total += getExpForNextLevel(i);
        }
        return total;
    }


    public ExperienceProgress getExperienceProgress(User user) {
        int level = user.getLevel();
        int exp = user.getExperience();

        int totalExp = getTotalExperienceForLevel(level) + exp;
        int expNeeded = getExpForNextLevel(level);

        return new ExperienceProgress(totalExp, exp, expNeeded);
    }

    public record ExperienceProgress(int totalExpEarned, int currentLevelExp, int expForNextLevel) {

        @Override
        @NonNull
        public String toString() {
            return "Total EXP Earned: " + totalExpEarned +
                    ", Current EXP: " + currentLevelExp +
                    ", EXP Needed for Next Level: " + expForNextLevel;
        }
    }
}

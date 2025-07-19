/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.leaderboard.LeaderboardEntryDto;
import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public List<LeaderboardEntryDto> getGlobalLeaderboard() {
        SoloLogger.info("🏆 Fetching global leaderboard");
        List<User> topUsers = userRepository.findTop10ByOrderByLevelDescExperienceDesc();

        List<LeaderboardEntryDto> leaderboard = topUsers.stream()
                .filter(user -> !"Admin".equals(user.getName()))
                .map(this::mapToDto)
                .toList();

        SoloLogger.debug("🌍 Global leaderboard contains {} entries (excluding admin)", leaderboard.size());
        return leaderboard;
    }

    public List<LeaderboardEntryDto> getClassLeaderboard(UserClass userClass) {
        SoloLogger.info("🥇 Fetching {} class leaderboard", userClass);
        List<User> classUsers = userRepository.findTop10ByUserClassOrderByLevelDescExperienceDesc(userClass);

        List<LeaderboardEntryDto> leaderboard = classUsers.stream()
                .filter(user -> !"Admin".equals(user.getName()))
                .map(this::mapToDto)
                .toList();

        SoloLogger.debug("👥 {} class leaderboard contains {} entries (excluding admin)",
                userClass, leaderboard.size());
        return leaderboard;
    }

    private LeaderboardEntryDto mapToDto(User user) {
        LeaderboardEntryDto dto = new LeaderboardEntryDto();
        dto.username = user.getName();
        dto.level = user.getLevel();
        dto.experience = user.getExperience();
        dto.userClass = String.valueOf(user.getUserClass());
        return dto;
    }
}

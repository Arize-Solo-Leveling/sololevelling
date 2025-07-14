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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public List<LeaderboardEntryDto> getGlobalLeaderboard() {
        return userRepository.findTop10ByOrderByLevelDescExperienceDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<LeaderboardEntryDto> getClassLeaderboard(UserClass userClass) {
        return userRepository.findTop10ByUserClassOrderByLevelDescExperienceDesc(userClass)
                .stream()
                .map(this::mapToDto)
                .toList();
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

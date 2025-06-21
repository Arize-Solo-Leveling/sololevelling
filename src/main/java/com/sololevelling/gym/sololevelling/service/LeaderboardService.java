package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.leaderboard.LeaderboardEntryDto;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    public List<LeaderboardEntryDto> getGlobalLeaderboard() {
        return userRepository.findTopGlobalLeaderboard()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<LeaderboardEntryDto> getClassLeaderboard(String characterClass) {
        return userRepository.findTopLeaderboardByClass(characterClass)
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

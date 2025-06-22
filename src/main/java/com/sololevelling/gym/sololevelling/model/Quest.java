package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Quest {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private int experienceReward;
    private boolean daily; // true = daily, false = weekly
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @ManyToMany(mappedBy = "completedQuests")
    @JsonIgnore
    private List<User> usersCompleted = new ArrayList<>();

    // Getters & Setters
}


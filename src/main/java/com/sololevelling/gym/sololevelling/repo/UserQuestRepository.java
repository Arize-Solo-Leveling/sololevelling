package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserQuestRepository extends JpaRepository<UserQuest, UUID> {
}
package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID> {
    List<Quest> findAllByCreatedAtAfter(LocalDateTime after);

    List<Quest> findAllByDailyFalse();
}

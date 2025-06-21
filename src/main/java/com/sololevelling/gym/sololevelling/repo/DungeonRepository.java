package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, Long> {
    List<Dungeon> findByUser(User user);

    List<Dungeon> findByUserAndCompletedFalse(User user);

    List<Dungeon> findByUserAndWeeklyTrue(User user);

}

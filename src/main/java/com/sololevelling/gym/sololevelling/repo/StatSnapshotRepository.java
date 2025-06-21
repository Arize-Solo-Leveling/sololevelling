package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.StatSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatSnapshotRepository extends JpaRepository<StatSnapshot, UUID> {
}

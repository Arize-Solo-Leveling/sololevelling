/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.level DESC, u.experience DESC")
    List<User> findTopGlobalLeaderboard();

    @Query("SELECT u FROM User u WHERE u.userClass = :characterClass ORDER BY u.level DESC, u.experience DESC")
    List<User> findTopLeaderboardByClass(@Param("characterClass") String characterClass);

}

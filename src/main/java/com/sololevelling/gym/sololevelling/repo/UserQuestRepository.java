/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.UserQuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserQuestRepository extends JpaRepository<UserQuest, UUID> {
}
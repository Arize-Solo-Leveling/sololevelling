/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.repo;

import com.sololevelling.gym.sololevelling.model.InventoryItem;
import com.sololevelling.gym.sololevelling.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends MongoRepository<InventoryItem, ObjectId> {
    List<InventoryItem> findByUser(User user);
}

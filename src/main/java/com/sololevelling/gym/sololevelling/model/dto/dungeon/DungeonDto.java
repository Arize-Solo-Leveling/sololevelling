/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.dungeon;

import org.bson.types.ObjectId;

public class DungeonDto {
    public ObjectId id;
    public String name;
    public String type;
    public String objective;
    public int expReward;
    public String lootReward;
    public boolean completed;
    public boolean isWeekly;
    public boolean expired;
}

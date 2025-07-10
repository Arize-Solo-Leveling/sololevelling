/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.dungeon;

import lombok.Data;

@Data
public class DungeonRequest {
    private String name;
    private String type;
    private String objective;
    private int expReward;
    private String lootReward;
    // Getters and Setters
}

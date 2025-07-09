/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.quest;

import java.util.UUID;

public class QuestDto {
    public UUID id;
    public String title;
    public String description;
    public int experienceReward;
    public boolean expired;
    public boolean completed;
}

/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.quest;

import org.bson.types.ObjectId;

public class QuestDto {
    public ObjectId id;
    public String title;
    public String description;
    public int experienceReward;
    public boolean daily;
    public boolean expired;
    public boolean completed;
}

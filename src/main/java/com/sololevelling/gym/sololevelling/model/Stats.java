/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Stats {
    private Integer strength = 0;
    private Integer endurance = 0;
    private Integer agility = 0;
    private Integer intelligence = 0;
    private Integer luck = 0;
}

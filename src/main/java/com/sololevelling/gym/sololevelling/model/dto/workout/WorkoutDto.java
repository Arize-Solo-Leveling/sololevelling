/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WorkoutDto {
    public UUID id;
    public String name;
    public LocalDateTime date;
    public double totalVolume;
    public int experienceGained;
    public List<ExerciseDto> exercises;
}
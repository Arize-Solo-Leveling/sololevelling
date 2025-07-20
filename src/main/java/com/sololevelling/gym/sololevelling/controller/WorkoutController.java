/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutRequest;
import com.sololevelling.gym.sololevelling.service.WorkoutService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<?> submitWorkout(@RequestBody WorkoutRequest request, Principal principal) {
        return ResponseEntity.ok(workoutService.submitWorkout(request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<?> getWorkoutHistory(Principal principal) {
        return ResponseEntity.ok(workoutService.getWorkoutHistory(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkoutDetail(@PathVariable ObjectId id, Principal principal) {
        return ResponseEntity.ok(workoutService.getWorkoutDetail(id, principal.getName()));
    }
}
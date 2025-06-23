/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutRequest;
import com.sololevelling.gym.sololevelling.service.WorkoutService;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> submitWorkout(@RequestBody WorkoutRequest request, Principal principal) {
        return ResponseEntity.ok(workoutService.submitWorkout(request, principal.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWorkoutHistory(Principal principal) {
        return ResponseEntity.ok(workoutService.getWorkoutHistory(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWorkoutDetail(@PathVariable UUID id, Principal principal) throws AccessDeniedException {
        return ResponseEntity.ok(workoutService.getWorkoutDetail(id, principal.getName()));
    }
}
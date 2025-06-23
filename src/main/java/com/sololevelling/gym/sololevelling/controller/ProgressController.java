/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getStatProgress(Principal principal) {
        return ResponseEntity.ok(progressService.getStatProgress(principal.getName()));
    }

    @GetMapping("/workout-graph")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getWorkoutVolumeGraph(Principal principal) {
        return ResponseEntity.ok(progressService.getWorkoutGraph(principal.getName()));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProgressSummary(Principal principal) {
        return ResponseEntity.ok(progressService.getSummary(principal.getName()));
    }
}

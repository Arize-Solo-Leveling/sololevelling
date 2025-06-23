/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.leaderboard.LeaderboardEntryDto;
import com.sololevelling.gym.sololevelling.model.dto.user.UserClass;
import com.sololevelling.gym.sololevelling.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/global")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LeaderboardEntryDto>> getGlobalLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard());
    }

    @GetMapping("/{className}/class")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<LeaderboardEntryDto>> getClassLeaderboard(@PathVariable UserClass className) {
        return ResponseEntity.ok(leaderboardService.getClassLeaderboard(className));
    }
}

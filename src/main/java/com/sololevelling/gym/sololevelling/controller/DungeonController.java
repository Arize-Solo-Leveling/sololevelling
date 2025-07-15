/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.service.DungeonService;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import com.sololevelling.gym.sololevelling.util.StatsLowException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/dungeons")
public class DungeonController {

    @Autowired
    private DungeonService dungeonService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private DungeonRepository dungeonRepo;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAvailableDungeons(Principal principal) {
        return ResponseEntity.ok(dungeonService.getAvailableDungeons(principal.getName()));
    }

    @PostMapping("/{dungeonId}/attempt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> attemptDungeon(@PathVariable ObjectId dungeonId, Principal principal) throws AccessDeniedException, StatsLowException {
        return ResponseEntity.ok(dungeonService.attemptDungeon(dungeonId, principal.getName()));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDungeonHistory(Principal principal) {
        return ResponseEntity.ok(dungeonService.getDungeonHistory(principal.getName()));
    }

}

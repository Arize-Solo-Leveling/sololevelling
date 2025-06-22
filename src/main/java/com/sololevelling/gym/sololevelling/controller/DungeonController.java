/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonRequest;
import com.sololevelling.gym.sololevelling.repo.DungeonRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.service.DungeonService;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import com.sololevelling.gym.sololevelling.util.StatsLowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getAvailableDungeons(Principal principal) {
        return ResponseEntity.ok(dungeonService.getAvailableDungeons(principal.getName()));
    }

    @PostMapping("/attempt")
    public ResponseEntity<?> attemptDungeon(@RequestParam Long dungeonId, Principal principal) throws AccessDeniedException, StatsLowException {
        return ResponseEntity.ok(dungeonService.attemptDungeon(dungeonId, principal.getName()));
    }

    @GetMapping("/week")
    public ResponseEntity<?> getWeeklyDungeons(Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElseThrow();
        List<Dungeon> weekly = dungeonRepo.findByUserAndWeeklyTrue(user);
        return ResponseEntity.ok(weekly);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getDungeonHistory(Principal principal) {
        return ResponseEntity.ok(dungeonService.getDungeonHistory(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<?> createDungeon(@RequestBody DungeonRequest request, Principal principal) {
        Dungeon dungeon = dungeonService.createDungeonForUser(principal.getName(), request);
        return ResponseEntity.ok(dungeon);
    }

}

/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.service.DungeonService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/dungeons")
public class DungeonController {

    @Autowired
    private DungeonService dungeonService;

    @GetMapping
    public ResponseEntity<?> getAvailableDungeons(Principal principal) {
        return ResponseEntity.ok(dungeonService.getAvailableDungeons(principal.getName()));
    }

    @PostMapping("/{dungeonId}/attempt")
    public ResponseEntity<?> attemptDungeon(@PathVariable ObjectId dungeonId, Principal principal) {
        return ResponseEntity.ok(dungeonService.attemptDungeon(dungeonId, principal.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getDungeonHistory(Principal principal) {
        return ResponseEntity.ok(dungeonService.getDungeonHistory(principal.getName()));
    }

}

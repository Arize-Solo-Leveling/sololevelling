/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.service.QuestService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/quests")
public class QuestController {

    @Autowired
    private QuestService questService;

    @GetMapping
    public ResponseEntity<?> getAvailableQuests(Principal principal) {
        return ResponseEntity.ok(questService.getAvailableQuests(principal.getName()));
    }

    @PostMapping("/{questId}/complete")
    public ResponseEntity<?> completeQuest(@PathVariable ObjectId questId, Principal principal) {
        return ResponseEntity.ok(questService.completeQuest(questId, principal.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getQuestHistory(Principal principal) {
        return ResponseEntity.ok(questService.getQuestHistory(principal.getName()));
    }
}

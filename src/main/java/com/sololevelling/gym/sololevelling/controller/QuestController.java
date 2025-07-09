/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.quest.QuestDto;
import com.sololevelling.gym.sololevelling.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quests")
public class QuestController {

    @Autowired
    private QuestService questService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAvailableQuests(Principal principal) {
        return ResponseEntity.ok(questService.getAvailableQuests(principal.getName()));
    }

    @PostMapping("/{questId}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> completeQuest(@PathVariable UUID questId, Principal principal) {
        return ResponseEntity.ok(questService.completeQuest(questId, principal.getName()));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getQuestHistory(Principal principal) {
        return ResponseEntity.ok(questService.getQuestHistory(principal.getName()));
    }
}

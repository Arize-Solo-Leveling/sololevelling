/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.quest.CreateQuestRequest;
import com.sololevelling.gym.sololevelling.model.dto.quest.QuestDto;
import com.sololevelling.gym.sololevelling.service.QuestGenerator;
import com.sololevelling.gym.sololevelling.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quests")
public class QuestController {

    @Autowired
    private QuestService questService;
    @Autowired
    private QuestGenerator questGenerator;

    @GetMapping
    public ResponseEntity<?> getQuests(Principal principal) {
        return ResponseEntity.ok(questService.getAvailableQuests(principal.getName()));
    }

    @PostMapping("/{questId}/complete")
    public ResponseEntity<?> completeQuest(@PathVariable UUID questId, Principal principal) {
        return ResponseEntity.ok(questService.completeQuest(questId, principal.getName()));
    }

    @GetMapping("/week")
    public ResponseEntity<?> getWeeklyQuests(Principal principal) {
        List<QuestDto> weekly = questService.getWeeklyQuests(principal.getName());
        return ResponseEntity.ok(weekly);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getQuestHistory(Principal principal) {
        return ResponseEntity.ok(questService.getQuestHistory(principal.getName()));
    }

    @PostMapping("/{userId}/create")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuest(@RequestBody CreateQuestRequest request, @PathVariable UUID userId) {
        return ResponseEntity.ok(questService.createQuest(request, userId));
    }

    @PostMapping("/{userId}/generate")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRandomQuest(@PathVariable UUID userId) {
        return ResponseEntity.ok(questGenerator.pickRandomDailyQuests(userId));
    }

}

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.model.dto.quest.CreateQuestRequest;
import com.sololevelling.gym.sololevelling.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/quests")
public class QuestController {

    @Autowired
    private QuestService questService;

    @GetMapping
    public ResponseEntity<?> getQuests(Principal principal) {
        return ResponseEntity.ok(questService.getAvailableQuests(principal.getName()));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completeQuest(@RequestParam UUID questId, Principal principal) {
        return ResponseEntity.ok(questService.completeQuest(questId, principal.getName()));
    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuest(@RequestBody CreateQuestRequest request) {
        return ResponseEntity.ok(questService.createQuest(request));
    }

}

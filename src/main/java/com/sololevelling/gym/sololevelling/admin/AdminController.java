package com.sololevelling.gym.sololevelling.admin;

import com.sololevelling.gym.sololevelling.model.Dungeon;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.admin.RoleUpdateRequest;
import com.sololevelling.gym.sololevelling.model.dto.admin.StatsResponse;
import com.sololevelling.gym.sololevelling.model.dto.dungeon.DungeonRequest;
import com.sololevelling.gym.sololevelling.model.dto.quest.CreateQuestRequest;
import com.sololevelling.gym.sololevelling.service.*;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import com.sololevelling.gym.sololevelling.util.DungeonNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private DungeonService dungeonService;
    @Autowired
    private WeeklyDungeonService weeklyDungeonService;
    @Autowired
    private QuestService questService;
    @Autowired
    private QuestGenerator questGenerator;

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable ObjectId userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @PutMapping("/user/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable ObjectId userId, @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(userId, request.getRole()));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(adminService.getAllRoles());
    }

    @GetMapping("/dungeon")
    public ResponseEntity<List<Dungeon>> getAllDungeons() {
        return ResponseEntity.ok(dungeonService.getAllDungeons());
    }

    @GetMapping("/dungeon/{id}")
    public ResponseEntity<Dungeon> getDungeonById(@PathVariable ObjectId id) throws DungeonNotFoundException {
        return ResponseEntity.ok(dungeonService.getDungeonById(id));
    }

    @PostMapping("/dungeon/{userId}")
    public ResponseEntity<Dungeon> createDungeonForUser(@PathVariable ObjectId userId,
                                                        @RequestBody DungeonRequest request) {
        return ResponseEntity.ok(dungeonService.createDungeonForUser(request, userId));
    }

    @PostMapping("/dungeon/{userId}/generate")
    public ResponseEntity<?> generateRandomDungeon(@PathVariable ObjectId userId) {
        return ResponseEntity.ok(weeklyDungeonService.pickRandomWeeklyDungeon(userId));
    }

    @DeleteMapping("/dungeon/{id}")
    public ResponseEntity<Void> deleteDungeon(@PathVariable ObjectId id) {
        dungeonService.deleteDungeonById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quests")
    public ResponseEntity<?> getAllQuests() {
        return ResponseEntity.ok(questService.getAllQuests());
    }

    @GetMapping("/quests/{id}")
    public ResponseEntity<?> getQuestById(@PathVariable ObjectId id) {
        return ResponseEntity.ok(questService.getQuestById(id));
    }

    @PostMapping("/quests/{userId}")
    public ResponseEntity<?> createQuest(@RequestBody CreateQuestRequest request, @PathVariable ObjectId userId) {
        return ResponseEntity.ok(questService.createQuest(request, userId));
    }

    @PostMapping("/quests/{userId}/generate")
    public ResponseEntity<?> generateRandomQuests(@PathVariable ObjectId userId) {
        return ResponseEntity.ok(questGenerator.pickRandomDailyQuests(userId));
    }

    @DeleteMapping("/quests/{id}")
    public ResponseEntity<?> deleteQuest(@PathVariable ObjectId id) throws Exception {
        questService.deleteQuest(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/workout")
    public ResponseEntity<?> getAllWorkouts() {
        return ResponseEntity.ok(adminService.getAllWorkouts());
    }

    @GetMapping("/workout/{id}")
    public ResponseEntity<?> getWorkoutDetail(@PathVariable ObjectId id) throws AccessDeniedException {
        return ResponseEntity.ok(adminService.getWorkoutDetail(id));
    }
}

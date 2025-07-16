/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.*;
import com.sololevelling.gym.sololevelling.model.dto.admin.StatsResponse;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutDto;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutMapper;
import com.sololevelling.gym.sololevelling.repo.*;
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private QuestRepository questRepo;
    @Autowired
    private DungeonRepository dungeonRepo;
    @Autowired
    private WorkoutRepository workoutRepo;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private StatSnapshotRepository statSnapshotRepository;
    @Autowired
    private ExerciseRepository exerciseRepo;

    public StatsResponse getDashboardStats() {
        long users = userRepo.count();
        long quests = questRepo.count();
        long dungeons = dungeonRepo.count();
        long workouts = workoutRepo.count();
        long inventory = inventoryItemRepository.count();
        return new StatsResponse(users, quests, dungeons, workouts, inventory);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUserById(ObjectId userId) {
        return userRepo.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );
    }

    public User updateUserRole(ObjectId userId, String newRoleName) {
        User user = getUserById(userId);

        // Prefix check (Spring Security requires "ROLE_" format)
        if (!newRoleName.startsWith("ROLE_")) {
            newRoleName = "ROLE_" + newRoleName;
        }

        String finalNewRoleName = newRoleName;
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + finalNewRoleName));

        // Assign the new role
        user.getRoles().add(newRole);

        return userRepo.save(user);
    }

    @Transactional
    public void deleteUser(ObjectId userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Quest> quests = questRepo.findQuestsByUser_Id(userId);
        questRepo.deleteAll(quests);

        List<Workout> workouts = workoutRepo.findWorkoutsByUser_Id(userId);
        for (Workout workout : workouts) {
            List<Exercise> exercises = exerciseRepo.findExercisesByWorkout_Id(workout.getId());
            exerciseRepo.deleteAll(exercises);
        }
        workoutRepo.deleteAll(workouts);


        List<InventoryItem> items = inventoryItemRepository.findInventoryItemsByUser_Id(userId);
        inventoryItemRepository.deleteAll(items);

        List<Dungeon> dungeons = dungeonRepo.findDungeonsByUser_Id(userId);
        dungeonRepo.deleteAll(dungeons);

        accessTokenRepository.deleteByUser(user);
        refreshTokenRepository.deleteByUser(user);

        List<StatSnapshot> statSnapshots = statSnapshotRepository.findStatSnapshotsByUser_Id(userId);
        statSnapshotRepository.deleteAll(statSnapshots);

        userRepo.delete(user);
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public WorkoutDto getWorkoutDetail(ObjectId workoutId) throws AccessDeniedException {
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new AccessDeniedException("Workout not found"));
        return WorkoutMapper.toDto(workout);
    }

    public List<WorkoutDto> getAllWorkouts() {
        return workoutRepo.findAll()
                .stream()
                .map(WorkoutMapper::toDto)
                .collect(Collectors.toList());
    }
}

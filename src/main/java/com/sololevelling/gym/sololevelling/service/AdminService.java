/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.*;
import com.sololevelling.gym.sololevelling.model.dto.admin.StatsResponse;
import com.sololevelling.gym.sololevelling.model.dto.user.UserDto;
import com.sololevelling.gym.sololevelling.model.dto.user.UserMapper;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutDto;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutMapper;
import com.sololevelling.gym.sololevelling.repo.*;
import com.sololevelling.gym.sololevelling.util.exception.RoleNotFoundException;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import com.sololevelling.gym.sololevelling.util.exception.WorkoutNotFoundException;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        SoloLogger.info("📊 Admin accessing dashboard statistics");
        long users = userRepo.count();
        long quests = questRepo.count();
        long dungeons = dungeonRepo.count();
        long workouts = workoutRepo.count();
        long inventory = inventoryItemRepository.count();
        return new StatsResponse(users, quests, dungeons, workouts, inventory);
    }

    public List<UserDto> getAllUsers() {
        SoloLogger.info("👥 Admin fetching all users");
        return userRepo.findAll().stream()
                .filter(user -> !"Admin".equals(user.getName()))
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(ObjectId userId) {
        SoloLogger.info("🔍 Admin fetching user by ID: {}", userId);
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    public UserDto updateUserRole(ObjectId userId, String newRoleName) {
        SoloLogger.info("🔄 Admin updating role for user: {} to {}", userId, newRoleName);
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!newRoleName.startsWith("ROLE_")) {
            newRoleName = "ROLE_" + newRoleName;
        }

        String finalNewRoleName = newRoleName;
        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + finalNewRoleName));

        user.getRoles().add(newRole);
        userRepo.save(user);
        return UserMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(ObjectId userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        SoloLogger.info("🧹 Cleaning up user data for: {}", userId);

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
        SoloLogger.info("👑 Admin fetching all roles");
        return roleRepository.findAll();
    }

    public WorkoutDto getWorkoutDetail(ObjectId workoutId) {
        SoloLogger.info("🏋️ Admin accessing workout details: {}", workoutId);
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found"));
        return WorkoutMapper.toDto(workout);
    }

    public List<WorkoutDto> getAllWorkouts() {
        SoloLogger.info("📅 Admin fetching all workouts");
        return workoutRepo.findAll()
                .stream()
                .map(WorkoutMapper::toDto)
                .collect(Collectors.toList());
    }
}

/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.model.Exercise;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.Workout;
import com.sololevelling.gym.sololevelling.model.dto.workout.ExerciseRequest;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutDto;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutMapper;
import com.sololevelling.gym.sololevelling.model.dto.workout.WorkoutRequest;
import com.sololevelling.gym.sololevelling.repo.ExerciseRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.repo.WorkoutRepository;
import com.sololevelling.gym.sololevelling.util.exception.AccessDeniedException;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import com.sololevelling.gym.sololevelling.util.exception.UserNotFoundException;
import com.sololevelling.gym.sololevelling.util.exception.WorkoutNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkoutRepository workoutRepo;
    @Autowired
    private ExerciseRepository exerciseRepo;

    public WorkoutDto submitWorkout(WorkoutRequest request, String email) {
        SoloLogger.info("🏋️ Submitting workout for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", email);
            return new UserNotFoundException("User not found");
        });

        Workout workout = new Workout();
        workout.setName(request.name);
        workout.setUser(user);
        workout.setDate(LocalDateTime.now());
        workout.setTotalVolume(0);
        workout.setExperienceGained(0);
        workout.setExercises(new ArrayList<>());

        Workout savedWorkout = workoutRepo.save(workout);
        SoloLogger.debug("Created base workout {} for user {}", savedWorkout.getId(), email);

        double totalVolume = 0;
        List<Exercise> exercises = new ArrayList<>();

        for (ExerciseRequest e : request.exercises) {
            double volume = e.sets * e.reps * e.weight;
            totalVolume += volume;

            Exercise ex = new Exercise();
            ex.setName(e.name);
            ex.setSets(e.sets);
            ex.setReps(e.reps);
            ex.setWeight(e.weight);
            ex.setWorkout(savedWorkout);
            exercises.add(ex);
        }

        List<Exercise> savedExercises = exerciseRepo.saveAll(exercises);
        SoloLogger.debug("Saved {} exercises for workout {}", savedExercises.size(), savedWorkout.getId());

        savedWorkout.setExercises(savedExercises);
        savedWorkout.setTotalVolume(totalVolume);
        int xp = (int) (totalVolume / 50);
        savedWorkout.setExperienceGained(xp);
        workoutRepo.save(savedWorkout);

        user.setExperience(user.getExperience() + xp);
        List<Workout> workouts = user.getWorkouts();
        if (workouts == null) {
            workouts = new ArrayList<>();
        }
        workouts.add(savedWorkout);
        user.setWorkouts(workouts);
        userRepository.save(user);

        SoloLogger.info("✅ Workout {} submitted successfully (Volume: {}, XP: {})",
                savedWorkout.getId(), totalVolume, xp);
        return WorkoutMapper.toDto(savedWorkout);
    }

    public List<WorkoutDto> getWorkoutHistory(String email) {
        SoloLogger.info("📅 Fetching workout history for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", email);
            return new UserNotFoundException("User not found");
        });

        List<Workout> workouts = workoutRepo.findByUser(user);
        SoloLogger.debug("Found {} workouts for user {}", workouts.size(), email);
        return workouts.stream().map(WorkoutMapper::toDto).toList();
    }

    public WorkoutDto getWorkoutDetail(ObjectId id, String email) throws AccessDeniedException {
        SoloLogger.info("🔍 Fetching workout details: {}", id);
        Workout workout = workoutRepo.findById(id).orElseThrow(() -> {
            SoloLogger.error("❌ Workout not found: {}", id);
            return new WorkoutNotFoundException("Workout not found");
        });

        if (!workout.getUser().getEmail().equals(email)) {
            SoloLogger.warn("🚫 Access denied - user {} doesn't own workout {}", email, id);
            throw new AccessDeniedException("Not your workout");
        }

        return WorkoutMapper.toDto(workout);
    }
}

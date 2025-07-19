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
import com.sololevelling.gym.sololevelling.util.AccessDeniedException;
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
        User user = userRepository.findByEmail(email).orElseThrow();

        Workout workout = new Workout();
        workout.setName(request.name);
        workout.setUser(user);
        workout.setDate(LocalDateTime.now());
        workout.setTotalVolume(0);
        workout.setExperienceGained(0);
        workout.setExercises(new ArrayList<>());
        workout = workoutRepo.save(workout);

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
            ex.setWorkout(workout);
            exercises.add(ex);
        }

        exerciseRepo.saveAll(exercises);

        workout.setExercises(exercises);
        workout.setTotalVolume(totalVolume);
        int xp = (int) (totalVolume / 50);
        workout.setExperienceGained(xp);
        workoutRepo.save(workout);

        user.setExperience(user.getExperience() + xp);
        List<Workout> workouts = user.getWorkouts();
        if (workouts == null) {
            workouts = new ArrayList<>();
        }
        workouts.add(workout);
        user.setWorkouts(workouts);
        userRepository.save(user);

        return WorkoutMapper.toDto(workout);
    }


    public List<WorkoutDto> getWorkoutHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return workoutRepo.findByUser(user).stream().map(WorkoutMapper::toDto).toList();
    }

    public WorkoutDto getWorkoutDetail(ObjectId id, String email) throws AccessDeniedException {
        Workout workout = workoutRepo.findById(id).orElseThrow();
        if (!workout.getUser().getEmail().equals(email)) throw new AccessDeniedException("Not your workout");
        return WorkoutMapper.toDto(workout);
    }
}

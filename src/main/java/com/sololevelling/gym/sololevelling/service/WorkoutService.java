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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        double totalVolume = 0;
        List<Exercise> exercises = new ArrayList<>();
        for (ExerciseRequest e : request.exercises) {
            double volume = e.sets * e.reps * e.weight;
            totalVolume += volume;
            Exercise ex = new Exercise(e.name, e.sets, e.reps, e.weight, workout);
            exercises.add(ex);
        }

        workout.setExercises(exercises);
        workout.setTotalVolume(totalVolume);
        workout.setExperienceGained((int) (totalVolume / 10));

        user.setExperience((int) (totalVolume / 10));

        workoutRepo.save(workout);
        exerciseRepo.saveAll(exercises);
        userRepository.save(user);

        return WorkoutMapper.toDto(workout);
    }

    public List<WorkoutDto> getWorkoutHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return workoutRepo.findByUser(user).stream().map(WorkoutMapper::toDto).toList();
    }

    public WorkoutDto getWorkoutDetail(UUID id, String email) throws AccessDeniedException {
        Workout workout = workoutRepo.findById(id).orElseThrow();
        if (!workout.getUser().getEmail().equals(email)) throw new AccessDeniedException("Not your workout");
        return WorkoutMapper.toDto(workout);
    }
}

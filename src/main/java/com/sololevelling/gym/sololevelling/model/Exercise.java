package com.sololevelling.gym.sololevelling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Exercise {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int sets;
    private int reps;
    private double weight;

    @ManyToOne
    @JsonIgnore
    private Workout workout;

    public Exercise(String name, int sets, int reps, double weight, Workout workout) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.workout = workout;
    }

    public Exercise() {

    }
}

package com.dreamteam.unikitchen.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer duration; // in Minuten

    @Column(nullable = false)
    private String difficultyLevel;

    @ElementCollection
    private List<String> ingredients; // Liste der Zutaten als String

    @Column(nullable = false)
    private String preparation;

    @Column(nullable = false)
    private String category;
}

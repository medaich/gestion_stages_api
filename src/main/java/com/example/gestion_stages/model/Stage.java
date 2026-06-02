package com.example.gestion_stages.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String sujet;

    LocalDate dateDebut;
    LocalDate dateFin;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String objectifs;

    @Column(columnDefinition = "TEXT")
    String solution;

    @Column(columnDefinition = "TEXT")
    String demarche;

    String outils;
}
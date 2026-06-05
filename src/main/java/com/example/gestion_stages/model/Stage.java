package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
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

    String status; // ACTIVE | COMPLETED | CANCELLED

    @ManyToOne
    private Etudiant etudiant;

    @ManyToOne
    private Entreprise entreprise;

    @ManyToOne
    private EncadrantAcademique encadrantAcademique;
}
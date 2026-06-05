package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    private String sujet;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(columnDefinition = "TEXT")
    private String solution;

    @Column(columnDefinition = "TEXT")
    private String demarche;

    private String outils;

    private String status =  "UNASSIGNED"; // UNASSIGNED | ACTIVE | COMPLETED | CANCELLED

    @ManyToOne
    private Etudiant etudiant;

    @ManyToOne
    private Entreprise entreprise;

    @ManyToOne
    private EncadrantAcademique encadrantAcademique;
}
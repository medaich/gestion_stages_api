package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class EncadrantAcademique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;

    private String departement;
    private String etablissement;

    @OneToMany
    private List<Stage> stages;
}

package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String intitule;

    @OneToMany
    private List<Etudiant> etudiants;

    @ManyToOne
    private Responsable responsable;
}

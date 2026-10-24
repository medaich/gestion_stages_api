package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String nom;
    String adresse;
    String telephone;
    String email;

    String ville;
    String pays;

    String responsableEmail;

    String encadrantEmail;
    String encadrantTelephone;

    @OneToMany
    private List<Stage> stages;
}
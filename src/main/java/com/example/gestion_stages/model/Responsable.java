package com.example.gestion_stages.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Responsable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private int tel;
    private String grade;

    @OneToMany
    private List<Filiere> filieres;
}

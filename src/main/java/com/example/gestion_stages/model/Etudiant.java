package com.example.gestion_stages.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Etudiant {
    @Id
    private String cne;

    private String nom;
    private String prenom;
    private String email;
    private String tel;

    @OneToMany
    private List<Stage> stages; // we can only have one active stage per student; However, we can have multiple abandoned stages

    @ManyToOne
    private Filiere filiere;
}

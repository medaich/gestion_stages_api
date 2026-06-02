package com.example.gestion_stages.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Etudiant {
    @Id
    private String cne;

    private String nom;
    private String prenom;
    private String email;
    private int tel;
}

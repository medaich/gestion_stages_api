package com.example.gestion_stages.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class Entreprise {
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
}
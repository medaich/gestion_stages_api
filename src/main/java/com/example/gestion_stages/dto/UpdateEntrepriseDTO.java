package com.example.gestion_stages.dto;

import lombok.Data;

@Data
public class UpdateEntrepriseDTO {

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
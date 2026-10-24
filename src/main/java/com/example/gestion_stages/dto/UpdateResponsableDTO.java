package com.example.gestion_stages.dto;

import lombok.Data;

@Data
public class UpdateResponsableDTO {

    private String nom;
    private String prenom;
    private String email;
    private int tel;
    private String grade;
}


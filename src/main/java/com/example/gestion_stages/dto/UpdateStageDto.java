package com.example.gestion_stages.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStageDto {

    private String sujet;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String description;

    private String objectifs;

    private String solution;

    private String demarche;

    private String outils;

    private String status; // UNASSIGNED | ACTIVE | COMPLETED | CANCELLED

}
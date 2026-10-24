package com.example.gestion_stages.dto;

import com.example.gestion_stages.model.Stage;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
public class EncadrantAcademiqueDTO {

    private String nom;
    private String prenom;

    private String departement;
    private String etablissement;
}

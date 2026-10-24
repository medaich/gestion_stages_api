package com.example.gestion_stages.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiliereStatDTO {
    private int filiereId;
    private String intitule;
    private long count;
}

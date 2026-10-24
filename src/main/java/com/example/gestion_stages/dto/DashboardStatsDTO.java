package com.example.gestion_stages.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsDTO {

    private long totalStages;

    /** Keyed by status string: UNASSIGNED, ACTIVE, COMPLETED, CANCELLED */
    private Map<String, Long> byStatus;

    /** One entry per filière that has at least one assigned stage */
    private List<FiliereStatDTO> byFiliere;

    private long totalEtudiants;
    private long totalEntreprises;
    private long totalEncadrants;
}

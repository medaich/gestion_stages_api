package com.example.gestion_stages;

import com.example.gestion_stages.dto.DashboardStatsDTO;
import com.example.gestion_stages.dto.FiliereStatDTO;
import com.example.gestion_stages.repository.EncadrantAcademiqueRepository;
import com.example.gestion_stages.repository.EntrepriseRepository;
import com.example.gestion_stages.repository.EtudiantRepository;
import com.example.gestion_stages.repository.StageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Dashboard", description = "Statistiques globales de la plateforme de gestion des stages")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private StageRepository stageRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private EntrepriseRepository entrepriseRepository;
    @Autowired private EncadrantAcademiqueRepository encadrantRepository;

    @Operation(
        summary = "Statistiques globales",
        description = "Retourne le nombre total de stages, leur répartition par statut et par filière, " +
                      "ainsi que les totaux d'étudiants, d'entreprises et d'encadrants."
    )
    @GetMapping
    public ResponseEntity<DashboardStatsDTO> dashboard() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Total counts
        stats.setTotalStages(stageRepository.count());
        stats.setTotalEtudiants(etudiantRepository.count());
        stats.setTotalEntreprises(entrepriseRepository.count());
        stats.setTotalEncadrants(encadrantRepository.count());

        // By status — JPQL GROUP BY returns List<Object[]> {status, count}
        Map<String, Long> byStatus = new LinkedHashMap<>();
        // Pre-populate all valid statuses so they always appear in the response
        byStatus.put("UNASSIGNED", 0L);
        byStatus.put("ACTIVE",     0L);
        byStatus.put("COMPLETED",  0L);
        byStatus.put("CANCELLED",  0L);
        for (Object[] row : stageRepository.countByStatus()) {
            String status = (String) row[0];
            long   count  = ((Number) row[1]).longValue();
            byStatus.put(status, count);
        }
        stats.setByStatus(byStatus);

        // By filière — JPQL GROUP BY returns List<Object[]> {filiereId, intitule, count}
        List<FiliereStatDTO> byFiliere = stageRepository.countByFiliere()
                .stream()
                .map(row -> new FiliereStatDTO(
                        ((Number) row[0]).intValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();
        stats.setByFiliere(byFiliere);

        return ResponseEntity.ok(stats);
    }
}

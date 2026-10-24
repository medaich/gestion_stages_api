package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateStageDto;
import com.example.gestion_stages.model.EncadrantAcademique;
import com.example.gestion_stages.model.Entreprise;
import com.example.gestion_stages.model.Etudiant;
import com.example.gestion_stages.model.Stage;
import com.example.gestion_stages.repository.EncadrantAcademiqueRepository;
import com.example.gestion_stages.repository.EntrepriseRepository;
import com.example.gestion_stages.repository.EtudiantRepository;
import com.example.gestion_stages.repository.StageRepository;
import com.example.gestion_stages.service.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Stages", description = "Gestion des stages (CRUD, filtres, rapports PDF)")
@RestController
@RequestMapping("/api/stages")
public class StageController {

    @Autowired private StageRepository stageRepository;
    @Autowired private EtudiantRepository etudiantRepository;
    @Autowired private EntrepriseRepository entrepriseRepository;
    @Autowired private EncadrantAcademiqueRepository encadrantRepository;
    @Autowired private PdfService pdfService;
    @Autowired private ObjectMapper objectMapper;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Operation(summary = "Lister les stages",
               description = "Retourne tous les stages. Filtrable par filière et/ou année de début.")
    @GetMapping
    public ResponseEntity<List<Stage>> stages(
            @Parameter(description = "ID de la filière") @RequestParam(required = false) Integer filiere,
            @Parameter(description = "Année de dateDebut (ex: 2025)") @RequestParam(required = false) Integer annee) {

        if (filiere != null || annee != null) {
            return ResponseEntity.ok(stageRepository.findByFiliereAndAnnee(filiere, annee));
        }
        return ResponseEntity.ok(stageRepository.findAll());
    }

    @Operation(summary = "Obtenir un stage par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Stage> stage(@PathVariable int id) {
        Optional<Stage> stageOptional = stageRepository.findById(id);
        return stageOptional.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouveau stage")
    @PostMapping
    public ResponseEntity<Stage> addStage(@RequestBody Stage stage) {
        return ResponseEntity.ok(stageRepository.save(stage));
    }

    @Operation(summary = "Supprimer un stage")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable int id) {
        stageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre à jour partiellement un stage")
    @PatchMapping("/{id}")
    public ResponseEntity<Stage> updateStage(@PathVariable int id, @RequestBody UpdateStageDto dto) {
        Optional<Stage> stageOptional = stageRepository.findById(id);
        if (stageOptional.isEmpty()) return ResponseEntity.notFound().build();

        Stage stage = stageOptional.get();
        try {
            objectMapper.updateValue(stage, dto);
            return ResponseEntity.ok(stageRepository.save(stage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─── ASSIGNMENT ACTIONS ──────────────────────────────────────────────────

    @Operation(summary = "Assigner un étudiant à un stage")
    @PostMapping("{id}/assign/etudiant")
    public ResponseEntity<Stage> assignStageToEtudiant(@PathVariable int id,
                                                        @RequestParam String etudiantId) {
        Optional<Stage> stageOptional       = stageRepository.findById(id);
        Optional<Etudiant> etudiantOptional = etudiantRepository.findById(etudiantId);

        if (stageOptional.isPresent() && etudiantOptional.isPresent()) {
            stageOptional.get().setEtudiant(etudiantOptional.get());
            stageOptional.get().setStatus("ACTIVE");
            return ResponseEntity.ok(stageRepository.save(stageOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Assigner une entreprise à un stage")
    @PostMapping("{id}/assign/enterprise")
    public ResponseEntity<Stage> assignStageToEntreprise(@PathVariable int id,
                                                          @RequestParam int entrepriseId) {
        Optional<Stage> stageOptional           = stageRepository.findById(id);
        Optional<Entreprise> entrepriseOptional = entrepriseRepository.findById(entrepriseId);

        if (stageOptional.isPresent() && entrepriseOptional.isPresent()) {
            stageOptional.get().setEntreprise(entrepriseOptional.get());
            return ResponseEntity.ok(stageRepository.save(stageOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Assigner un encadrant académique à un stage")
    @PostMapping("{id}/assign/encadrant")
    public ResponseEntity<Stage> assignStageToEncadrant(@PathVariable int id,
                                                         @RequestParam int encadrantId) {
        Optional<Stage> stageOptional                  = stageRepository.findById(id);
        Optional<EncadrantAcademique> encadrantOptional = encadrantRepository.findById(encadrantId);

        if (stageOptional.isPresent() && encadrantOptional.isPresent()) {
            stageOptional.get().setEncadrantAcademique(encadrantOptional.get());
            return ResponseEntity.ok(stageRepository.save(stageOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // ─── PDF REPORTS ─────────────────────────────────────────────────────────

    @Operation(summary = "Rapport PDF récapitulatif",
               description = "Génère un PDF listant tous les stages correspondant aux filtres optionnels. " +
                             "Sans paramètres, retourne un rapport de tous les stages.")
    @GetMapping("/rapport")
    public ResponseEntity<byte[]> summaryRapport(
            @Parameter(description = "Filtrer par filière") @RequestParam(required = false) Integer filiere,
            @Parameter(description = "Filtrer par année de début") @RequestParam(required = false) Integer annee) {

        List<Stage> stages = (filiere != null || annee != null)
                ? stageRepository.findByFiliereAndAnnee(filiere, annee)
                : stageRepository.findAll();

        byte[] pdf = pdfService.generateSummaryRapport(stages);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rapport_stages.pdf\"")
                .body(pdf);
    }

    @Operation(summary = "Rapport PDF d'un stage",
               description = "Génère un PDF détaillé pour un stage donné, incluant étudiant, entreprise et encadrant.")
    @GetMapping("/{id}/rapport")
    public ResponseEntity<byte[]> singleRapport(@PathVariable int id) {
        Optional<Stage> stageOptional = stageRepository.findById(id);
        if (stageOptional.isEmpty()) return ResponseEntity.notFound().build();

        byte[] pdf = pdfService.generateSingleRapport(stageOptional.get());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"rapport_stage_" + id + ".pdf\"")
                .body(pdf);
    }
}

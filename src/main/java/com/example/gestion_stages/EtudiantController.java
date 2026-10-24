package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateEtudiantDTO;
import com.example.gestion_stages.model.Etudiant;
import com.example.gestion_stages.model.Filiere;
import com.example.gestion_stages.repository.EtudiantRepository;
import com.example.gestion_stages.repository.FiliereRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {
    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Etudiant>> etudiants() {
        return ResponseEntity.ok(etudiantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> etudiant(@PathVariable String id) {
        Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        return etudiantOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Etudiant> addEtudiant(@RequestBody Etudiant etudiant) {
        return ResponseEntity.ok(etudiantRepository.save(etudiant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable String id) {
        etudiantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable String id, @RequestBody UpdateEtudiantDTO dto) {
        Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        if (etudiantOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Etudiant etudiant = etudiantOptional.get();

        try {
            objectMapper.updateValue(etudiant, dto);
            return ResponseEntity.ok(etudiantRepository.save(etudiant));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // assign an etudiant to a filiere
    @PostMapping("{id}/assign/filiere")
    public ResponseEntity<Etudiant> assignEtudiantToFiliere(@PathVariable String id, @RequestParam int filiereId) {
        Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        Optional<Filiere> filiereOptional = filiereRepository.findById(filiereId);

        if(etudiantOptional.isPresent() && filiereOptional.isPresent()) {
            etudiantOptional.get().setFiliere(filiereOptional.get());
            return ResponseEntity.ok(etudiantRepository.save(etudiantOptional.get()));
        }
        else return ResponseEntity.notFound().build();
    }
}

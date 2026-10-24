package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateEntrepriseDTO;
import com.example.gestion_stages.model.Entreprise;
import com.example.gestion_stages.repository.EntrepriseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entreprise")
public class EntrepriseController {
    @Autowired
    EntrepriseRepository entrepriseRepository;


    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Entreprise>> entreprises() {
        return ResponseEntity.ok(entrepriseRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entreprise> entreprise(@PathVariable int id) {
        Optional<Entreprise> entrepriseOptional = entrepriseRepository.findById(id);
        return entrepriseOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Entreprise> addEntreprise(@RequestBody Entreprise entreprise) {
        return ResponseEntity.ok(entrepriseRepository.save(entreprise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable int id) {
        entrepriseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Entreprise> updateEntreprise(@PathVariable int id, @RequestBody UpdateEntrepriseDTO dto) {
        Optional<Entreprise> entrepriseOptional = entrepriseRepository.findById(id);
        if (entrepriseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Entreprise entreprise = entrepriseOptional.get();

        try {
            objectMapper.updateValue(entreprise, dto);
            return ResponseEntity.ok(entrepriseRepository.save(entreprise));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

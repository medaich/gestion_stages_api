package com.example.gestion_stages;

import com.example.gestion_stages.dto.EncadrantAcademiqueDTO;
import com.example.gestion_stages.model.EncadrantAcademique;
import com.example.gestion_stages.repository.EncadrantAcademiqueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/encadrant")
public class EncadrantAcademiqueController {
    @Autowired
    EncadrantAcademiqueRepository encadrantRepository;


    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<EncadrantAcademique>> encadrants() {
        return ResponseEntity.ok(encadrantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncadrantAcademique> encadrant(@PathVariable int id) {
        Optional<EncadrantAcademique> encadrantOptional = encadrantRepository.findById(id);
        return encadrantOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EncadrantAcademique> addEncadrantAcademique(@RequestBody EncadrantAcademique encadrant) {
        return ResponseEntity.ok(encadrantRepository.save(encadrant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncadrantAcademique(@PathVariable int id) {
        encadrantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EncadrantAcademique> updateEncadrantAcademique(@PathVariable int id, @RequestBody EncadrantAcademiqueDTO dto) {
        Optional<EncadrantAcademique> encadrantOptional = encadrantRepository.findById(id);
        if (encadrantOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EncadrantAcademique encadrant = encadrantOptional.get();

        try {
            objectMapper.updateValue(encadrant, dto);
            return ResponseEntity.ok(encadrantRepository.save(encadrant));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

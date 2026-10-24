package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateResponsableDTO;
import com.example.gestion_stages.model.Responsable;
import com.example.gestion_stages.repository.ResponsableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/responsable")
public class ResponsableController {
    @Autowired
    ResponsableRepository responsableRepository;


    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Responsable>> responsables() {
        return ResponseEntity.ok(responsableRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Responsable> responsable(@PathVariable int id) {
        Optional<Responsable> responsableOptional = responsableRepository.findById(id);
        return responsableOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Responsable> addResponsable(@RequestBody Responsable responsable) {
        return ResponseEntity.ok(responsableRepository.save(responsable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponsable(@PathVariable int id) {
        responsableRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Responsable> updateResponsable(@PathVariable int id, @RequestBody UpdateResponsableDTO dto) {
        Optional<Responsable> responsableOptional = responsableRepository.findById(id);
        if (responsableOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Responsable responsable = responsableOptional.get();

        try {
            objectMapper.updateValue(responsable, dto);
            return ResponseEntity.ok(responsableRepository.save(responsable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

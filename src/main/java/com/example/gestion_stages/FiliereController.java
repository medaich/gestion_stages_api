package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateFiliereDTO;
import com.example.gestion_stages.model.Filiere;
import com.example.gestion_stages.model.Responsable;
import com.example.gestion_stages.repository.FiliereRepository;
import com.example.gestion_stages.repository.ResponsableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/filiere")
public class FiliereController {
    @Autowired
    FiliereRepository filiereRepository;

    @Autowired
    ResponsableRepository responsableRepository;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Filiere>> filieres() {
        return ResponseEntity.ok(filiereRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filiere> filiere(@PathVariable int id) {
        Optional<Filiere> filiereOptional = filiereRepository.findById(id);
        return filiereOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Filiere> addFiliere(@RequestBody Filiere filiere) {
        return ResponseEntity.ok(filiereRepository.save(filiere));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable int id) {
        filiereRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Filiere> updateFiliere(@PathVariable int id, @RequestBody UpdateFiliereDTO dto) {
        Optional<Filiere> filiereOptional = filiereRepository.findById(id);
        if (filiereOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Filiere filiere = filiereOptional.get();

        try {
            objectMapper.updateValue(filiere, dto);
            return ResponseEntity.ok(filiereRepository.save(filiere));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // assign a filiere to an responsable
    @PostMapping("{id}/assign/responsable")
    public ResponseEntity<Filiere> assignFiliereToResponsable(@PathVariable int id, @RequestParam int responsableId) {
        Optional<Filiere> filiereOptional = filiereRepository.findById(id);
        Optional<Responsable> responsableOptional = responsableRepository.findById(responsableId);

        if(filiereOptional.isPresent() && responsableOptional.isPresent()) {
            filiereOptional.get().setResponsable(responsableOptional.get());
            return ResponseEntity.ok(filiereRepository.save(filiereOptional.get()));
        }
        else return ResponseEntity.notFound().build();
    }
}

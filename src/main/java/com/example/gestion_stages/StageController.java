package com.example.gestion_stages;

import com.example.gestion_stages.dto.UpdateStageDto;
import com.example.gestion_stages.model.Stage;
import com.example.gestion_stages.repository.StageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/api/stages")
public class StageController {
    @Autowired
    private StageRepository stageRepository;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Stage>> stages() {
        return ResponseEntity.ok(stageRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Stage> addStage(@RequestBody Stage stage) {
        return ResponseEntity.ok(stageRepository.save(stage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable int id) {
        stageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Stage> updateStage(@PathVariable int id, @RequestBody UpdateStageDto dto) {
        Optional<Stage> stageOptional = stageRepository.findById(id);
        if (stageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stage stage = stageOptional.get();

        try {
            objectMapper.updateValue(stage, dto);
            return ResponseEntity.ok(stageRepository.save(stage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

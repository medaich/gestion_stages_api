package com.example.gestion_stages.repository;

import com.example.gestion_stages.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, Integer> {
}

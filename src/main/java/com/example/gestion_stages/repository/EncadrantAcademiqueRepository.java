package com.example.gestion_stages.repository;

import com.example.gestion_stages.model.EncadrantAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncadrantAcademiqueRepository extends JpaRepository<EncadrantAcademique, Integer> {
}

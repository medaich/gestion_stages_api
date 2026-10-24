package com.example.gestion_stages.repository;

import com.example.gestion_stages.model.Responsable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsableRepository extends JpaRepository<Responsable, Integer> {
}

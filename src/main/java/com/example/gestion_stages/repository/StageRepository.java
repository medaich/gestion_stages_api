package com.example.gestion_stages.repository;

import com.example.gestion_stages.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Integer> {

    /**
     * Returns stages filtered by optional filière and/or year of dateDebut.
     * Passing null for a parameter disables that filter.
     */
    @Query("""
            SELECT s FROM Stage s
            WHERE (:filiereId IS NULL OR s.etudiant.filiere.id = :filiereId)
            AND   (:annee     IS NULL OR YEAR(s.dateDebut) = :annee)
            """)
    List<Stage> findByFiliereAndAnnee(@Param("filiereId") Integer filiereId,
                                      @Param("annee") Integer annee);

    /**
     * Returns [status, count] pairs for the dashboard stats.
     */
    @Query("SELECT s.status, COUNT(s) FROM Stage s GROUP BY s.status")
    List<Object[]> countByStatus();

    /**
     * Returns [filiereId, intitule, count] for stages whose student has a filière.
     */
    @Query("""
            SELECT s.etudiant.filiere.id, s.etudiant.filiere.intitule, COUNT(s)
            FROM Stage s
            WHERE s.etudiant IS NOT NULL AND s.etudiant.filiere IS NOT NULL
            GROUP BY s.etudiant.filiere.id, s.etudiant.filiere.intitule
            """)
    List<Object[]> countByFiliere();
}

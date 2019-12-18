package com.example.testspring.repository;

import com.example.testspring.model.GeoClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * GeoClassRepository class for handling access to PostgreSQL.
 */

@Repository
public interface GeoClassRepository extends JpaRepository<GeoClass, Long> {
    List<GeoClass> findBySectionId(Long sectionId);
}
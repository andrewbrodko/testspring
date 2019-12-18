package com.example.testspring.repository;

import com.example.testspring.model.IOXLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * IOXLSRepository class for handling access to PostgreSQL.
 */

@Repository
public interface IOXLSRepository extends JpaRepository<IOXLS, Long> {
}
package com.example.testspring.repository;

import com.example.testspring.model.IOXLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOXLSRepository extends JpaRepository<IOXLS, Long> {
}
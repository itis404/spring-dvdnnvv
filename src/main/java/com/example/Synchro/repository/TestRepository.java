package com.example.Synchro.repository;

import com.example.Synchro.dto.TestSummaryDto;
import com.example.Synchro.entity.Test;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {


    List<Test> findAll();

    boolean existsByIsRequiredTrue();
    List<Test> findByIsRequiredTrue();

    @Query("SELECT new com.example.Synchro.dto.TestSummaryDto(t.id, t.name, t.description, t.isRequired) FROM Test t")
    List<TestSummaryDto> findAllSummaries();
}
package com.example.Synchro.repository;

import com.example.Synchro.entity.Test;
import com.example.Synchro.entity.TestResult;
import com.example.Synchro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResultRepository> findByUserAndTest(User user, Test test);
}

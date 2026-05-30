package com.example.Synchro.service;

import com.example.Synchro.dto.TestSummaryDto;
import com.example.Synchro.entity.Test;
import com.example.Synchro.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    @Cacheable(value = "testSummaries")
    public List<TestSummaryDto> getAllTestSummaries() {
        System.out.println(" ЗАГРУЗКА СПИСКА ТЕСТОВ ИЗ БД ");
        return testRepository.findAllSummaries();
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id).orElseThrow();
    }
}
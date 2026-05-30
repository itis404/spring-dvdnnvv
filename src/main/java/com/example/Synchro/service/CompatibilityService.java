package com.example.Synchro.service;

import com.example.Synchro.entity.Answer;
import com.example.Synchro.entity.Test;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.AnswerRepository;
import com.example.Synchro.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompatibilityService {

    private final AnswerRepository answerRepository;
    private final TestRepository testRepository;

    public int calculateCompatibility(User userA, User userB) {
        List<Test> allTests = testRepository.findAll();

        if (allTests.isEmpty()) {
            return 50;
        }

        int totalCompatibility = 0;
        int totalQuestions = 0;

        for (Test test : allTests) {
            List<Answer> answers = answerRepository.findByUsersAndTest(List.of(userA, userB), test);

            if (answers.size() < 2) {
                continue;
            }


            Map<Long, Integer> answersA = new HashMap<>();
            Map<Long, Integer> answersB = new HashMap<>();

            for (Answer answer : answers) {
                if (answer.getUser().getId().equals(userA.getId())) {
                    answersA.put(answer.getQuestion().getId(), answer.getValue());
                } else {
                    answersB.put(answer.getQuestion().getId(), answer.getValue());
                }
            }


            for (Long questionId : answersA.keySet()) {
                if (answersB.containsKey(questionId)) {
                    int valueA = answersA.get(questionId);
                    int valueB = answersB.get(questionId);
                    int diff = Math.abs(valueA - valueB);
                    int compatibility = 100 - (diff * 25);
                    totalCompatibility += Math.max(compatibility, 0);
                    totalQuestions++;
                }
            }
        }

        if (totalQuestions == 0) {
            return 0;
        }

        return totalCompatibility / totalQuestions;
    }
}
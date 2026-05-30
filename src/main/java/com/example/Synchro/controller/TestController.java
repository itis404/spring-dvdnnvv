package com.example.Synchro.controller;

import com.example.Synchro.dto.TestSummaryDto;
import com.example.Synchro.entity.Answer;
import com.example.Synchro.entity.Question;
import com.example.Synchro.entity.Test;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.AnswerRepository;
import com.example.Synchro.repository.QuestionRepository;
import com.example.Synchro.repository.TestRepository;
import com.example.Synchro.repository.UserRepository;
import com.example.Synchro.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestController {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final TestService testService;


    @GetMapping
    public String listTests(Model model) {
        List<TestSummaryDto> allTests = testService.getAllTestSummaries();
        model.addAttribute("tests", allTests);
        return "tests/list";
    }

    @GetMapping("/{testId}")
    public String takeTest(@PathVariable Long testId, Model model) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        List<Question> questions = questionRepository.findByTestIdOrderByIdAsc(testId);
        model.addAttribute("test", test);
        model.addAttribute("questions", questions);
        return "tests/take";
    }

    @PostMapping("/{testId}/submit")
    public String submitTest(@PathVariable Long testId,
                             @RequestParam Map<String, String> formData,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        for (Question question : test.getQuestions()) {
            String answerKey = "question_" + question.getId();
            String valueStr = formData.get(answerKey);

            if (valueStr != null && !valueStr.isEmpty()) {
                int value = Integer.parseInt(valueStr);

                if (!answerRepository.existsByUserAndQuestion(user, question)) {
                    Answer answer = Answer.builder()
                            .user(user)
                            .question(question)
                            .value(value)
                            .build();
                    answerRepository.save(answer);
                }
            }
        }

        if (test.isRequired()) {
            List<Test> requiredTests = testRepository.findByIsRequiredTrue();
            boolean allRequiredCompleted = true;

            for (Test requiredTest : requiredTests) {
                if (!answerRepository.existsByUserAndTest(user, requiredTest)) {
                    allRequiredCompleted = false;
                    break;
                }
            }

            if (allRequiredCompleted && !user.isHasCompletedRequiredTests()) {
                user.setHasCompletedRequiredTests(true);
                userRepository.save(user);
                return "redirect:/feed";
            }

            return "redirect:/tests/required";
        }

        return "redirect:/tests/" + testId + "/result";
    }

    @GetMapping("/{testId}/result")
    public String testResult(@PathVariable Long testId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        List<Answer> userAnswers = answerRepository.findByUserAndTest(user, test);

        int totalScore = 0;
        int maxScore = test.getQuestions().size() * 5;

        for (Answer answer : userAnswers) {
            totalScore += answer.getValue();
        }

        model.addAttribute("test", test);
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("maxScore", maxScore);
        model.addAttribute("answers", userAnswers);

        return "tests/result";
    }
    @GetMapping("/required")
    public String showRequiredTests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isHasCompletedRequiredTests()) {
            return "redirect:/feed";
        }

        List<Test> requiredTests = testRepository.findByIsRequiredTrue();
        model.addAttribute("tests", requiredTests);
        return "tests/required";
    }
}
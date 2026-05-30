package com.example.Synchro.controller;

import com.example.Synchro.entity.Question;
import com.example.Synchro.entity.Test;
import com.example.Synchro.repository.QuestionRepository;
import com.example.Synchro.repository.TestRepository;
import com.example.Synchro.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tests")
public class TestAdminController {
    private final TestService testService;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    @GetMapping
    public String listTests(Model model){
        model.addAttribute("tests", testRepository.findAll());
        return "admin/tests/list";

    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("test", new Test());
        return "admin/tests/create";
    }

    @PostMapping("/create")
    @CacheEvict(value = "tests", allEntries = true)
    public String createTest(@ModelAttribute Test test){
        testRepository.save(test);
        return "redirect:/admin/tests";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Test test = testRepository.findById(id).orElseThrow();
        model.addAttribute("test", test);
        return "admin/tests/edit";
    }

    @PostMapping("/edit/{id}")
    @CacheEvict(value = "tests", allEntries = true)
    public String updateTest(@PathVariable Long id, @ModelAttribute Test test) {
        Test existingTest = testRepository.findById(id).orElseThrow();
        existingTest.setName(test.getName());
        existingTest.setDescription(test.getDescription());
        existingTest.setRequired(test.isRequired());
        testRepository.save(existingTest);
        return "redirect:/admin/tests";
    }

    @GetMapping("/delete/{id}")
    @CacheEvict(value = "tests", allEntries = true)
    public String deleteTest(@PathVariable Long id){
        testRepository.deleteById(id);
        return "redirect:/admin/tests";
    }

    @GetMapping("/{testId}/question/create")
    public String showCreateQuestionForm(@PathVariable Long testId, Model model){
        Question question = new Question();
        question.setTest(testRepository.findById(testId).orElseThrow());
        model.addAttribute("question", question);
        model.addAttribute("test_id", testId);
        return "admin/tests/question-form";
    }

    @PostMapping("/{testId}/question/create")
    @CacheEvict(value = "tests", allEntries = true)
    public String createQuestion(@PathVariable Long testId, @ModelAttribute Question question){
        System.out.println("=== СОХРАНЕНИЕ ВОПРОСА ===");
        System.out.println("Текст: " + question.getText());
        System.out.println("TestId из URL: " + testId);

        Test test = testRepository.findById(testId).orElseThrow();
        question.setTest(test);
        questionRepository.save(question);

        System.out.println("Вопрос сохранён, ID: " + question.getId());

        return "redirect:/admin/tests/edit/" + testId;
    }

    @GetMapping("/{testId}/question/delete/{questionId}")
    @CacheEvict(value = "tests", allEntries = true)
    public String deleteQuestion(@PathVariable Long testId, @PathVariable Long questionId){
        questionRepository.deleteById(questionId);
        return "redirect:/admin/tests/edit/" + testId;
    }
}
package com.example.Synchro.controller;

import com.example.Synchro.entity.Profile;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.ProfileRepository;
import com.example.Synchro.repository.TestRepository;
import com.example.Synchro.repository.UserRepository;
import com.example.Synchro.service.CompatibilityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CompatibilityService compatibilityService;
    private final TestRepository testRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/feed")
    public String showFeed(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestParam(required = false) String city,
                           @RequestParam(required = false) Integer ageFrom,
                           @RequestParam(required = false) Integer ageTo,
                           @RequestParam(required = false) String goal,
                           @RequestParam(required = false) String hobby,
                           Model model) {


        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Пользователь {} загрузил ленту", currentUser.getEmail());

        if (!currentUser.isHasCompletedRequiredTests() && testRepository.existsByIsRequiredTrue()) {
            return "redirect:/tests/required";
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Profile> query = cb.createQuery(Profile.class);
        Root<Profile> profileRoot = query.from(Profile.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.notEqual(profileRoot.get("user").get("id"), currentUser.getId()));
        predicates.add(cb.isTrue(profileRoot.get("active")));

        if (!currentUser.getProfile().isActive()) {
            return "redirect:/profile?deactivated";
        }


        // Фильтр по городу
        if (city != null && !city.isEmpty()) {
            predicates.add(cb.equal(profileRoot.get("city"), city));
        }

        // Фильтр по возрасту
        if (ageFrom != null || ageTo != null) {
            LocalDate today = LocalDate.now();
            if (ageFrom != null) {
                LocalDate maxBirthDate = today.minusYears(ageFrom);
                predicates.add(cb.lessThanOrEqualTo(profileRoot.get("birthdate"), maxBirthDate));
            }
            if (ageTo != null) {
                LocalDate minBirthDate = today.minusYears(ageTo + 1);
                predicates.add(cb.greaterThanOrEqualTo(profileRoot.get("birthdate"), minBirthDate));
            }
        }

        // Фильтр по цели знакомства
        if (goal != null && !goal.isEmpty()) {
            predicates.add(cb.equal(profileRoot.get("goal"), goal));
        }

        // Фильтр по интересам
        if (hobby != null && !hobby.isEmpty()) {
            predicates.add(cb.isMember(hobby, profileRoot.get("hobbies")));
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Profile> filteredProfiles = entityManager.createQuery(query).getResultList();

        // Для каждого профиля считаем совместимость
        List<Map<String, Object>> profilesWithCompatibility = new ArrayList<>();
        for (Profile profile : filteredProfiles) {
            int compatibility = compatibilityService.calculateCompatibility(currentUser, profile.getUser());

            Map<String, Object> profileData = new HashMap<>();
            profileData.put("profile", profile);
            profileData.put("compatibility", compatibility);
            profilesWithCompatibility.add(profileData);
        }

        // Сортируем по совместимости
        profilesWithCompatibility.sort((a, b) ->
                Integer.compare((int) b.get("compatibility"), (int) a.get("compatibility")));

        // Оставляем только тех, у кого совместимость больше 70%
        List<Map<String, Object>> recommended = new ArrayList<>();
        for (Map<String, Object> item : profilesWithCompatibility) {
            int compatibility = (int) item.get("compatibility");
            if (compatibility >= 70) {
                recommended.add(item);
            }
        }
        profilesWithCompatibility = recommended;

        model.addAttribute("profilesWithCompatibility", profilesWithCompatibility);
        model.addAttribute("currentUser", currentUser);

        return "feed";
    }
}
package com.example.Synchro.controller;

import com.example.Synchro.dto.RegistrationDto;
import com.example.Synchro.entity.Profile;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.ProfileRepository;
import com.example.Synchro.repository.TestRepository;
import com.example.Synchro.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestRepository testRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegistrationDto dto,
                               BindingResult result,
                               Model model){

        if (userRepository.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "error.email", "Email уже используется");
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("USER")
                .active(true)
                .hasCompletedRequiredTests(false)
                .build();
        userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .username(dto.getUsername())
                .city(dto.getCity())
                .build();
        profileRepository.save(profile);

        boolean hasRequiredTests = testRepository.existsByIsRequiredTrue();

        if (hasRequiredTests) {
            return "redirect:/login?registered&requiredTests=true";
        }

        return "redirect:/login?registered";
    }
}
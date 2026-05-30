package com.example.Synchro.controller;

import com.example.Synchro.entity.FavoriteMovie;
import com.example.Synchro.entity.Profile;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.FavoriteMovieRepository;
import com.example.Synchro.repository.ProfileRepository;
import com.example.Synchro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FavoriteMovieRepository favoriteMovieRepository;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();

        model.addAttribute("profile", profile);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String username,
                                @RequestParam String city,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String gender,
                                @RequestParam(required = false) String goal,
                                @RequestParam(required = false) String children,
                                @RequestParam(required = false) String smoking,
                                @RequestParam(required = false) String alcohol,
                                @RequestParam(required = false) List<String> hobbies,
                                @RequestParam(required = false) String favoriteMovieId,
                                @RequestParam(required = false) String favoriteMovieTitle,
                                @RequestParam(required = false) String favoriteMovieYear,
                                @RequestParam(required = false) String favoriteMoviePoster,
                                @RequestParam(required = false) MultipartFile photo,
                                @RequestParam(required = false) LocalDate birthdate) throws IOException {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();

        profile.setUsername(username);
        profile.setCity(city);
        if (bio != null) profile.setBio(bio);
        if (gender != null) profile.setGender(gender);
        if (birthdate != null) {
            profile.setBirthdate(birthdate);
        }

        if (goal != null) profile.setGoal(goal);
        if (children != null) profile.setChildren(children);
        if (smoking != null) profile.setSmoking(smoking);
        if (alcohol != null) profile.setAlcohol(alcohol);

        if (hobbies != null && !hobbies.isEmpty()) {
            profile.setHobbies(hobbies);
        } else {
            profile.setHobbies(new ArrayList<>());
        }

        if (favoriteMovieId != null && !favoriteMovieId.isEmpty()) {
            FavoriteMovie movie = favoriteMovieRepository.findById(favoriteMovieId)
                    .orElseGet(() -> {
                        FavoriteMovie newMovie = FavoriteMovie.builder()
                                .id(favoriteMovieId)
                                .title(favoriteMovieTitle)
                                .year(favoriteMovieYear)
                                .posterUrl(favoriteMoviePoster)
                                .build();
                        return favoriteMovieRepository.save(newMovie);
                    });
            profile.setFavoriteMovie(movie);
        } else {
            profile.setFavoriteMovie(null);
        }

        if (photo != null && !photo.isEmpty()) {
            if (photo.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("Файл слишком большой. Максимальный размер: 5 МБ");
            }

            String contentType = photo.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                throw new RuntimeException("Можно загружать только изображения (JPG, JPEG, PNG)");
            }

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                if (!uploadPath.mkdirs()) {
                    throw new RuntimeException("Не удалось создать директорию для загрузки фото: " + uploadDir);
                }
            }

            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            File destFile = new File(uploadDir + fileName);
            photo.transferTo(destFile);
            profile.setPhotoUrl("/uploads/" + fileName);

            System.out.println(" Фото сохранено: " + destFile.getAbsolutePath());
        }

        profileRepository.save(profile);
        return "redirect:/profile";
    }

    @GetMapping("/profile/deactivate")
    public String deactivateProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();
        profile.setActive(false);
        profileRepository.save(profile);
        return "redirect:/profile?deactivated";
    }

    @GetMapping("/profile/activate")
    public String activateProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = user.getProfile();
        profile.setActive(true);
        profileRepository.save(profile);
        return "redirect:/profile?activated";
    }
}
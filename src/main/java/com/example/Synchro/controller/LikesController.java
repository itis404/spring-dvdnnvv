package com.example.Synchro.controller;

import com.example.Synchro.entity.Like;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.LikeRepository;
import com.example.Synchro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikesController {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String showLikes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Like> likes = likeRepository.findByToUser(currentUser);

        model.addAttribute("likes", likes);
        model.addAttribute("currentUser", currentUser);
        return "likes";
    }
}
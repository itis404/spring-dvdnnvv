package com.example.Synchro.controller;

import com.example.Synchro.entity.Notification;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.NotificationRepository;
import com.example.Synchro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String showNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("notifications", notifications);

        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        for (Notification n : unread) {
            n.setRead(true);
            notificationRepository.save(n);
        }

        return "notifications";
    }

    @GetMapping("/count")
    @ResponseBody
    public long getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return 0;
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return 0;
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
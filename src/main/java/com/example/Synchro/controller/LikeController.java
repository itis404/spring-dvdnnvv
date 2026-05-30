package com.example.Synchro.controller;

import com.example.Synchro.entity.Like;
import com.example.Synchro.entity.Notification;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.LikeRepository;
import com.example.Synchro.repository.NotificationRepository;
import com.example.Synchro.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@Slf4j
public class LikeController {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @PostMapping("/{toUserId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> like(
            @PathVariable Long toUserId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User fromUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        boolean alreadyLiked = likeRepository.existsByFromUserAndToUser(fromUser, toUser);
        Map<String, Object> response = new HashMap<>();

        log.info("Пользователь {} {} лайк пользователю {}", fromUser.getEmail(), alreadyLiked ? "отменил" : "поставил", toUser.getEmail());

        if (alreadyLiked) {
            likeRepository.deleteByFromUserAndToUser(fromUser, toUser);
            response.put("liked", false);
            response.put("message", "Лайк отменён");
        } else {
            Like like = Like.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(like);
            response.put("liked", true);
            response.put("message", "Лайк поставлен");

            Notification notification = Notification.builder()
                    .user(toUser)
                    .type("LIKE")
                    .message("Пользователь " + fromUser.getProfile().getUsername() + " поставил вам лайк")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            boolean mutual = likeRepository.existsByFromUserAndToUser(toUser, fromUser);
            if (mutual) {
                Notification mutualNotif = Notification.builder()
                        .user(fromUser)
                        .type("MATCH")
                        .message("У вас взаимный лайк с " + toUser.getProfile().getUsername() + "! Начните чат")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(mutualNotif);

                Notification mutualNotif2 = Notification.builder()
                        .user(toUser)
                        .type("MATCH")
                        .message("У вас взаимный лайк с " + fromUser.getProfile().getUsername() + "! Начните чат")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(mutualNotif2);

                response.put("mutual", true);
                response.put("message", " Взаимный лайк! Начните чат");
            }
        }

        long likesCount = likeRepository.findByToUser(toUser).size();
        response.put("likesCount", likesCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{toUserId}")
    public ResponseEntity<Map<String, Boolean>> checkLike(
            @PathVariable Long toUserId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User fromUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        boolean liked = likeRepository.existsByFromUserAndToUser(fromUser, toUser);

        Map<String, Boolean> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mutual/{toUserId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> mutualLike(@PathVariable Long toUserId, @AuthenticationPrincipal UserDetails userDetails) {

        User fromUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        boolean alreadyLiked = likeRepository.existsByFromUserAndToUser(fromUser, toUser);
        Map<String, Object> response = new HashMap<>();

        if (!alreadyLiked) {
            Like like = Like.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(like);
            response.put("liked", true);

            Notification notification = Notification.builder()
                    .user(toUser)
                    .type("LIKE")
                    .message("Пользователь " + fromUser.getProfile().getUsername() + " поставил вам лайк")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            boolean mutual = likeRepository.existsByFromUserAndToUser(toUser, fromUser);
            if (mutual) {
                Notification mutualNotif = Notification.builder()
                        .user(fromUser)
                        .type("MATCH")
                        .message("У вас взаимный лайк с " + toUser.getProfile().getUsername() + "! Начните чат")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(mutualNotif);

                Notification mutualNotif2 = Notification.builder()
                        .user(toUser)
                        .type("MATCH")
                        .message("У вас взаимный лайк с " + fromUser.getProfile().getUsername() + "! Начните чат")
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(mutualNotif2);

                response.put("mutual", true);
                response.put("message", "🎉 Взаимный лайк! Начните чат");
            } else {
                response.put("mutual", false);
                response.put("message", "Лайк отправлен");
            }
        } else {
            response.put("liked", false);
            response.put("message", "Вы уже лайкнули этого пользователя");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-mutual/{toUserId}")
    public ResponseEntity<Map<String, Boolean>> checkMutual(
            @PathVariable Long toUserId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean mutual = likeRepository.existsByFromUserAndToUser(currentUser, otherUser) &&
                likeRepository.existsByFromUserAndToUser(otherUser, currentUser);

        Map<String, Boolean> response = new HashMap<>();
        response.put("mutual", mutual);
        return ResponseEntity.ok(response);
    }
}
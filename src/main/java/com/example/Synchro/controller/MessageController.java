package com.example.Synchro.controller;

import com.example.Synchro.entity.Message;
import com.example.Synchro.entity.Notification;
import com.example.Synchro.entity.User;
import com.example.Synchro.repository.LikeRepository;
import com.example.Synchro.repository.MessageRepository;
import com.example.Synchro.repository.NotificationRepository;
import com.example.Synchro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/messages")
@Slf4j
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final NotificationRepository notificationRepository;

    @GetMapping
    public String showChats(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> mutualUsers = likeRepository.findMutualUsers(currentUser);

        List<Map<String, Object>> chats = new ArrayList<>();
        for (User otherUser : mutualUsers) {
            List<Message> conversation = messageRepository.findConversation(currentUser, otherUser);
            long unreadCount = messageRepository.countByReceiverAndIsReadFalse(currentUser);

            Map<String, Object> chat = new HashMap<>();
            chat.put("userId", otherUser.getId());
            chat.put("username", otherUser.getProfile().getUsername());
            chat.put("lastMessage", conversation.isEmpty() ? "Нет сообщений" : conversation.get(conversation.size() - 1).getText());
            chat.put("unreadCount", unreadCount);
            chats.add(chat);
        }

        model.addAttribute("chats", chats);
        return "messages";
    }

    @GetMapping("/chat/{userId}")
    public String showChat(@PathVariable Long userId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasMutualLike = likeRepository.existsByFromUserAndToUser(currentUser, otherUser) &&
                likeRepository.existsByFromUserAndToUser(otherUser, currentUser);

        if (!hasMutualLike) {
            return "redirect:/messages";
        }

        List<Message> messages = messageRepository.findConversation(currentUser, otherUser);

        for (Message msg : messages) {
            if (msg.getReceiver().getId().equals(currentUser.getId()) && !msg.isRead()) {
                msg.setRead(true);
                messageRepository.save(msg);
            }
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("messages", messages);
        return "chat";
    }

    @PostMapping("/send")
    @ResponseBody
    public Map<String, Object> sendMessage(@RequestParam Long receiverId,
                                           @RequestParam String text,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        User sender = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .text(text)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        messageRepository.save(message);

        log.info("Сообщение отправлено от {} к {}", sender.getEmail(), receiver.getEmail());

        Notification notification = Notification.builder()
                .user(receiver)
                .type("MESSAGE")
                .message("Новое сообщение от " + sender.getProfile().getUsername())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        return Map.of("success", true, "messageId", message.getId(), "text", text, "sentAt", message.getSentAt().toString());
    }

    @GetMapping("/api/new")
    @ResponseBody
    public List<Map<String, Object>> getNewMessages(@RequestParam Long lastMessageId,
                                                    @RequestParam Long receiverId,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> newMessages = messageRepository.findNewMessages(currentUser, otherUser, lastMessageId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : newMessages) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", msg.getId());
            m.put("text", msg.getText());
            m.put("senderId", msg.getSender().getId());
            m.put("formattedTime", msg.getSentAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            result.add(m);
        }
        return result;
    }
    @GetMapping("/api/messages/latest")
    @ResponseBody
    public List<Map<String, Object>> getLatestMessages(@RequestParam Long afterId,
                                                       @RequestParam Long otherUserId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> newMessages = messageRepository.findMessagesAfterId(currentUser, otherUser, afterId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : newMessages) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", msg.getId());
            m.put("text", msg.getText());
            m.put("senderId", msg.getSender().getId());
            m.put("formattedTime", msg.getSentAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            result.add(m);
        }
        return result;
    }
    @GetMapping("/api/test")
    @ResponseBody
    public String testApi() {
        return "API works!";
    }
}
package com.example.Synchro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JoinColumn(nullable = false)
    private String type;

    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }


}

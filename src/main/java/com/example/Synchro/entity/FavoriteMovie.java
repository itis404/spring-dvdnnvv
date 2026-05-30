package com.example.Synchro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite_movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteMovie {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String year;

    private String posterUrl;
}
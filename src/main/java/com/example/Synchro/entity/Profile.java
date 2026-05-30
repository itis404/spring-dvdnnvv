package com.example.Synchro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String username;
    private String city;
    private LocalDate birthdate;
    private String bio;
    private String gender;
    private String photoUrl;
    private String goal;
    private String children;
    private String smoking;
    private String alcohol;
    private boolean active = true;



    @ManyToMany
    @JoinTable(
            name = "profile_interests",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "interests_id")
    )
    private List<Interest> interests = new ArrayList<>();

    public int getAge() {
        if (birthdate == null) return 0;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    @ElementCollection
    @CollectionTable(name = "profile_hobbies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "hobby")
    private List<String> hobbies = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "favorite_movie_id")
    private FavoriteMovie favoriteMovie;


}

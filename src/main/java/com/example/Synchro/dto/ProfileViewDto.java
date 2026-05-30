package com.example.Synchro.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProfileViewDto {
    private Long id;
    private String username;
    private String city;
    private int age;
    private String bio;
    private String gender;
    private String photoUrl;
    private String goal;
    private String children;
    private String smoking;
    private String alcohol;
    private List<String> hobbies;
}
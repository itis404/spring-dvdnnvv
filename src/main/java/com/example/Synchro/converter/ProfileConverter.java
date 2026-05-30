package com.example.Synchro.converter;

import com.example.Synchro.dto.ProfileViewDto;
import com.example.Synchro.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileConverter {

    public ProfileViewDto toDto(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileViewDto.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .city(profile.getCity())
                .age(profile.getAge())
                .bio(profile.getBio())
                .gender(profile.getGender())
                .photoUrl(profile.getPhotoUrl())
                .goal(profile.getGoal())
                .children(profile.getChildren())
                .smoking(profile.getSmoking())
                .alcohol(profile.getAlcohol())
                .hobbies(profile.getHobbies())
                .build();
    }
}
package com.example.Synchro.repository;

import com.example.Synchro.entity.Interest;
import com.example.Synchro.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}

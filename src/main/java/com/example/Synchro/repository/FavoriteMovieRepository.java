package com.example.Synchro.repository;

import com.example.Synchro.entity.FavoriteMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteMovieRepository extends JpaRepository<FavoriteMovie, String> {
}
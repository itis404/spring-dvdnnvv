package com.example.Synchro.controller;

import com.example.Synchro.service.KinopoiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final KinopoiskService kinopoiskService;

    @GetMapping("/search")
    public List<Map<String, String>> searchMovies(@RequestParam String query) {
        return kinopoiskService.searchMovies(query);
    }
}
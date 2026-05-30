package com.example.Synchro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class KinopoiskService {

    @Value("${kinopoisk.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, String>> searchMovies(String query) {
        if (query == null || query.length() < 2) {
            return Collections.emptyList();
        }

        try {
            String url = "https://api.poiskkino.dev/v1.4/movie/search?page=1&limit=5&query=" +
                    java.net.URLEncoder.encode(query, "UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode docs = root.path("docs");

            List<Map<String, String>> movies = new ArrayList<>();
            for (JsonNode doc : docs) {
                Map<String, String> movie = new HashMap<>();
                movie.put("id", doc.path("id").asText());

                String title = doc.path("name").asText();
                if (title == null || title.isEmpty()) {
                    title = doc.path("alternativeName").asText();
                }
                if (title == null || title.isEmpty()) {
                    title = "Без названия";
                }
                movie.put("title", title);

                movie.put("year", doc.path("year").asText());

                JsonNode poster = doc.path("poster");
                String posterUrl = poster.path("url").asText();
                if (posterUrl == null || posterUrl.isEmpty()) {
                    posterUrl = "/images/no-poster.png";
                }
                movie.put("poster", posterUrl);

                movies.add(movie);
            }
            return movies;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
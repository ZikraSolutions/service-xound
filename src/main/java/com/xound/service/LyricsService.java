package com.xound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class LyricsService {

    private static final String LRCLIB_URL = "https://lrclib.net";

    private final RestClient restClient;

    public LyricsService() {
        this.restClient = RestClient.builder()
                .baseUrl(LRCLIB_URL)
                .defaultHeader("User-Agent", "XOUND/1.0")
                .build();
    }

    /**
     * Busca la letra de una cancion usando lrclib.net API.
     * GET https://lrclib.net/api/get?artist_name={artist}&track_name={title}
     * Retorna la letra como texto plano, o null si no se encontro.
     */
    public String searchLyrics(String artist, String title) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri("/api/get?artist_name={artist}&track_name={title}", artist.trim(), title.trim())
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("plainLyrics")) {
                String lyrics = (String) response.get("plainLyrics");
                if (lyrics != null && !lyrics.isBlank()) {
                    return lyrics.trim();
                }
            }
            return null;
        } catch (RestClientException e) {
            // Si falla el get directo, intentar con search
            return searchLyricsFallback(artist, title);
        }
    }

    private String searchLyricsFallback(String artist, String title) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> results = restClient.get()
                    .uri("/api/search?q={query}", artist.trim() + " " + title.trim())
                    .retrieve()
                    .body(java.util.List.class);

            if (results != null && !results.isEmpty()) {
                Map<String, Object> first = results.get(0);
                String lyrics = (String) first.get("plainLyrics");
                if (lyrics != null && !lyrics.isBlank()) {
                    return lyrics.trim();
                }
            }
            return null;
        } catch (RestClientException e) {
            return null;
        }
    }
}

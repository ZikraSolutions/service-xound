package com.xound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class LyricsService {

    private static final String LYRICS_OVH_URL = "https://api.lyrics.ovh/v1";

    private final RestClient restClient;

    public LyricsService() {
        this.restClient = RestClient.builder()
                .baseUrl(LYRICS_OVH_URL)
                .build();
    }

    /**
     * Busca la letra de una cancion usando lyrics.ovh API.
     * GET https://api.lyrics.ovh/v1/{artist}/{title}
     * Retorna la letra como texto plano, o null si no se encontro.
     */
    public String searchLyrics(String artist, String title) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri("/{artist}/{title}", artist.trim(), title.trim())
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("lyrics")) {
                return response.get("lyrics").toString().trim();
            }
            return null;
        } catch (RestClientException e) {
            return null;
        }
    }
}

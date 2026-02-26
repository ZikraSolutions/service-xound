package com.xound.service;

import com.xound.model.ChordSearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChordSearchService {

    private static final String BASE_URL = "https://www.lacuerda.net";
    private static final String SEARCH_URL = BASE_URL + "/buscar/?q=";
    private static final String ALLOWED_HOST = "www.lacuerda.net";

    /**
     * Busca canciones en lacuerda.net y retorna lista de resultados con título, artista y URL.
     */
    public List<ChordSearchResult> search(String query) {
        List<ChordSearchResult> results = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            Document doc = Jsoup.connect(SEARCH_URL + encodedQuery)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10_000)
                    .get();

            // lacuerda.net muestra resultados en elementos <li> dentro de .resultados
            Elements items = doc.select(".resultados li, .result-list li, ul.canciones li");

            if (items.isEmpty()) {
                // Fallback: buscar cualquier link que apunte a una canción dentro del sitio
                items = doc.select("a[href*='/acordes/'], a[href*='/tabs/'], a[href*='/canciones/']")
                        .parents().stream()
                        .reduce(new Elements(), (acc, el) -> { acc.add(el); return acc; }, (a, b) -> a);
            }

            for (Element item : items) {
                Element link = item.selectFirst("a[href]");
                if (link == null) continue;

                String href = link.attr("href");
                String fullUrl = href.startsWith("http") ? href : BASE_URL + href;
                String text = item.text();

                // Intenta separar "Artista - Título" o "Título por Artista"
                String title = link.text().trim();
                String artist = "";

                Element artistEl = item.selectFirst(".artista, .artist, span.por");
                if (artistEl != null) {
                    artist = artistEl.text().trim();
                } else if (text.contains(" - ")) {
                    String[] parts = text.split(" - ", 2);
                    artist = parts[0].trim();
                    title = parts[1].trim();
                }

                if (!fullUrl.contains(ALLOWED_HOST)) continue;

                results.add(new ChordSearchResult(title, artist, fullUrl, null));

                if (results.size() >= 20) break;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al buscar en lacuerda.net: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Descarga el contenido de acordes de una URL específica de lacuerda.net.
     * Solo acepta URLs del dominio lacuerda.net para evitar SSRF.
     */
    public ChordSearchResult fetchChords(String url) {
        validateUrl(url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10_000)
                    .get();

            // Título y artista de la página
            String title = extractMeta(doc, "og:title", doc.title());
            String artist = extractMeta(doc, "og:description", "");

            // Contenido de acordes: buscar en los contenedores más comunes de lacuerda.net
            String chords = "";

            Element chordsEl = doc.selectFirst(
                ".tablatura, .acordes, .tab-content, #tablatura, #acordes, pre.tab, pre"
            );

            if (chordsEl != null) {
                chords = chordsEl.wholeText().trim();
            }

            if (chords.isEmpty()) {
                // Fallback: extraer todo el texto del main content
                Element main = doc.selectFirst("main, .contenido, #contenido, article");
                if (main != null) {
                    chords = main.wholeText().trim();
                }
            }

            return new ChordSearchResult(title, artist, url, chords);

        } catch (IOException e) {
            throw new RuntimeException("Error al obtener acordes de " + url + ": " + e.getMessage(), e);
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL no puede estar vacía");
        }
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            throw new IllegalArgumentException("URL inválida");
        }
        try {
            java.net.URI uri = java.net.URI.create(url);
            String host = uri.getHost();
            if (host == null || !host.endsWith("lacuerda.net")) {
                throw new IllegalArgumentException("Solo se permiten URLs de lacuerda.net");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("URL inválida: " + e.getMessage());
        }
    }

    private String extractMeta(Document doc, String property, String defaultValue) {
        Element meta = doc.selectFirst("meta[property=" + property + "], meta[name=" + property + "]");
        if (meta != null) {
            String content = meta.attr("content");
            if (!content.isBlank()) return content.trim();
        }
        return defaultValue;
    }
}

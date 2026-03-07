package com.xound.service;

import com.xound.model.ChordSearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChordSearchService {

    private static final String BASE_URL = "https://www.cifraclub.com.br";
    private static final String SOLR_URL = "https://solr.sscdn.co/cc/search";
    private static final String ALLOWED_HOST = "cifraclub.com.br";

    private final RestClient restClient;

    public ChordSearchService() {
        this.restClient = RestClient.builder()
                .baseUrl(SOLR_URL)
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();
    }

    /**
     * Busca canciones con acordes en Cifra Club usando su API de Solr.
     */
    public List<ChordSearchResult> search(String query) {
        List<ChordSearchResult> results = new ArrayList<>();

        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String jsonp = restClient.get()
                    .uri("?q={query}&rows=15", query.trim())
                    .retrieve()
                    .body(String.class);

            // El response es JSONP: suggest_callback({...})
            if (jsonp == null || !jsonp.contains("{")) return results;
            String json = jsonp.substring(jsonp.indexOf("{"), jsonp.lastIndexOf("}") + 1);

            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) parsed.get("response");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> docs = (List<Map<String, Object>>) response.get("docs");

            for (Map<String, Object> doc : docs) {
                String type = (String) doc.get("t");
                // t=2 son canciones, t=1 son artistas
                if (!"2".equals(type)) continue;

                String title = (String) doc.get("txt");
                String artist = (String) doc.get("art");
                String artistDns = (String) doc.get("dns");
                String songUrl = (String) doc.get("url");

                if (title == null || artistDns == null || songUrl == null) continue;

                String fullUrl = BASE_URL + "/" + artistDns + "/" + songUrl + "/";
                results.add(new ChordSearchResult(title, artist != null ? artist : "", fullUrl, null, null));

                if (results.size() >= 15) break;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar acordes: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Descarga el contenido de acordes de una URL de Cifra Club.
     * Solo acepta URLs del dominio cifraclub.com.br para evitar SSRF.
     */
    public ChordSearchResult fetchChords(String url) {
        validateUrl(url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15_000)
                    .get();

            // Titulo
            String title = doc.title().replace(" - Cifra Club", "").trim();
            String artist = "";

            // Extraer artista del titulo "Cancion - Artista - Cifra Club"
            Element h1 = doc.selectFirst("h1");
            if (h1 != null) {
                title = h1.text().trim();
            }
            Element artistEl = doc.selectFirst("h2 a, .cifra-composer a, a[href*='/']");
            if (artistEl != null && artistEl.text().length() < 100) {
                artist = artistEl.text().trim();
            }

            // Acordes en <pre>
            String chords = "";
            String tone = null;
            Element pre = doc.selectFirst("pre");
            if (pre != null) {
                // Extraer tonalidad del primer acorde <b> en el contenido
                Element firstChord = pre.selectFirst("b");
                if (firstChord != null) {
                    String chordText = firstChord.text().trim();
                    // Extraer solo la nota raíz (ej: "Am7" -> "Am", "D" -> "D", "F#m" -> "F#m")
                    Matcher toneMatcher = Pattern.compile("^([A-G][#b]?m?)").matcher(chordText);
                    if (toneMatcher.find()) {
                        tone = toneMatcher.group(1);
                    }
                }

                // Reemplazar <b> tags con el texto del acorde (mantener formato)
                chords = pre.html()
                        .replaceAll("<b>", "")
                        .replaceAll("</b>", "")
                        .replaceAll("<a[^>]*>", "")
                        .replaceAll("</a>", "")
                        .replaceAll("<[^>]+>", "")
                        .replaceAll("&amp;", "&")
                        .replaceAll("&lt;", "<")
                        .replaceAll("&gt;", ">")
                        .replaceAll("&nbsp;", " ")
                        .trim();
            }

            return new ChordSearchResult(title, artist, url, chords, tone);

        } catch (IOException e) {
            throw new RuntimeException("Error al obtener acordes de " + url + ": " + e.getMessage(), e);
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La URL no puede estar vacia");
        }
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            throw new IllegalArgumentException("URL invalida");
        }
        try {
            java.net.URI uri = java.net.URI.create(url);
            String host = uri.getHost();
            if (host == null || !host.endsWith(ALLOWED_HOST)) {
                throw new IllegalArgumentException("Solo se permiten URLs de " + ALLOWED_HOST);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("URL invalida: " + e.getMessage());
        }
    }
}

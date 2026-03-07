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

    private static final String BASE_URL = "https://www.guitartabs.cc";
    private static final String SEARCH_URL = BASE_URL + "/search.php?tabtype=chords&band=%s&song=%s";
    private static final String ALLOWED_HOST = "guitartabs.cc";

    /**
     * Busca canciones con acordes en guitartabs.cc y retorna lista de resultados.
     * El query se separa en artista y cancion para una busqueda mas precisa.
     */
    public List<ChordSearchResult> search(String query) {
        List<ChordSearchResult> results = new ArrayList<>();

        try {
            // Separar query en partes para band y song
            String[] parts = query.trim().split("\\s+", 2);
            String band = URLEncoder.encode(parts[0], StandardCharsets.UTF_8);
            String song = parts.length > 1 ? URLEncoder.encode(parts[1], StandardCharsets.UTF_8) : "";

            String searchUrl = String.format(SEARCH_URL, band, song);

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10_000)
                    .get();

            // guitartabs.cc muestra resultados con links a /tabs/
            Elements links = doc.select("a[href*=/tabs/]");

            String lastArtist = "";
            for (Element link : links) {
                String href = link.attr("href");
                String fullUrl = href.startsWith("http") ? href : BASE_URL + href;

                // Los links de artista son como /tabs/q/queen/
                // Los links de canciones son como /tabs/q/queen/bohemian_rhapsody_crd.html
                if (href.endsWith("/")) {
                    // Es un link de artista, guardar el nombre
                    lastArtist = link.text().trim();
                    continue;
                }

                // Solo incluir links que contengan _crd (chords)
                if (!href.contains("_crd")) continue;
                if (!fullUrl.contains(ALLOWED_HOST)) continue;

                String title = link.text().trim();
                results.add(new ChordSearchResult(title, lastArtist, fullUrl, null));

                if (results.size() >= 20) break;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al buscar acordes: " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Descarga el contenido de acordes de una URL especifica de guitartabs.cc.
     * Solo acepta URLs del dominio guitartabs.cc para evitar SSRF.
     */
    public ChordSearchResult fetchChords(String url) {
        validateUrl(url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10_000)
                    .get();

            // Titulo de la pagina
            String pageTitle = doc.title();
            String title = pageTitle;
            String artist = "";

            // Extraer titulo del h3.content_h
            Element titleEl = doc.selectFirst("h3.content_h");
            if (titleEl != null) {
                title = titleEl.text().trim();
            }

            // Extraer el contenido de acordes del segundo <pre> (el primero es el titulo)
            String chords = "";
            Elements preElements = doc.select("pre");

            for (Element pre : preElements) {
                String text = pre.wholeText().trim();
                // El pre con contenido real tiene mas de unas pocas lineas
                if (text.length() > 50 && !text.contains("<h3")) {
                    // Limpiar tags HTML residuales del texto
                    chords = Jsoup.parse(pre.html()).wholeText().trim();
                    break;
                }
            }

            // Si no encontramos en pre, buscar en .tabcont
            if (chords.isEmpty()) {
                Element tabcont = doc.selectFirst(".tabcont");
                if (tabcont != null) {
                    Element prInTab = tabcont.selectFirst("pre");
                    if (prInTab != null) {
                        chords = Jsoup.parse(prInTab.html()).wholeText().trim();
                    }
                }
            }

            // Extraer artista del contenido si aparece "Artist: ..."
            if (!chords.isEmpty()) {
                for (String line : chords.split("\n")) {
                    if (line.trim().toLowerCase().startsWith("artist:")) {
                        artist = line.substring(line.indexOf(":") + 1).trim();
                        break;
                    }
                }
            }

            return new ChordSearchResult(title, artist, url, chords);

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

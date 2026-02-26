package com.xound.controller;

import com.xound.model.ChordSearchResult;
import com.xound.service.ChordSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chords")
@Tag(name = "Búsqueda de Acordes", description = "Busca y descarga acordes de lacuerda.net")
public class ChordSearchController {

    private final ChordSearchService chordSearchService;

    public ChordSearchController(ChordSearchService chordSearchService) {
        this.chordSearchService = chordSearchService;
    }

    /**
     * Busca canciones en lacuerda.net.
     * GET /api/chords/search?q=nombre+cancion
     * Retorna lista de resultados con título, artista y URL (sin acordes aún).
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar en lacuerda.net",
               description = "Busca canciones por nombre y retorna lista de resultados con sus URLs")
    public ResponseEntity<?> search(
            @Parameter(description = "Nombre de la canción o artista a buscar")
            @RequestParam String q) {

        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'q' es requerido"));
        }

        List<ChordSearchResult> results = chordSearchService.search(q.trim());
        return ResponseEntity.ok(results);
    }

    /**
     * Descarga el contenido de acordes de una URL específica de lacuerda.net.
     * GET /api/chords/fetch?url=https://www.lacuerda.net/...
     * Retorna título, artista y los acordes en texto plano.
     */
    @GetMapping("/fetch")
    @Operation(summary = "Obtener acordes de una URL",
               description = "Descarga el contenido de acordes de una URL de lacuerda.net para guardarla en la canción")
    public ResponseEntity<?> fetchChords(
            @Parameter(description = "URL completa de la canción en lacuerda.net")
            @RequestParam String url) {

        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El parámetro 'url' es requerido"));
        }

        try {
            ChordSearchResult result = chordSearchService.fetchChords(url);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

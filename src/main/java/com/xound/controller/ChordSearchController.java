package com.xound.controller;

import com.xound.exception.BadRequestException;
import com.xound.model.ChordSearchResult;
import com.xound.service.ChordSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chords")
@Tag(name = "Busqueda de Acordes", description = "Busca y descarga acordes de lacuerda.net")
public class ChordSearchController {

    private final ChordSearchService chordSearchService;

    public ChordSearchController(ChordSearchService chordSearchService) {
        this.chordSearchService = chordSearchService;
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar en lacuerda.net",
               description = "Busca canciones por nombre y retorna lista de resultados con sus URLs")
    public ResponseEntity<List<ChordSearchResult>> search(
            @Parameter(description = "Nombre de la cancion o artista a buscar")
            @RequestParam String q) {

        if (q == null || q.isBlank()) {
            throw new BadRequestException("El parametro 'q' es requerido");
        }

        return ResponseEntity.ok(chordSearchService.search(q.trim()));
    }

    @GetMapping("/fetch")
    @Operation(summary = "Obtener acordes de una URL",
               description = "Descarga el contenido de acordes de una URL de lacuerda.net para guardarla en la cancion")
    public ResponseEntity<ChordSearchResult> fetchChords(
            @Parameter(description = "URL completa de la cancion en lacuerda.net")
            @RequestParam String url) {

        if (url == null || url.isBlank()) {
            throw new BadRequestException("El parametro 'url' es requerido");
        }

        return ResponseEntity.ok(chordSearchService.fetchChords(url));
    }
}

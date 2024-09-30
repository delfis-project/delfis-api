/*
 * Classe WordSearchController
 * Controller da entidade WordSearch
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 */

package goldenage.delfis.api.mongo.controller;

import goldenage.delfis.api.mongo.model.WordSearch;
import goldenage.delfis.api.mongo.service.WordSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/word-search")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    public WordSearchController(WordSearchService wordSearchService) {
        this.wordSearchService = wordSearchService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os caça-palavras", description = "Retorna uma lista de todos os caça-palavras gerados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de caça-palavras encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WordSearch.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum caça-palavras encontrado", content = @Content)
    })
    public ResponseEntity<List<WordSearch>> getWordSearches() {
        List<WordSearch> wordSearches = wordSearchService.getWordSearches();
        if (wordSearches != null && !wordSearches.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(wordSearches);

        throw new EntityNotFoundException("Nenhum caça-palavras encontrado.");
    }

    @PostMapping("/generate")
    @Operation(summary = "Gerar um novo caça-palavras", description = "Gera um novo caça-palavras com o tamanho de grid especificado e a lista de palavras fornecida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caça-palavras gerado com sucesso", content = @Content(schema = @Schema(implementation = WordSearch.class))),
    })
    public ResponseEntity<WordSearch> generateWordSearch(
            @RequestParam(name = "gridSize") int gridSize,
            @RequestParam(name = "words") List<String> wordList) {
        if (wordList == null || wordList.isEmpty()) throw new DataIntegrityViolationException("Lista de palavras vazia.");
        if (gridSize < 3) throw new DataIntegrityViolationException("Grid deve ser de no mínimo 4x4.");

        return ResponseEntity.status(HttpStatus.OK).body(wordSearchService.generateWordSearch(gridSize, wordList));
    }
}

/*
 * Classe SudokuController
 * Controller da entidade Sudoku
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 */

package goldenage.delfis.api.mongo.controller;

import goldenage.delfis.api.mongo.model.Sudoku;
import goldenage.delfis.api.mongo.service.SudokuService;
import goldenage.delfis.api.mongo.model.SudokuGenerator;
import goldenage.delfis.api.mongo.model.SudokuType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sudoku")
@Schema(description = "Controlador responsável pela gestão de Sudokus")
public class SudokuController {

    private final SudokuService sudokuService;

    public SudokuController(SudokuService sudokuService) {
        this.sudokuService = sudokuService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os Sudokus", description = "Retorna uma lista de todos os Sudokus gerados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Sudokus encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sudoku.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum Sudoku encontrado", content = @Content)
    })
    public ResponseEntity<List<Sudoku>> getSudokues() {
        List<Sudoku> sudokues = sudokuService.getSudokus();
        if (sudokues != null && !sudokues.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(sudokues);
        }
        throw new EntityNotFoundException("Nenhum Sudoku encontrado.");
    }

    @PostMapping("/generate")
    @Operation(summary = "Gerar um novo Sudoku", description = "Gera um novo Sudoku com o tipo especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sudoku gerado com sucesso", content = @Content(schema = @Schema(implementation = Sudoku.class))),
    })
    public ResponseEntity<Sudoku> generateSudoku() {
        Sudoku generatedSudoku = SudokuGenerator.generateRandomSudoku(SudokuType.SIXBYSIX);
        Sudoku savedSudoku = sudokuService.saveSudoku(generatedSudoku);
        return ResponseEntity.status(HttpStatus.OK).body(savedSudoku);
    }
}

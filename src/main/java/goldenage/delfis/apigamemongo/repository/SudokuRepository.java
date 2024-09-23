/*
 * Classe SudokuRepository
 * Repository da entidade Sudoku
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 * */

package goldenage.delfis.apigamemongo.repository;

import goldenage.delfis.apigamemongo.model.Sudoku;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SudokuRepository extends MongoRepository<Sudoku, String> {
}

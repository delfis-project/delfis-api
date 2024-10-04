/*
 * Classe SudokuRepository
 * Repository da entidade Sudoku
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 * */

package goldenage.delfis.api.mongo.repository;

import goldenage.delfis.api.mongo.model.Sudoku;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SudokuRepository extends MongoRepository<Sudoku, String> {
    @Aggregation(pipeline = {
            "{ '$project': { 'filledCellsCount': { '$sum': { '$map': { 'input': '$board', 'as': 'row', 'in': { '$size': { '$filter': { 'input': '$$row', 'as': 'cell', 'cond': { '$ne': ['$$cell', ''] } } } } } } } } }",
            "{ '$sort': { 'filledCellsCount': -1 } }"
    })
    List<Sudoku> findSudokusWithMostFilledCells();
}

/*
 * Classe WordSearchRepository
 * Repository da entidade WordSearch
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 * */

package goldenage.delfis.api.mongo.repository;

import goldenage.delfis.api.mongo.model.WordSearch;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordSearchRepository extends MongoRepository<WordSearch, String> {
    /**
     * Conta as ocorrências de uma palavra específica em todas as grades de WordSearch.
     * @param word A palavra a ser contada.
     * @return O total de ocorrências da palavra.
     */
    @Aggregation(pipeline = {
            "{ '$unwind': '$words' }",
            "{ '$match': { 'words': ?0 } }",
            "{ '$count': 'totalOccurrences' }"
    })
    Integer countOccurrencesOfWord(String word);
}
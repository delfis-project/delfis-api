/*
 * Classe WordSearchRepository
 * Repository da entidade WordSearch
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 * */

package goldenage.delfis.apigamemongo.repository;

import goldenage.delfis.apigamemongo.model.WordSearch;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordSearchRepository extends MongoRepository<WordSearch, String> {
}

/*
 * Classe WordSearchService
 * Service da entidade WordSearch
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 * */

package goldenage.delfis.apigamemongo.service;

import goldenage.delfis.apigamemongo.model.WordSearch;
import goldenage.delfis.apigamemongo.repository.WordSearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordSearchService {
    private final WordSearchRepository wordSearchRepository;

    public WordSearchService(WordSearchRepository wordSearchRepository) {
        this.wordSearchRepository = wordSearchRepository;
    }

    /**
     * @return todos os caça-palavras do banco.
     */
    public List<WordSearch> getWordSearches() {
        List<WordSearch> wordSearchs = wordSearchRepository.findAll();
        return wordSearchs.isEmpty() ? null : wordSearchs;
    }

    /**
     * @return caça-palavras inserido.
     */
    public WordSearch saveWordSearch(WordSearch wordSearch) {
        return wordSearchRepository.save(wordSearch);
    }
}

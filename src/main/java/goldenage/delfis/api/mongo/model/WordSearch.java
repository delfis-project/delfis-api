/*
 * Classe WordSearch
 * Model da entidade WordSearch
 * Autor: João Diniz Araujo
 * Data: 16/09/2024
 * */

package goldenage.delfis.api.mongo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Schema(description = "Representa um jogo de caça-palavras.")
@Document
public class WordSearch {

    private static final String ALL_CAP_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Schema(description = "Grid do jogo representado como uma string onde cada linha é separada por quebras de linha.", example = "A B C D\nE F G H\nI J K L\nM N O P")
    private String grid;

    @Min(value = 4)
    @Schema(description = "Tamanho do grid do jogo, que é uma dimensão do grid quadrado.", example = "10")
    private int gridSize;

    @Schema(description = "Lista de palavras a serem encontradas no caça-palavras.", example = "[\"JAVA\", \"SPRING\", \"JPA\"]")
    private List<String> words;

    private static class Coordinate {
        int x;
        int y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public WordSearch(int gridSize, List<String> wordList) {
        this.gridSize = gridSize;

        this.words = wordList;

        char[][] grid = generateGrid(gridSize, wordList);
        String gridToString = "";
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gridToString += grid[i][j] + " ";
            }
            gridToString += "\r\n";
        }
        this.grid = gridToString;
    }

    /**
     * Gera o grid do caça-palavras preenchendo com palavras e letras aleatórias.
     *
     * @param gridSize Tamanho do grid (dimensão do grid quadrado).
     * @param words Lista de palavras a serem inseridas no grid.
     * @return Um array bidimensional de caracteres representando o grid do caça-palavras.
     */
    private static char[][] generateGrid(int gridSize, List<String> words) {
        List<Coordinate> coordinates = new ArrayList<>();
        char[][] contents = new char[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                coordinates.add(new Coordinate(i, j));
                contents[i][j] = '_';
            }
        }

        for (String word : words) {
            Collections.shuffle(coordinates);
            for (Coordinate coordinate : coordinates) {
                int x = coordinate.x;
                int y = coordinate.y;
                WordSearchDirection selectedWordSearchDirection = getDirectionForFit(contents, word, coordinate);
                if (selectedWordSearchDirection != null) {
                    switch (selectedWordSearchDirection) {
                        case HORIZONTAL -> {
                            for (char c : word.toCharArray()) {
                                contents[x][y++] = c;
                            }
                        }
                        case VERTICAL -> {
                            for (char c : word.toCharArray()) {
                                contents[x++][y] = c;
                            }
                        }
                        case DIAGONAL -> {
                            for (char c : word.toCharArray()) {
                                contents[x++][y++] = c;
                            }
                        }
                        case HORIZONTAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                contents[x][y--] = c;
                            }
                        }
                        case VERTICAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                contents[x--][y] = c;
                            }
                        }
                        case DIAGONAL_INVERSE -> {
                            for (char c : word.toCharArray()) {
                                contents[x--][y--] = c;
                            }
                        }
                    }
                    break;
                }
            }
        }
        randomFillGrid(contents);
        return contents;
    }

    /**
     * Preenche as células restantes do grid com letras aleatórias.
     *
     * @param contents O grid do caça-palavras que será preenchido com letras aleatórias.
     */
    private static void randomFillGrid(char[][] contents) {
        int gridSize = contents[0].length;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (contents[i][j] == '_') {
                    int randomIndex = ThreadLocalRandom.current().nextInt(0, ALL_CAP_LETTERS.length());
                    contents[i][j] = ALL_CAP_LETTERS.charAt(randomIndex);
                }
            }
        }
    }

    /**
     * Determina a direção em que uma palavra pode ser inserida no grid, verificando se a palavra se encaixa.
     *
     * @param contents O grid do caça-palavras.
     * @param word A palavra a ser inserida.
     * @param coordinate A coordenada inicial onde a palavra será tentada.
     * @return A direção selecionada para a inserção da palavra, ou null se a palavra não se encaixar em nenhuma direção.
     */
    private static WordSearchDirection getDirectionForFit(char[][] contents, String word, Coordinate coordinate) {
        List<WordSearchDirection> wordSearchDirections = Arrays.asList(WordSearchDirection.values());
        Collections.shuffle(wordSearchDirections);
        for (WordSearchDirection wordSearchDirection : wordSearchDirections) {
            if (doesFit(contents, word, coordinate, wordSearchDirection)) {
                return wordSearchDirection;
            }
        }
        return null;
    }

    /**
     * Verifica se uma palavra se encaixa no grid numa determinada coordenada e direção.
     *
     * @param contents O grid do caça-palavras.
     * @param word A palavra a ser verificada.
     * @param coordinate A coordenada inicial.
     * @param wordSearchDirection A direção para verificar o encaixe da palavra.
     * @return true se a palavra se encaixa no grid na coordenada e direção especificadas; false caso contrário.
     */
    private static boolean doesFit(char[][] contents, String word, Coordinate coordinate, WordSearchDirection wordSearchDirection) {
        int gridSize = contents[0].length;
        int wordLength = word.length();
        switch (wordSearchDirection) {
            case HORIZONTAL -> {
                if (coordinate.y + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x][coordinate.y + i];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
            case VERTICAL -> {
                if (coordinate.x + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x + i][coordinate.y];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
            case DIAGONAL -> {
                if (coordinate.x + wordLength > gridSize || coordinate.y + wordLength > gridSize) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x + i][coordinate.y + i];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
            case HORIZONTAL_INVERSE -> {
                if (coordinate.y < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x][coordinate.y - i];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
            case VERTICAL_INVERSE -> {
                if (coordinate.x < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x - i][coordinate.y];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
            case DIAGONAL_INVERSE -> {
                if (coordinate.x < wordLength || coordinate.y < wordLength) return false;
                for (int i = 0; i < wordLength; i++) {
                    char letter = contents[coordinate.x - i][coordinate.y - i];
                    if (letter != '_' && letter != word.charAt(i)) return false;
                }
            }
        }
        return true;
    }

    /**
     * Verifica se a palavra selecionada pelo jogador está correta em qualquer direção.
     *
     * @param startX Coordenada X de início da palavra no grid.
     * @param startY Coordenada Y de início da palavra no grid.
     * @param word A palavra que o jogador selecionou.
     * @return true se a palavra selecionada está correta em qualquer direção, false caso contrário.
     */
    public boolean isWordCorrect(int startX, int startY, String word) {
        int wordLength = word.length();

        char[][] gridArray = convertGridToArray();

        if (checkHorizontal(gridArray, startX, startY, word, wordLength)) return true;
        if (checkVertical(gridArray, startX, startY, word, wordLength)) return true;
        if (checkDiagonal(gridArray, startX, startY, word, wordLength)) return true;

        if (checkHorizontalInverse(gridArray, startX, startY, word, wordLength)) return true;
        if (checkVerticalInverse(gridArray, startX, startY, word, wordLength)) return true;
        return checkDiagonalInverse(gridArray, startX, startY, word, wordLength);
    }

    /**
     * Converte o grid em string para uma matriz bidimensional de caracteres.
     *
     * @return Matriz bidimensional representando o grid.
     */
    private char[][] convertGridToArray() {
        String[] rows = this.grid.split("\r\n");
        char[][] gridArray = new char[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            String[] columns = rows[i].split(" ");
            for (int j = 0; j < gridSize; j++) {
                gridArray[i][j] = columns[j].charAt(0);
            }
        }
        return gridArray;
    }

    // Verifica horizontalmente da esquerda para a direita
    private boolean checkHorizontal(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startY + wordLength > gridSize) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX][startY + i] != word.charAt(i)) return false;
        }
        return true;
    }

    // Verifica verticalmente de cima para baixo
    private boolean checkVertical(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startX + wordLength > gridSize) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX + i][startY] != word.charAt(i)) return false;
        }
        return true;
    }

    // Verifica diagonalmente de cima para baixo, da esquerda para a direita
    private boolean checkDiagonal(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startX + wordLength > gridSize || startY + wordLength > gridSize) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX + i][startY + i] != word.charAt(i)) return false;
        }
        return true;
    }

    // Verifica horizontalmente da direita para a esquerda
    private boolean checkHorizontalInverse(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startY - wordLength < 0) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX][startY - i] != word.charAt(i)) return false;
        }
        return true;
    }

    // Verifica verticalmente de baixo para cima
    private boolean checkVerticalInverse(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startX - wordLength < 0) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX - i][startY] != word.charAt(i)) return false;
        }
        return true;
    }

    // Verifica diagonalmente de baixo para cima, da direita para a esquerda
    private boolean checkDiagonalInverse(char[][] gridArray, int startX, int startY, String word, int wordLength) {
        if (startX - wordLength < 0 || startY - wordLength < 0) return false;
        for (int i = 0; i < wordLength; i++) {
            if (gridArray[startX - i][startY - i] != word.charAt(i)) return false;
        }
        return true;
    }
}

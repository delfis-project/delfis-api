/*
 * Classe Sudoku
 * Model da entidade Sudoku
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 * */

package goldenage.delfis.apigamemongo.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Modelo que representa um tabuleiro de Sudoku")
@Document
public class Sudoku {

    @ArraySchema(schema = @Schema(description = "Tabuleiro do Sudoku com as posições preenchidas e vazias", example = "[[\"5\", \"3\", \"\", ...], [...]]"))
    protected String[][] board;

    @ArraySchema(schema = @Schema(description = "Indica se uma posição do tabuleiro é mutável", example = "[[true, false, ...], [...]]"))
    protected boolean[][] mutable;

    @Schema(description = "Número de linhas do tabuleiro", example = "9")
    private int ROWS;

    @Schema(description = "Número de colunas do tabuleiro", example = "9")
    private int COLUMNS;

    @Schema(description = "Largura da caixa do Sudoku", example = "3")
    private int BOXWIDTH;

    @Schema(description = "Altura da caixa do Sudoku", example = "3")
    private int BOXHEIGHT;

    @ArraySchema(schema = @Schema(description = "Valores válidos para o Sudoku", example = "[\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\"]"))
    private String[] VALIDVALUES;

    public Sudoku(
            @Schema(description = "Número de linhas do tabuleiro", example = "9") int rows,
            @Schema(description = "Número de colunas do tabuleiro", example = "9") int columns,
            @Schema(description = "Largura da caixa do Sudoku", example = "3") int boxWidth,
            @Schema(description = "Altura da caixa do Sudoku", example = "3") int boxHeight,
            @ArraySchema(schema = @Schema(description = "Valores válidos do Sudoku", example = "[\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\"]")) String[] validValues
    ) {
        this.ROWS = rows;
        this.COLUMNS = columns;
        this.BOXWIDTH = boxWidth;
        this.BOXHEIGHT = boxHeight;
        this.VALIDVALUES = validValues;
        this.board = new String[ROWS][COLUMNS];
        this.mutable = new boolean[ROWS][COLUMNS];
        initializeBoard();
        initializeMutableSlots();
    }

    public Sudoku(
            @Schema(description = "Outro tabuleiro de Sudoku para copiar os valores") Sudoku puzzle) {
        this.ROWS = puzzle.ROWS;
        this.COLUMNS = puzzle.COLUMNS;
        this.BOXWIDTH = puzzle.BOXWIDTH;
        this.BOXHEIGHT = puzzle.BOXHEIGHT;
        this.VALIDVALUES = puzzle.VALIDVALUES;
        this.board = new String[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(puzzle.board[r], 0, board[r], 0, COLUMNS);
        }
        this.mutable = new boolean[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(puzzle.mutable[r], 0, this.mutable[r], 0, COLUMNS);
        }
    }

    @Schema(description = "Retorna o número de linhas do tabuleiro", example = "9")
    public int getNumRows() {
        return this.ROWS;
    }

    @Schema(description = "Retorna o número de colunas do tabuleiro", example = "9")
    public int getNumColumns() {
        return this.COLUMNS;
    }

    @Schema(description = "Retorna a largura da caixa do Sudoku", example = "3")
    public int getBoxWidth() {
        return this.BOXWIDTH;
    }

    @Schema(description = "Retorna a altura da caixa do Sudoku", example = "3")
    public int getBoxHeight() {
        return this.BOXHEIGHT;
    }

    @ArraySchema(schema = @Schema(description = "Valores válidos para o Sudoku", example = "[\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\"]"))
    public String[] getValidValues() {
        return this.VALIDVALUES;
    }

    @Schema(description = "Faz uma jogada no tabuleiro")
    public void makeMove(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col,
            @Schema(description = "Valor a ser inserido", example = "5") String value,
            @Schema(description = "Indica se a célula é mutável", example = "true") boolean isMutable
    ) {
        if (this.isValidValue(value) && this.isValidMove(row, col, value) && this.isSlotMutable(row, col)) {
            this.board[row][col] = value;
            this.mutable[row][col] = isMutable;
        }
    }

    @Schema(description = "Verifica se o movimento é válido")
    public boolean isValidMove(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col,
            @Schema(description = "Valor a ser verificado", example = "5") String value
    ) {
        return this.inRange(row, col) && !this.numInCol(col, value) && !this.numInRow(row, value) && !this.numInBox(row, col, value);
    }

    @Schema(description = "Verifica se o valor já está presente na coluna")
    public boolean numInCol(
            @Schema(description = "Número da coluna", example = "0") int col,
            @Schema(description = "Valor a ser verificado", example = "5") String value
    ) {
        if (col <= this.COLUMNS) {
            for (int row = 0; row < this.ROWS; row++) {
                if (this.board[row][col].equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Schema(description = "Verifica se o valor já está presente na linha")
    public boolean numInRow(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Valor a ser verificado", example = "5") String value
    ) {
        if (row <= this.ROWS) {
            for (int col = 0; col < this.COLUMNS; col++) {
                if (this.board[row][col].equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Schema(description = "Verifica se o valor já está presente na caixa")
    public boolean numInBox(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col,
            @Schema(description = "Valor a ser verificado", example = "5") String value
    ) {
        if (this.inRange(row, col)) {
            int boxRow = row / this.BOXHEIGHT;
            int boxCol = col / this.BOXWIDTH;

            int startingRow = (boxRow * this.BOXHEIGHT);
            int startingCol = (boxCol * this.BOXWIDTH);

            for (int r = startingRow; r <= (startingRow + this.BOXHEIGHT) - 1; r++) {
                for (int c = startingCol; c <= (startingCol + this.BOXWIDTH) - 1; c++) {
                    if (this.board[r][c].equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Schema(description = "Verifica se a célula está disponível para preenchimento")
    public boolean isSlotAvailable(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col
    ) {
        return (this.inRange(row, col) && this.board[row][col].equals("") && this.isSlotMutable(row, col));
    }

    @Schema(description = "Verifica se a célula é mutável")
    public boolean isSlotMutable(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col
    ) {
        return this.mutable[row][col];
    }

    @Schema(description = "Retorna o valor presente na célula")
    public String getValue(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col
    ) {
        if (this.inRange(row, col)) {
            return this.board[row][col];
        }
        return "";
    }

    @Schema(description = "Retorna o tabuleiro do Sudoku")
    public String[][] getBoard() {
        return this.board;
    }

    @Schema(description = "Verifica se o valor é válido")
    private boolean isValidValue(
            @Schema(description = "Valor a ser verificado", example = "5") String value
    ) {
        for (String str : this.VALIDVALUES) {
            if (str.equals(value)) return true;
        }
        return false;
    }

    @Schema(description = "Verifica se a célula está dentro do intervalo")
    public boolean inRange(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col
    ) {
        return row <= this.ROWS && col <= this.COLUMNS && row >= 0 && col >= 0;
    }

    @Schema(description = "Verifica se o tabuleiro está completo")
    public boolean boardFull() {
        for (int r = 0; r < this.ROWS; r++) {
            for (int c = 0; c < this.COLUMNS; c++) {
                if (this.board[r][c].equals("")) return false;
            }
        }
        return true;
    }

    @Schema(description = "Esvazia a célula do tabuleiro")
    public void makeSlotEmpty(
            @Schema(description = "Número da linha", example = "0") int row,
            @Schema(description = "Número da coluna", example = "0") int col
    ) {
        this.board[row][col] = "";
    }

    @Override
    @Schema(description = "Retorna o tabuleiro como string")
    public String toString() {
        String str = "Game Board:\n";
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                str += this.board[row][col] + " ";
            }
            str += "\n";
        }
        return str + "\n";
    }

    @Schema(description = "Inicializa o tabuleiro com células vazias")
    private void initializeBoard() {
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                this.board[row][col] = "";
            }
        }
    }

    @Schema(description = "Inicializa as células mutáveis")
    private void initializeMutableSlots() {
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                this.mutable[row][col] = true;
            }
        }
    }
}

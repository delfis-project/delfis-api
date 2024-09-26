/*
 * Classe Sudoku
 * Model da entidade Sudoku
 * Autor: João Diniz Araujo
 * Data: 23/09/2024
 * */

package goldenage.delfis.api.mongo.model;

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

    public Sudoku(int rows, int columns, int boxWidth, int boxHeight, String[] validValues) {
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

    public Sudoku(Sudoku puzzle) {
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

    public void makeMove(int row, int col, String value, boolean isMutable) {
        if (this.isValidValue(value) && this.isValidMove(row, col, value) && this.isSlotMutable(row, col)) {
            this.board[row][col] = value;
            this.mutable[row][col] = isMutable;
        }
    }

    public boolean isValidMove(int row, int col, String value) {
        return this.inRange(row, col) && !this.numInCol(col, value) && !this.numInRow(row, value) && !this.numInBox(row, col, value);
    }

    public boolean numInCol(int col, String value) {
        if (col <= this.COLUMNS) {
            for (int row = 0; row < this.ROWS; row++) {
                if (this.board[row][col].equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean numInRow(int row, String value) {
        if (row <= this.ROWS) {
            for (int col = 0; col < this.COLUMNS; col++) {
                if (this.board[row][col].equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean numInBox(int row, int col, String value) {
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

    public boolean isSlotAvailable(int row, int col) {
        return (this.inRange(row, col) && this.board[row][col].equals("") && this.isSlotMutable(row, col));
    }

    public boolean isSlotMutable(int row, int col) {
        return this.mutable[row][col];
    }

    public String getValue(int row, int col) {
        if (this.inRange(row, col)) {
            return this.board[row][col];
        }
        return "";
    }

    private boolean isValidValue(String value) {
        for (String str : this.VALIDVALUES) {
            if (str.equals(value)) return true;
        }
        return false;
    }

    public boolean inRange(int row, int col) {
        return row <= this.ROWS && col <= this.COLUMNS && row >= 0 && col >= 0;
    }

    public boolean boardFull() {
        for (int r = 0; r < this.ROWS; r++) {
            for (int c = 0; c < this.COLUMNS; c++) {
                if (this.board[r][c].equals("")) return false;
            }
        }
        return true;
    }

    public void makeSlotEmpty(int row, int col) {
        this.board[row][col] = "";
    }

    private void initializeBoard() {
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                this.board[row][col] = "";
            }
        }
    }

    private void initializeMutableSlots() {
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                this.mutable[row][col] = true;
            }
        }
    }

    public int getNumRows() {
        return this.ROWS;
    }

    public int getNumColumns() {
        return this.COLUMNS;
    }

    public int getBoxWidth() {
        return this.BOXWIDTH;
    }

    public int getBoxHeight() {
        return this.BOXHEIGHT;
    }

    public String[] getValidValues() {
        return this.VALIDVALUES;
    }

    @Override
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
}

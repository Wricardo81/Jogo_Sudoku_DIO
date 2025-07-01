package com.sudoku;

public class SudokuSolver {
    private final int SIZE = 9;
    private final int BLOCK_SIZE = 3;

    // Método principal para resolver o tabuleiro
    public boolean solve(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) { // Encontrou uma célula vazia
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num; // Tenta o número
                            if (solve(board)) { // Recorre para a próxima célula
                                return true; // Se a recursão retornar true, encontramos a solução
                            } else {
                                board[row][col] = 0; // Backtrack: desfaz a tentativa
                            }
                        }
                    }
                    return false; // Nenhum número funcionou para esta célula, backtrack
                }
            }
        }
        return true; // Se nenhuma célula vazia foi encontrada, o tabuleiro está resolvido
    }

    // Verifica se um número pode ser colocado em uma determinada posição
    private boolean isValid(int[][] board, int row, int col, int num) {
        // Verifica a linha
        for (int c = 0; c < SIZE; c++) {
            if (board[row][c] == num) {
                return false;
            }
        }

        // Verifica a coluna
        for (int r = 0; r < SIZE; r++) {
            if (board[r][col] == num) {
                return false;
            }
        }

        // Verifica o bloco 3x3
        int startRow = row - (row % BLOCK_SIZE);
        int startCol = col - (col % BLOCK_SIZE);
        for (int r = startRow; r < startRow + BLOCK_SIZE; r++) {
            for (int c = startCol; c < startCol + BLOCK_SIZE; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // Opcional: Contar soluções (mais complexo, necessário para geração de puzzles únicos)
    // public int countSolutions(int[][] board) { ... }
}
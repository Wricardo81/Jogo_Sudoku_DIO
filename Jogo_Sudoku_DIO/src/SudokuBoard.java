package com.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SudokuBoard {
    private final int[][] board;
    private final boolean[][] initialCells;
    private final int SIZE = 9;

    public SudokuBoard() {
        this.board = new int[SIZE][SIZE];
        this.initialCells = new boolean[SIZE][SIZE];
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    public void setValue(int row, int col, int value) {
        if (!initialCells[row][col]) { // Só permite alterar se não for uma célula inicial
            board[row][col] = value;
        }
    }

    public boolean isInitialCell(int row, int col) {
        return initialCells[row][col];
    }

    // --- Métodos de Validação ---
    public boolean isValidMove(int row, int col, int value) {
        if (value == 0) return true; // 0 significa célula vazia, sempre válido para ser limpa

        // Verifica a linha
        for (int c = 0; c < SIZE; c++) {
            if (c != col && board[row][c] == value) { // Ignora a própria célula para a verificação
                return false;
            }
        }

        // Verifica a coluna
        for (int r = 0; r < SIZE; r++) {
            if (r != row && board[r][col] == value) { // Ignora a própria célula para a verificação
                return false;
            }
        }

        // Verifica o bloco 3x3
        int BLOCK_SIZE = 3;
        int startRow = row - (row % BLOCK_SIZE);
        int startCol = col - (col % BLOCK_SIZE);
        for (int r = startRow; r < startRow + BLOCK_SIZE; r++) {
            for (int c = startCol; c < startCol + BLOCK_SIZE; c++) {
                if (r != row && c != col && board[r][c] == value) { // Ignora a própria célula
                    return false;
                }
            }
        }
        return true;
    }

    // Verifica se todo o tabuleiro está preenchido e correto
    public boolean isSolved() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0 || !isValidMove(r, c, board[r][c])) {
                    return false;
                }
            }
        }
        return true; // Se chegou aqui, está cheio e válido
    }

    // --- Geração e Resolução do Tabuleiro (Lógica Central) ---

    // Gera um novo quebra-cabeça de Sudoku
    public void generateNewPuzzle(int difficulty) {
        // 1. Zera o tabuleiro
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = 0;
                initialCells[r][c] = false;
            }
        }

        // 2. Gera um tabuleiro Sudoku COMPLETO e VÁLIDO usando backtracking
        //    (Você precisará da lógica de solve() para isso)
        SudokuSolver solver = new SudokuSolver();
        solver.solve(board); // Preenche o 'board' com uma solução completa

        // 3. Remove células para criar o quebra-cabeça
        int cellsToRemove = 0;
        switch (difficulty) {
            case 1: cellsToRemove = 40; break; // Fácil
            case 2: cellsToRemove = 50; break; // Médio
            case 3: cellsToRemove = 60; break; // Difícil
            default: cellsToRemove = 45; break; // Padrão
        }

        Random random = new Random();
        int removedCount = 0;
        while (removedCount < cellsToRemove) {
            int r = random.nextInt(SIZE);
            int c = random.nextInt(SIZE);

            if (board[r][c] != 0) { // Se a célula não estiver vazia
                int tempValue = board[r][c];
                board[r][c] = 0; // Tenta remover

                // Verificar se a remoção mantém uma solução única
                // Este é um passo complexo. Simplificando por enquanto:
                // Para um jogo real, você precisaria de um contador de soluções
                // (por exemplo, outro método no SudokuSolver que retorna o número de soluções)
                // Se o número de soluções for > 1, não remova essa célula.

                // Por enquanto, apenas removemos e assumimos que a geração de puzzles únicos
                // com um solver básico pode ser um desafio.
                // Para uma implementação robusta, o SudokuSolver precisaria de um método
                // como `countSolutions(int[][] tempBoard)`

                // Se fosse para ter um contador de soluções:
                // if (solver.countSolutions(board) == 1) {
                //    initialCells[r][c] = true; // Marca como célula inicial removida
                //    removedCount++;
                // } else {
                //    board[r][c] = tempValue; // Se não for única, volta o valor
                // }
                initialCells[r][c] = true; // Marca como célula inicial que permanecerá vazia
                removedCount++;
            }
        }

        // As células que NÃO foram removidas são as células iniciais fixas
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != 0) {
                    initialCells[r][c] = true; // Estas são as dicas fixas
                } else {
                    initialCells[r][c] = false; // Estas são as células que o usuário deve preencher
                }
            }
        }
    }

    // Retorna uma cópia do tabuleiro para o solver, se necessário
    public int[][] getBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }

    // Define o tabuleiro com uma matriz fornecida (útil para o solver)
    public void setBoard(int[][] solvedBoard) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(solvedBoard[r], 0, board[r], 0, SIZE);
        }
    }
}
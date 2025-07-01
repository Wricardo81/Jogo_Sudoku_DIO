package com.sudoku;

import javax.swing.SwingUtilities;

public class SudokuGame {
    public static void main(String[] args) {
        // Garante que a GUI seja criada e atualizada na Thread de Despacho de Eventos do Swing
        SwingUtilities.invokeLater(() -> {
            com.sudoku.SudokuBoard board = new com.sudoku.SudokuBoard();
            com.sudoku.SudokuGUI gui = new com.sudoku.SudokuGUI(board);
            gui.display();
        });
    }
}
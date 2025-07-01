package com.sudoku;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI extends JFrame {
    private SudokuBoard sudokuBoard;
    private JTextField[][] cells;
    private final int SIZE = 9;

    public SudokuGUI(SudokuBoard board) {
        this.sudokuBoard = board;
        this.cells = new JTextField[SIZE][SIZE];

        setTitle("Jogo de Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        createBoardPanel();
        createControlPanel();

        pack(); // Ajusta o tamanho da janela aos componentes
        setLocationRelativeTo(null); // Centraliza a janela
    }

    public SudokuGUI(com.sudoku.SudokuBoard board) {
    }

    private void createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Margem externa

        Font cellFont = new Font("Arial", Font.BOLD, 24);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JTextField cell = new JTextField();
                cell.setFont(cellFont);
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setPreferredSize(new Dimension(50, 50)); // Tamanho preferido da célula

                // Define as bordas para separar os blocos 3x3
                Border border = new LineBorder(Color.LIGHT_GRAY, 1);
                if (r % 3 == 2 && r != 8) { // Linhas divisórias dos blocos
                    border = BorderFactory.createMatteBorder(1, 1, 3, 1, Color.BLACK);
                }
                if (c % 3 == 2 && c != 8) { // Colunas divisórias dos blocos
                    border = BorderFactory.createMatteBorder(1, 1, 1, 3, Color.BLACK);
                }
                if (r % 3 == 2 && r != 8 && c % 3 == 2 && c != 8) { // Cruzamento de divisórias
                    border = BorderFactory.createMatteBorder(1, 1, 3, 3, Color.BLACK);
                }
                cell.setBorder(border);


                final int row = r;
                final int col = c;
                cell.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        handleChange();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        handleChange();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        // Não usado para JTextFields
                    }

                    private void handleChange() {
                        handleCellInput(row, col, cell);
                    }
                });

                cells[r][c] = cell;
                boardPanel.add(cell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centraliza e dá espaçamento

        JButton newGameButton = new JButton("Novo Jogo");
        newGameButton.addActionListener(e -> handleNewGame());
        controlPanel.add(newGameButton);

        JButton checkButton = new JButton("Verificar");
        checkButton.addActionListener(e -> handleCheckSolution());
        controlPanel.add(checkButton);

        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener(e -> handleSolve());
        controlPanel.add(solveButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    // --- Métodos de Manipulação da GUI ---

    public void updateGUI() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int value = sudokuBoard.getValue(r, c);
                if (value == 0) {
                    cells[r][c].setText("");
                } else {
                    cells[r][c].setText(String.valueOf(value));
                }

                if (sudokuBoard.isInitialCell(r, c)) {
                    cells[r][c].setEditable(false);
                    cells[r][c].setBackground(new Color(220, 220, 220)); // Cinza claro para células iniciais
                    cells[r][c].setForeground(Color.BLACK); // Cor do texto para células iniciais
                } else {
                    cells[r][c].setEditable(true);
                    cells[r][c].setBackground(Color.WHITE);
                    cells[r][c].setForeground(Color.BLUE); // Cor do texto para células editáveis
                }
            }
        }
    }

    private void handleCellInput(int row, int col, JTextField cell) {
        if (sudokuBoard.isInitialCell(row, col)) {
            return; // Não permite alterar células iniciais
        }

        String text = cell.getText();
        int value = 0;
        try {
            if (!text.isEmpty()) {
                value = Integer.parseInt(text);
                if (value < 1 || value > 9) {
                    value = 0; // Ignora valores inválidos
                    cell.setText(""); // Limpa o campo
                }
            }
        } catch (NumberFormatException ex) {
            value = 0; // Limpa se não for um número
            cell.setText("");
        }

        sudokuBoard.setValue(row, col, value);

        // Feedback visual para o usuário enquanto digita
        if (value != 0 && !sudokuBoard.isValidMove(row, col, value)) {
            cell.setForeground(Color.RED);
        } else {
            cell.setForeground(Color.BLUE); // Cor padrão para user input
        }

        if (sudokuBoard.isSolved()) {
            JOptionPane.showMessageDialog(this, "Parabéns! Você resolveu o Sudoku!", "Vitória!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleNewGame() {
        // Nível de dificuldade simples por enquanto (pode ser um JComboBox)
        String[] difficulties = {"Fácil", "Médio", "Difícil"};
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Selecione a dificuldade:",
                "Novo Jogo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficulties,
                difficulties[0]);

        int difficultyLevel = 2; // Padrão Médio
        if (selected != null) {
            switch (selected) {
                case "Fácil": difficultyLevel = 1; break;
                case "Médio": difficultyLevel = 2; break;
                case "Difícil": difficultyLevel = 3; break;
            }
        }

        sudokuBoard.generateNewPuzzle(difficultyLevel);
        updateGUI();
    }

    private void handleCheckSolution() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!sudokuBoard.isInitialCell(r, c)) { // Apenas verifica células do usuário
                    int value = sudokuBoard.getValue(r, c);
                    if (value != 0 && !sudokuBoard.isValidMove(r, c, value)) {
                        cells[r][c].setForeground(Color.RED);
                    } else {
                        cells[r][c].setForeground(Color.BLUE);
                    }
                }
            }
        }
        if (sudokuBoard.isSolved()) {
            JOptionPane.showMessageDialog(this, "Parabéns! O Sudoku está correto!", "Verificação Completa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Ainda há erros ou células vazias.", "Verificação Completa", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleSolve() {
        SudokuSolver solver = new SudokuSolver();
        int[][] tempBoard = sudokuBoard.getBoard(); // Obter uma cópia do tabuleiro atual
        if (solver.solve(tempBoard)) {
            sudokuBoard.setBoard(tempBoard); // Atualiza o tabuleiro do jogo com a solução
            updateGUI();
            JOptionPane.showMessageDialog(this, "Sudoku resolvido!", "Solução Encontrada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível resolver este Sudoku.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void display() {
        // Inicializa o tabuleiro com um novo puzzle ao iniciar
        sudokuBoard.generateNewPuzzle(2); // Dificuldade média inicial
        updateGUI();
        setVisible(true);
    }
}
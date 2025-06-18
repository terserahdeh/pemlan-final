import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
        "gelandangan", "hirono", "janice", "kak ajis", "kak icut",
        "kayla", "lugakpeduli", "orel", "sheyla", "vara"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Match Cards Game");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    ArrayList<JButton> selectedCards = new ArrayList<>();
    Timer hideCardTimer;
    boolean gameReady = false;
    int matchCount = 2;

    int level = 1;
    int matchedSets = 0;
    int totalPairs = 0;

    MatchCards() {
        setupLevel(level);

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Level: " + level + " | Errors: " + errorCount);
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setFocusable(false);
            int index = i;
            tile.addActionListener(e -> handleCardClick(tile, index));
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(500, e -> hideCards());
        hideCardTimer.setRepeats(false);

        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }
        gameReady = false;
        restartButton.setEnabled(false);

        Timer startTimer = new Timer(2500, e -> {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
    void handleCardClick(JButton tile, int index) {
        if (!gameReady || selectedCards.contains(tile)) return;

        tile.setIcon(cardSet.get(index).cardImageIcon);
        selectedCards.add(tile);

        if (selectedCards.size() == matchCount) {
            boolean match = true;
            String firstName = cardSet.get(board.indexOf(selectedCards.get(0))).cardName;
            for (JButton btn : selectedCards) {
                String name = cardSet.get(board.indexOf(btn)).cardName;
                if (!name.equals(firstName)) {
                    match = false;
                    break;
                }
            }

            if (!match) {
                errorCount++;
                textLabel.setText("Level: " + level + " | Errors: " + errorCount);
                hideCardTimer.start();
            } else {
                matchedSets++;
                if (matchedSets == totalPairs) {
                    if (level == 5) {
                        JOptionPane.showMessageDialog(frame, "Selamat! Anda berhasil menyelesaikan semua level.");
                    } else {
                        level++;
                        JOptionPane.showMessageDialog(frame, "Level " + (level - 1) + " selesai! Lanjut ke level " + level);
                        nextLevel();
                    }
                }
                selectedCards.clear();
            }
        }
    }

    void hideCards() {
        if (gameReady && !selectedCards.isEmpty()) {
            for (JButton btn : selectedCards) {
                btn.setIcon(cardBackImageIcon);
            }
            selectedCards.clear();
        } else {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    void restartGame() {
        if (!gameReady) return;
        gameReady = false;
        restartButton.setEnabled(false);
        selectedCards.clear();
        errorCount = 0;
        matchedSets = 0;
        textLabel.setText("Level: " + level + " | Errors: " + errorCount);
        java.util.Collections.shuffle(cardSet);

        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        Timer showTimer = new Timer(2500, e -> {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        });
        showTimer.setRepeats(false);
        showTimer.start();
    }


    void setupLevel(int level) {
        switch (level) {
            case 1: setupCards(5, 2, 2, 5); break;
            case 2: setupCards(8, 2, 4, 4); break;
            case 3: setupCards(10, 2, 4, 5); break;
            case 4: setupCards(8, 3, 4, 6); break;
            case 5: setupCards(10, 3, 5, 6); break;
        }
        totalPairs = cardSet.size() / matchCount;
        matchedSets = 0;
    }

    void nextLevel() {
    frame.getContentPane().remove(boardPanel);
    boardPanel = new JPanel();
    setupLevel(level);

    board = new ArrayList<>();
    boardPanel.setLayout(new GridLayout(rows, columns));
    for (int i = 0; i < cardSet.size(); i++) {
        JButton tile = new JButton();
        tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
        tile.setOpaque(true);
        tile.setFocusable(false);
        int index = i;
        tile.addActionListener(e -> handleCardClick(tile, index));
        board.add(tile);
        boardPanel.add(tile);
    }

    frame.add(boardPanel);
    frame.revalidate();
    frame.repaint();

    errorCount = 0;
    textLabel.setText("Level: " + level + " | Errors: " + errorCount);
    restartButton.setEnabled(false);
    gameReady = false;

    for (int i = 0; i < board.size(); i++) {
        board.get(i).setIcon(cardSet.get(i).cardImageIcon);
    }

    Timer showTimer = new Timer(2500, e -> {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardBackImageIcon);
        }
        gameReady = true;
        restartButton.setEnabled(true);
    });
    showTimer.setRepeats(false);
    showTimer.start();
}

    void setupCards() {
        setupCards(5, 2);
    }

    void setupCards(int pairs, int matchCount) {
        setupCards(pairs, matchCount, 4, 5); // default grid
    }

    void setupCards(int pairs, int matchCount, int rows, int columns) {
        this.matchCount = matchCount;
        this.rows = rows;
        this.columns = columns;
        this.boardWidth = columns * cardWidth;
        this.boardHeight = rows * cardHeight;

        cardSet = new ArrayList<>();

        ArrayList<String> selectedNames = new ArrayList<>();
        while (selectedNames.size() < pairs) {
            String name = cardList[(int) (Math.random() * cardList.length)];
            if (!selectedNames.contains(name)) selectedNames.add(name);
        }

        for (String name : selectedNames) {
            for (int i = 0; i < matchCount; i++) {
                Image cardImg = new ImageIcon(getClass().getResource("/img/" + name + ".png")).getImage();
                ImageIcon icon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                cardSet.add(new Card(name, icon));
            }
        }

        java.util.Collections.shuffle(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("/img/back.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }
}
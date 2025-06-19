import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class MatchCards {
    class Card { // inner-class
        String cardName;
        ImageIcon cardImageIcon;

        // konstruktor card
        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }
    // nama-nama cards
    String[] cardList = {
        "gelandangan", "hirono", "janice", "kak ajis", "kak icut",
        "kayla", "lugakpeduli", "orel", "sheyla", "vara"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<ICard> cardSet;
    ImageIcon cardBackImageIcon;
    
    // ukuran board game
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

    //toggle
    boolean hardMode = false;
    int maxErrors = Integer.MAX_VALUE;
    Map<String, ImageIcon> cardImageCache = new HashMap<>();

 
    MatchCards() {
        setupLevel(level);

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Level: " + level + " | Fails: " + errorCount);
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));

        //toggle
        JToggleButton toggleDifficulty = new JToggleButton("Easy");
        toggleDifficulty.setFocusable(false);
        toggleDifficulty.addActionListener(e -> {
            hardMode = toggleDifficulty.isSelected();
            toggleDifficulty.setText(hardMode ? "Hard" : "Easy");
            setMaxErrors();
            textLabel.setText("Level: " + level + " | Fails: " + errorCount);
            level = 1;
            SwingUtilities.invokeLater(() -> nextLevel());
        });
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.WEST);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        textPanel.add(toggleDifficulty, BorderLayout.EAST);
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
            board.get(i).setIcon(cardSet.get(i).getCardImageIcon());
        }
        gameReady = false;
        restartButton.setEnabled(false);

        Timer startTimer = new Timer(2000, e -> {
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }

    void setMaxErrors() {
        if (hardMode) {
            switch (level) {
                case 1 -> maxErrors = 5;
                case 2 -> maxErrors = 8;
                case 3 -> maxErrors = 10;
                case 4 -> maxErrors = 20;
                case 5 -> maxErrors = 25;
            }
        } else {
            maxErrors = Integer.MAX_VALUE;
        }
    }

    void handleCardClick(JButton tile, int index) {
        if (!gameReady || selectedCards.contains(tile) || tile.getIcon() != cardBackImageIcon) return;


        tile.setIcon(cardSet.get(index).getCardImageIcon());
        selectedCards.add(tile);

        if (selectedCards.size() == matchCount) {
            boolean match = true;
            ICard firstCard = cardSet.get(board.indexOf(selectedCards.get(0)));
            String firstName = firstCard.getCardName();

            for (JButton btn : selectedCards) { 
                ICard currentCard = cardSet.get(board.indexOf(btn));
                if (!currentCard.getCardName().equals(firstName)) {
                    match = false;
                    break;
                }
            }

        // semua adalah NyawaCard
            boolean allNyawa = true;
            for (JButton btn : selectedCards) {
                if (!(cardSet.get(board.indexOf(btn)) instanceof NyawaCard)) {
                    allNyawa = false;
                    break;
                }
            }

            if (match) {
                matchedSets++;

                if (allNyawa && hardMode) {
                    errorCount = Math.max(0, errorCount - 1);
                    textLabel.setText("Level: " + level + " | Fails: " + errorCount + " / " + maxErrors);
                }

                if (matchedSets == totalPairs) {
                    if (level == 5) {
                        JOptionPane.showMessageDialog(frame, "Selamat! Anda berhasil menyelesaikan semua level.");
                        level = 1;
                        nextLevel();
                    } else {
                        level++;
                        JOptionPane.showMessageDialog(frame, "Level " + (level - 1) + " selesai! Lanjut ke level " + level);
                        nextLevel();
                    }
                }

                selectedCards.clear();

            } else {
                errorCount++;
                textLabel.setText("Level: " + level + " | Fails: " + errorCount + (hardMode ? " / " + maxErrors : ""));
                hideCardTimer.start();

                if (hardMode && errorCount >= maxErrors) {
                    JOptionPane.showMessageDialog(frame, "Game Over! Error maksimum tercapai.");
                    level = 1;
                    nextLevel();
                }
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
        textLabel.setText("Level: " + level + " | Fails: " + errorCount);
        java.util.Collections.shuffle(cardSet);

        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).getCardImageIcon());
        }

        Timer showTimer = new Timer(2000, e -> {
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
            case 1: setupCards(6, 2, 3, 4); break;
            case 2: setupCards(8, 2, 4, 4); break;
            case 3: setupCards(10, 2, 4, 5); break;
            case 4: setupCards(8, 3, 4, 6); break;
            case 5: setupCards(10, 3, 5, 6); break;
        }
        setMaxErrors();
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
        textLabel.setText("Level: " + level + " | Fails: " + errorCount);
        restartButton.setEnabled(false);
        gameReady = false;

        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).getCardImageIcon());
        }

        Timer showTimer = new Timer(2000, e -> {
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
        setupCards(pairs, matchCount, 4, 4); // default grid
    }

    void setupCards(int pairs, int matchCount, int rows, int columns) {
        this.matchCount = matchCount;
        this.rows = rows;
        this.columns = columns;
        this.boardWidth = columns * cardWidth;
        this.boardHeight = rows * cardHeight;

        cardSet = new ArrayList<>();

        ArrayList<String> selectedNames = new ArrayList<>();
        int normalPairs = pairs;

        if (hardMode){
            normalPairs=pairs-1;
        }

        while (selectedNames.size() < normalPairs) {
            String name = cardList[(int) (Math.random() * cardList.length)];
            if (!selectedNames.contains(name)) selectedNames.add(name);
        }

        for (String name : selectedNames) {
            for (int i = 0; i < matchCount; i++) {
                ImageIcon icon = cardImageCache.computeIfAbsent(name, n -> {
                    Image cardImg = new ImageIcon(getClass().getResource("/img/" + n + ".png")).getImage();
                    return new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                });
                
                cardSet.add(new NormalCard(name, icon));

            }
        }
        if (hardMode) {
            Image nyawaImg = new ImageIcon(getClass().getResource("/img/nyawa.png")).getImage();
            ImageIcon nyawaIcon = new ImageIcon(nyawaImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            for (int i = 0; i < matchCount; i++) {
                cardSet.add(new NyawaCard(nyawaIcon));
            }
        }

        java.util.Collections.shuffle(cardSet);
        if (frame != null) {
            frame.setSize(boardWidth, boardHeight + 100); // +100 untuk text dan tombol bawah
            textPanel.setPreferredSize(new Dimension(boardWidth, 30));
            restartButton.setPreferredSize(new Dimension(boardWidth, 30));
            frame.revalidate(); // perbarui layout
        }

        Image backImg = new ImageIcon(getClass().getResource("/img/back.png")).getImage();
        cardBackImageIcon = new ImageIcon(backImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
        Image nyawaImg = new ImageIcon(getClass().getResource("/img/nyawa.png")).getImage();
        ImageIcon nyawaIcon = new ImageIcon(nyawaImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));

    }
}
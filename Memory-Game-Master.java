package memoryGameMaster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MemoryGameMaster extends JFrame {
    // --- 設定定数 ---
    private final int GRID_SIZE = 4;
    private final String SCORE_FILE = "highscores.properties";
    
    // --- UIコンポーネント ---
    private List<JButton> buttons = new ArrayList<>();
    private JLabel timerLabel, highScoreLabel;
    
    // --- ゲーム状態管理 ---
    private List<Integer> cardValues = new ArrayList<>();
    private JButton firstCard = null, secondCard = null;
    private boolean isChecking = false;
    private int timeLeft;
    private String currentDifficulty = "普通";
    private javax.swing.Timer gameTimer;
    
    // --- スコア管理 ---
    private Properties scores = new Properties();

    public MemoryGameMaster() {
        loadScores();
        initUI();
        startNewGame();
    }

    private void initUI() {
        setTitle("Java Memory Game Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ヘッダーパネル
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(230, 230, 250));
        
        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Meiryo", Font.BOLD, 20));
        
        highScoreLabel = new JLabel("", SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        
        header.add(timerLabel);
        header.add(highScoreLabel);
        add(header, BorderLayout.NORTH);

        // カードパネル
        JPanel board = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 8, 8));
        board.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            JButton btn = new JButton("?");
            btn.setFont(new Font("Arial", Font.BOLD, 28));
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            buttons.add(btn);
            board.add(btn);
        }
        add(board, BorderLayout.CENTER);

        // メインタイマー初期化 (1秒周期)
        gameTimer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            if (timeLeft <= 5) playSound("tick.wav"); // 残り5秒でチック音
            updateDisplay();
            if (timeLeft <= 0) gameOver();
        });

        setSize(450, 550);
        setLocationRelativeTo(null);
    }

    private void startNewGame() {
        gameTimer.stop();
        chooseDifficulty();
        setupCards();
        resetButtons();
        updateDisplay();
        gameTimer.start();
    }

    private void chooseDifficulty() {
        String[] options = {"簡単 (60s)", "普通 (30s)", "難しい (15s)"};
        int choice = JOptionPane.showOptionDialog(this, 
            "難易度を選択してください\n現在の記録\n" + 
            "簡単:" + scores.getProperty("簡単", "0") + "s | " +
            "普通:" + scores.getProperty("普通", "0") + "s | " +
            "難:" + scores.getProperty("難しい", "0") + "s", 
            "Difficulty", 0, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        switch (choice) {
            case 0 -> { timeLeft = 60; currentDifficulty = "簡単"; }
            case 2 -> { timeLeft = 15; currentDifficulty = "難しい"; }
            default -> { timeLeft = 30; currentDifficulty = "普通"; }
        }
    }

    private void setupCards() {
        cardValues.clear();
        for (int i = 1; i <= (GRID_SIZE * GRID_SIZE) / 2; i++) {
            cardValues.add(i); cardValues.add(i);
        }
        Collections.shuffle(cardValues);
    }

    private void resetButtons() {
        for (int i = 0; i < buttons.size(); i++) {
            JButton btn = buttons.get(i);
            btn.setText("?");
            btn.setBackground(Color.WHITE);
            btn.setEnabled(true);
            for (ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
            final int val = cardValues.get(i);
            btn.addActionListener(e -> handleFlip(btn, val));
        }
        firstCard = secondCard = null;
        isChecking = false;
    }

    private void handleFlip(JButton btn, int val) {
        if (isChecking || btn == firstCard || !btn.getText().equals("?")) return;

        playSound("flip.wav");
        btn.setText(String.valueOf(val));

        if (firstCard == null) {
            firstCard = btn;
        } else {
            secondCard = btn;
            isChecking = true;
            checkMatch();
        }
    }

    private void checkMatch() {
        if (firstCard.getText().equals(secondCard.getText())) {
            playSound("match.wav");
            firstCard.setBackground(new Color(144, 238, 144));
            secondCard.setBackground(new Color(144, 238, 144));
            firstCard = secondCard = null;
            isChecking = false;
            if (checkWin()) win();
        } else {
            javax.swing.Timer t = new javax.swing.Timer(600, e -> {
                firstCard.setText("?");
                secondCard.setText("?");
                firstCard = secondCard = null;
                isChecking = false;
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private boolean checkWin() {
        return buttons.stream().allMatch(b -> !b.getText().equals("?"));
    }

    private void win() {
        gameTimer.stop();
        playSound("win.wav");
        int best = Integer.parseInt(scores.getProperty(currentDifficulty, "0"));
        String msg = "クリア！残り時間: " + timeLeft + "秒";
        
        if (timeLeft > best) {
            scores.setProperty(currentDifficulty, String.valueOf(timeLeft));
            saveScores();
            msg += "\n★ハイスコア更新！★";
        }
        showEndDialog(msg);
    }

    private void gameOver() {
        gameTimer.stop();
        playSound("gameover.wav");
        showEndDialog("タイムアップ！終了です。");
    }

    private void showEndDialog(String msg) {
        int res = JOptionPane.showConfirmDialog(this, msg + "\nもう一度遊びますか？", "Game Over", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) startNewGame();
        else System.exit(0);
    }

    private void updateDisplay() {
        timerLabel.setText("モード: " + currentDifficulty + " | 残り: " + timeLeft + "秒");
        timerLabel.setForeground(timeLeft <= 10 ? Color.RED : Color.DARK_GRAY);
        highScoreLabel.setText("Best (" + currentDifficulty + "): " + scores.getProperty(currentDifficulty, "0") + "秒");
    }

    // --- ユーティリティ: スコア入出力 ---
    private void loadScores() {
        try (InputStream is = new FileInputStream(SCORE_FILE)) { scores.load(is); } 
        catch (IOException e) { /* ファイルなし */ }
    }
    private void saveScores() {
        try (OutputStream os = new FileOutputStream(SCORE_FILE)) { scores.store(os, null); } 
        catch (IOException e) { e.printStackTrace(); }
    }

    // --- ユーティリティ: サウンド再生 ---
    private void playSound(String file) {
        try {
            AudioInputStream ai = AudioSystem.getAudioInputStream(new File("res/" + file));
            Clip clip = AudioSystem.getClip();
            clip.open(ai);
            clip.start();
        } catch (Exception e) { /* 音源がない場合は無視 */ }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryGameMaster().setVisible(true));
    }
}

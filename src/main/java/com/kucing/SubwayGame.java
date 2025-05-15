package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class SubwayGame extends JPanel implements ActionListener, KeyListener {
    private JFrame parentFrame;  // Add this line
    private JButton backButton;  // Add this with other class variables
    
    // Inner class untuk Obstacle
    // Update the Obstacle inner class
    private class Obstacle {
        int x, y, lane;
        int jumpOffset = 0;
        boolean isJumping = false;
        boolean isBone = false;  // New field to determine if obstacle is a bone
        
        Obstacle(int lane) {
            this.lane = lane;
            this.y = -50;
            this.x = 200 + (lane * 100);
            this.isJumping = Math.random() < 0.3;
            this.isBone = Math.random() < 0.4; // 40% chance to be a bone
        }
    }

    private int playerX = 300;
    private int playerY = 400;
    private int playerLane = 1; // 0=kiri, 1=tengah, 2=kanan
    private int score = 0;
    private boolean isJumping = false;
    private int jumpHeight = 0;
    private boolean isGameOver = false;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private Timer timer;
    private Random random = new Random();

    // Definisi warna tema yang selaras dengan login
    private static final Color SOFT_PURPLE = new Color(230, 230, 250);
    private static final Color DARK_PURPLE = new Color(103, 58, 183);
    private static final Color LIGHT_PURPLE = new Color(209, 196, 233);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;

    // Tambahkan variabel untuk warna dan gradien
    private GradientPaint skyGradient = new GradientPaint(
        0, 0, SOFT_PURPLE,
        0, 600, LIGHT_PURPLE
    );
    
    private Color[] laneColors = {
        new Color(DARK_PURPLE.getRed(), DARK_PURPLE.getGreen(), DARK_PURPLE.getBlue(), 100),
        new Color(DARK_PURPLE.getRed(), DARK_PURPLE.getGreen(), DARK_PURPLE.getBlue(), 80),
        new Color(DARK_PURPLE.getRed(), DARK_PURPLE.getGreen(), DARK_PURPLE.getBlue(), 60)
    };
    private int backgroundOffset = 0;
    private JButton playAgainButton;
    private JButton exitButton;
    private JButton exitToMainButton;
    private JButton viewScoresButton;
    
        private void initializeButtons() {
            playAgainButton = new JButton("Main Lagi");
            exitButton = new JButton("Selesai");
            exitToMainButton = new JButton("Kembali ke Menu");
            
            playAgainButton.setBounds(getWidth()/2 - 160, getHeight()/2 + 120, 100, 30);
            exitButton.setBounds(getWidth()/2 + 60, getHeight()/2 + 120, 100, 30);
            exitToMainButton.setBounds(getWidth()/2 - 50, getHeight()/2 + 160, 120, 30);
            
            playAgainButton.setVisible(false);
            exitButton.setVisible(false);
            exitToMainButton.setVisible(false);
            
            playAgainButton.addActionListener(e -> {
                resetGame();
                timer.start();
                requestFocusInWindow();
            });
            
            exitButton.addActionListener(e -> {
                Leaderboard.addScore(score);
                Leaderboard.showLeaderboard(parentFrame);
            });
    
            exitToMainButton.addActionListener(e -> {
                timer.stop();
                MainGame mainGame = new MainGame();
                parentFrame.getContentPane().removeAll();
                parentFrame.add(mainGame.getMainPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            });
            
            add(playAgainButton);
            add(exitButton);
            add(exitToMainButton);
        }

        private void resetGame() {
            isGameOver = false;
            score = 0;
            obstacles.clear();
            playerLane = 1;
            int trackWidth = 300;
            int startX = (getWidth() - trackWidth) / 2;
            playerX = startX + (playerLane * (trackWidth/3)) + 5;
            
            // Sembunyikan semua tombol
            playAgainButton.setVisible(false);
            exitButton.setVisible(false);
            exitToMainButton.setVisible(false);
            viewScoresButton.setVisible(false);
            
            // Reset fokus ke panel game
            requestFocusInWindow();
        }
    
        // Add these fields at the top of the class
        private Clip jumpSound;
        private Clip moveSound;
        private Clip gameOverSound;
        private Clip collisionSound;  // Move this here with other sound fields
    
        private void initializeSounds() {
            try {
                // Load jump sound
                AudioInputStream jumpStream = AudioSystem.getAudioInputStream(
                    new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\jump.wav"));
                jumpSound = AudioSystem.getClip();
                jumpSound.open(jumpStream);
                
                // Load move sound
                AudioInputStream moveStream = AudioSystem.getAudioInputStream(
                    new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\moved.wav"));
                moveSound = AudioSystem.getClip();
                moveSound.open(moveStream);
                
                // Load game over sound
                AudioInputStream gameOverStream = AudioSystem.getAudioInputStream(
                    new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\gameover.wav"));
                gameOverSound = AudioSystem.getClip();
                gameOverSound.open(gameOverStream);
                
                // Load collision sound
                AudioInputStream collisionStream = AudioSystem.getAudioInputStream(
                    new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\collision.wav"));
                collisionSound = AudioSystem.getClip();
                collisionSound.open(collisionStream);
                
            } catch (Exception e) {
                System.out.println("Error loading sounds: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void handleGameOver() {
            isGameOver = true;
            
            // Play collision sound
            if (collisionSound != null) {
                collisionSound.setFramePosition(0);
                collisionSound.start();
            }
            
            // Play game over sound after a short delay
            Timer soundTimer = new Timer(500, e -> {
                if (gameOverSound != null) {
                    gameOverSound.setFramePosition(0);
                    gameOverSound.start();
                }
                ((Timer)e.getSource()).stop();
            });
            soundTimer.setRepeats(false);
            soundTimer.start();
        }
    

        // Modify the constructor to initialize sounds
        public SubwayGame(JFrame frame) {
            this.parentFrame = frame;
            setPreferredSize(new Dimension(800, 600));
            setBackground(new Color(135, 206, 235));
            timer = new Timer(20, this);
            timer.start();
            addKeyListener(this);
            setFocusable(true);
            
            // Initialize sounds
            initializeSounds();
            
            // Initialize back button
            backButton = new JButton("Back to Leaderboard");
            backButton.setVisible(false);
            backButton.addActionListener(e -> {
                timer.stop();
                Leaderboard.showLeaderboard(parentFrame);
                Leaderboard.addScore(score);
            });
            setLayout(null);
            backButton.setBounds(getWidth()/2 - 80, getHeight()/2 + 120, 160, 30);
            add(backButton);
            
            // Inisialisasi tombol-tombol game over
            setLayout(null); // Penting! Agar bisa mengatur posisi tombol secara manual
            initializeButtons(); // Pastikan ini dipanggil
            
            // Inisialisasi gradien langit
            skyGradient = new GradientPaint(
                0, 0, new Color(135, 206, 235),
                0, 600, new Color(65, 105, 225)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Aktifkan anti-aliasing untuk grafik yang lebih halus
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Gambar latar belakang dengan gradien
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Gambar jalur dengan efek 3D
            drawTrack(g2d);
            
            // Gambar pemain dengan bentuk yang lebih menarik
            drawPlayer(g2d);
            
            // Gambar rintangan dengan efek bayangan
            drawObstacles(g2d);
            
            // Gambar skor dengan efek bayangan
            drawScore(g2d);
            
            if (isGameOver) {
                drawGameOver(g2d);
            }
        }
        
        // Update color scheme with more vibrant colors
        private static final Color SKY_TOP = new Color(230, 210, 255);    // Light magical purple
        private static final Color SKY_BOTTOM = new Color(180, 200, 255); // Soft magical blue
        private static final Color TRACK_COLOR = new Color(75, 0, 130, 180); // Semi-transparent dark purple
        private static final Color TRACK_LINES = new Color(255, 255, 255, 150);
        
        private void drawTrack(Graphics2D g2d) {
            int trackWidth = 300;
            int startX = (getWidth() - trackWidth) / 2;
            
            // Enhanced magical background with rainbow gradient
            LinearGradientPaint skyGradient = new LinearGradientPaint(
                0, 0, 0, getHeight(),
                new float[]{0.0f, 0.3f, 0.7f, 1.0f},
                new Color[]{
                    new Color(230, 210, 255), // Light purple
                    new Color(200, 220, 255), // Light blue
                    new Color(255, 200, 220), // Light pink
                    new Color(180, 200, 255)  // Soft blue
                }
            );
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Add rainbow sparkles
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < 40; i++) {
                double angle = (currentTime / 1000.0 + i) * Math.PI / 6;
                int x = (int)(getWidth() / 2 + Math.cos(angle) * getWidth() / 3);
                int y = (int)(getHeight() / 2 + Math.sin(angle) * getHeight() / 3);
                int sparkSize = 3 + (int)(Math.sin(currentTime / 500.0 + i) * 2);
                
                // Rainbow colors for sparkles
                float hue = (float)((currentTime / 2000.0 + i / 30.0) % 1.0);
                Color sparkleColor = Color.getHSBColor(hue, 0.8f, 1.0f);
                g2d.setColor(new Color(sparkleColor.getRed(), sparkleColor.getGreen(), 
                    sparkleColor.getBlue(), 40 + (int)(Math.sin(currentTime / 700.0 + i) * 20)));
                g2d.fillOval(x, y, sparkSize, sparkSize);
            }
            
            // Add magical track glow
            RadialGradientPaint trackGlow = new RadialGradientPaint(
                new Point(getWidth()/2, getHeight()/2),
                trackWidth * 1.5f,
                new float[]{0.4f, 1.0f},
                new Color[]{
                    new Color(255, 255, 255, 0),
                    new Color(147, 112, 219, 60)
                }
            );
            g2d.setPaint(trackGlow);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw track with magical border
            g2d.setColor(TRACK_COLOR);
            g2d.fillRect(startX, 0, trackWidth, getHeight());
            
            // Add rainbow edge effect
            for (int i = 0; i < 10; i++) {
                float hue = (float)((currentTime / 1000.0 + i / 10.0) % 1.0);
                Color edgeColor = Color.getHSBColor(hue, 0.7f, 1.0f);
                g2d.setColor(new Color(edgeColor.getRed(), edgeColor.getGreen(), 
                    edgeColor.getBlue(), 25 - i * 2));
                g2d.drawRect(startX - i, 0, trackWidth + i * 2, getHeight());
            }
            
            // Draw lane lines with magical glow
            g2d.setColor(TRACK_LINES);
            int laneWidth = trackWidth / 3;
            for(int y = backgroundOffset % 40; y < getHeight(); y += 40) {
                // Add glowing effect to lines
                GradientPaint lineGlow = new GradientPaint(
                    startX + laneWidth, y,
                    new Color(255, 255, 255, 180),
                    startX + laneWidth, y + 20,
                    new Color(255, 255, 255, 40)
                );
                g2d.setPaint(lineGlow);
                g2d.fillRect(startX + laneWidth - 2, y, 4, 20);
                g2d.fillRect(startX + laneWidth * 2 - 2, y, 4, 20);
            }
        }

        private void drawObstacles(Graphics2D g2d) {
            for (Obstacle obs : obstacles) {
                if (obs.isBone) {
                    // Draw bone shadow
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillOval(obs.x - 5, obs.y + 35, 40, 10);
                    
                    // Draw bone ends
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.fillOval(obs.x - 5, obs.y, 15, 20);
                    g2d.fillOval(obs.x + 20, obs.y, 15, 20);
                    
                    // Draw bone middle
                    g2d.fillRect(obs.x + 8, obs.y + 5, 14, 10);
                    
                    // Add bone details
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.drawOval(obs.x - 5, obs.y, 15, 20);
                    g2d.drawOval(obs.x + 20, obs.y, 15, 20);
                    g2d.drawLine(obs.x + 8, obs.y + 5, obs.x + 22, obs.y + 5);
                    g2d.drawLine(obs.x + 8, obs.y + 15, obs.x + 22, obs.y + 15);
                } else {
                    // Original trash bin drawing code
                    // Efek bayangan yang lebih realistis dengan gradasi
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillOval(obs.x - 8, obs.y + 35, 46, 15);
                    
                    // Badan tempat sampah dengan efek 3D
                    GradientPaint binBodyGradient = new GradientPaint(
                        obs.x, obs.y, new Color(120, 120, 120),
                        obs.x + 30, obs.y, new Color(80, 80, 80)
                    );
                    g2d.setPaint(binBodyGradient);
                    g2d.fillRect(obs.x, obs.y, 30, 40);
                    
                    // Efek highlight pada badan
                    g2d.setColor(new Color(140, 140, 140, 100));
                    g2d.fillRect(obs.x + 2, obs.y + 2, 5, 36);
                    
                    // Tutup tempat sampah dengan efek 3D
                    GradientPaint lidGradient = new GradientPaint(
                        obs.x - 5, obs.y - 5, new Color(100, 100, 100),
                        obs.x + 35, obs.y - 5, new Color(60, 60, 60)
                    );
                    g2d.setPaint(lidGradient);
                    g2d.fillRect(obs.x - 5, obs.y - 5, 40, 10);
                    
                    // Efek highlight pada tutup
                    g2d.setColor(new Color(130, 130, 130, 80));
                    g2d.fillRect(obs.x - 3, obs.y - 4, 36, 2);
                    
                    // Pegangan tutup dengan efek 3D
                    GradientPaint handleGradient = new GradientPaint(
                        obs.x + 10, obs.y - 8, new Color(70, 70, 70),
                        obs.x + 20, obs.y - 8, new Color(40, 40, 40)
                    );
                    g2d.setPaint(handleGradient);
                    g2d.fillRect(obs.x + 10, obs.y - 8, 10, 5);
                    
                    // Efek mengkilap pada pegangan
                    g2d.setColor(new Color(150, 150, 150, 70));
                    g2d.fillRect(obs.x + 11, obs.y - 7, 8, 2);
                    
                    // Garis-garis detail dengan efek mengkilap
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.setColor(new Color(50, 50, 50, 150));
                    // Garis vertikal
                    g2d.drawLine(obs.x + 10, obs.y, obs.x + 10, obs.y + 40);
                    g2d.drawLine(obs.x + 20, obs.y, obs.x + 20, obs.y + 40);
                    // Garis horizontal
                    g2d.drawLine(obs.x, obs.y + 13, obs.x + 30, obs.y + 13);
                    g2d.drawLine(obs.x, obs.y + 26, obs.x + 30, obs.y + 26);
                    
                    // Efek highlight di sudut-sudut
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.drawLine(obs.x, obs.y, obs.x, obs.y + 40); // Kiri
                    g2d.drawLine(obs.x, obs.y, obs.x + 30, obs.y); // Atas
                    
                    // Efek bayangan di sudut-sudut
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.drawLine(obs.x + 30, obs.y, obs.x + 30, obs.y + 40); // Kanan
                    g2d.drawLine(obs.x, obs.y + 40, obs.x + 30, obs.y + 40); // Bawah
                }
            }
        }

        private void drawScore(Graphics2D g2d) {
            // Efek glow untuk skor
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            
            // Outer glow
            g2d.setColor(new Color(255, 215, 0, 50));
            g2d.drawString("Skor: " + score, 23, 43);
            g2d.drawString("Skor: " + score, 21, 41);
            g2d.drawString("Skor: " + score, 19, 39);
            
            // Inner shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString("Skor: " + score, 22, 42);
            
            // Main text dengan gradien
            GradientPaint scoreGradient = new GradientPaint(
                20, 20, new Color(255, 215, 0),
                20, 50, new Color(255, 180, 0)
            );
            g2d.setPaint(scoreGradient);
            g2d.drawString("Skor: " + score, 20, 40);
        }
        
        private void drawPlayer(Graphics2D g2d) {
            int playerWidth = 40;
            int playerHeight = 50;
            
            // Efek bayangan kucing yang lebih realistis
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(playerX - 5, playerY - jumpHeight + 5, playerWidth + 10, 25);
            
            // Badan kucing dengan gradien yang lebih natural
            GradientPaint catGradient = new GradientPaint(
                playerX, playerY - jumpHeight,
                LIGHT_PURPLE,
                playerX + playerWidth, playerY - jumpHeight + playerHeight,
                DARK_PURPLE
            );

            g2d.setPaint(catGradient);
            g2d.fillOval(playerX, playerY - jumpHeight, playerWidth, playerHeight);
            
            // Pola bulu kucing
            g2d.setColor(new Color(190, 170, 210, 50));
            for(int i = 0; i < 5; i++) {
                g2d.drawArc(playerX + (i*8), playerY - jumpHeight + 10, 10, 30, 0, 180);
            }
            
            // Kepala kucing dengan bentuk yang lebih alami
            GradientPaint headGradient = new GradientPaint(
                playerX, playerY - jumpHeight - 15,
                LIGHT_PURPLE,
                playerX + playerWidth, playerY - jumpHeight + 5,
                DARK_PURPLE
            );
            g2d.setPaint(headGradient);
            g2d.fillOval(playerX + 5, playerY - jumpHeight - 15, playerWidth - 10, playerHeight - 15);
            
            // Mata kucing yang lebih ekspresif - berubah saat game over
            // Bagian putih mata
            g2d.setColor(new Color(255, 255, 245));
            g2d.fillOval(playerX + 12, playerY - jumpHeight - 10, 8, 9);
            g2d.fillOval(playerX + 25, playerY - jumpHeight - 10, 8, 9);
            
            if (isGameOver) {
                // Mata sedih dengan air mata
                g2d.setColor(new Color(80, 200, 120));
                // Mata kiri
                g2d.fillArc(playerX + 13, playerY - jumpHeight - 9, 6, 7, 0, -180);
                // Mata kanan
                g2d.fillArc(playerX + 26, playerY - jumpHeight - 9, 6, 7, 0, -180);
                
                // Air mata
                g2d.setColor(new Color(100, 149, 237, 180));
                g2d.fillOval(playerX + 14, playerY - jumpHeight - 5, 3, 5);
                g2d.fillOval(playerX + 27, playerY - jumpHeight - 5, 3, 5);
                
                // Mulut sedih
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawArc(playerX + 18, playerY - jumpHeight - 2, 4, 4, -180, 180);
                
                // Efek tabrakan
                // Bintang-bintang kecil di sekitar kepala
                g2d.setColor(new Color(255, 255, 0, 180));
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    int starX = (int)(playerX + 20 + Math.cos(angle) * 25);
                    int starY = (int)(playerY - jumpHeight + Math.sin(angle) * 25);
                    drawStar(g2d, starX, starY, 4);
                }
                
                // Efek getaran
                if (System.currentTimeMillis() % 200 < 100) {
                    playerX += 2;
                }
            } else {
                // Mata normal
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillOval(playerX + 12, playerY - jumpHeight - 10, 8, 9);
                g2d.fillOval(playerX + 25, playerY - jumpHeight - 10, 8, 9);
                
                // Iris mata dengan gradien
                GradientPaint irisGradient = new GradientPaint(
                    playerX, playerY - jumpHeight - 8,
                    new Color(80, 200, 120),  // Warna iris hijau
                    playerX + 4, playerY - jumpHeight - 4,
                    new Color(40, 160, 80)
                );
                g2d.setPaint(irisGradient);
                g2d.fillOval(playerX + 13, playerY - jumpHeight - 9, 6, 7);
                g2d.fillOval(playerX + 26, playerY - jumpHeight - 9, 6, 7);
                
                // Pupil mata yang lebih detail
                g2d.setColor(Color.BLACK);
                g2d.fillOval(playerX + 14, playerY - jumpHeight - 8, 4, 5);
                g2d.fillOval(playerX + 27, playerY - jumpHeight - 8, 4, 5);
                
                // Kilau mata yang lebih realistis
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillOval(playerX + 13, playerY - jumpHeight - 9, 2, 2);
                g2d.fillOval(playerX + 26, playerY - jumpHeight - 9, 2, 2);
                
                // Telinga dengan detail bulu
                drawDetailedEar(g2d, playerX + 10, playerY - jumpHeight - 25, true);  // Telinga kiri
                drawDetailedEar(g2d, playerX + 30, playerY - jumpHeight - 25, false); // Telinga kanan
                
                // Hidung yang lebih realistis
                GradientPaint noseGradient = new GradientPaint(
                    playerX + 18, playerY - jumpHeight - 5,
                    new Color(255, 120, 150),
                    playerX + 24, playerY - jumpHeight - 1,
                    new Color(255, 90, 120)
                );
                g2d.setPaint(noseGradient);
                int[] noseX = {playerX + 20, playerX + 22, playerX + 18};
                int[] noseY = {playerY - jumpHeight - 5, playerY - jumpHeight - 2, playerY - jumpHeight - 2};
                g2d.fillPolygon(noseX, noseY, 3);
                
                // Mulut kucing
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.drawArc(playerX + 18, playerY - jumpHeight - 2, 4, 3, 0, -180);
                
                // Kumis yang lebih realistis
                g2d.setStroke(new BasicStroke(1.0f));
                drawDetailedWhiskers(g2d);
                
                // Ekor yang lebih natural dengan efek bulu
                drawDetailedTail(g2d);
                
                // Kaki dengan detail bulu
                drawDetailedLegs(g2d);
            }
        }
        
        private void drawDetailedEar(Graphics2D g2d, int x, int y, boolean isLeft) {
            // Bentuk dasar telinga
            int[] xPoints = {x, x + (isLeft ? -5 : 5), x + (isLeft ? 5 : -5)};
            int[] yPoints = {y, y + 15, y + 15};
            
            GradientPaint earGradient = new GradientPaint(
                x, y,
                new Color(200, 180, 220), // Soft lilac
                x + (isLeft ? 5 : -5), y + 15,
                new Color(180, 160, 200)  // Darker lilac
            );
            g2d.setPaint(earGradient);
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Detail bulu dalam telinga
            g2d.setColor(new Color(255, 182, 193));
            int[] innerX = {x, x + (isLeft ? -3 : 3), x + (isLeft ? 3 : -3)};
            int[] innerY = {y + 2, y + 12, y + 12};
            g2d.fillPolygon(innerX, innerY, 3);
            
            // Garis-garis bulu
            g2d.setColor(new Color(230, 120, 0, 100));
            g2d.setStroke(new BasicStroke(0.5f));
            for(int i = 0; i < 3; i++) {
                g2d.drawLine(x, y + i*4, x + (isLeft ? -3 : 3), y + i*4 + 3);
            }
        }
        
        private void drawDetailedWhiskers(Graphics2D g2d) {
            // Kumis dengan efek gradasi dan lengkungan
            for(int i = -1; i <= 1; i++) {
                // Kumis kiri
                g2d.setColor(new Color(0, 0, 0, 150 - Math.abs(i) * 30));
                g2d.drawLine(
                    playerX + 15,
                    playerY - jumpHeight - 3 + i*2,
                    playerX - 5,
                    playerY - jumpHeight - 1 + i*2
                );
                
                // Kumis kanan
                g2d.drawLine(
                    playerX + 25,
                    playerY - jumpHeight - 3 + i*2,
                    playerX + 45,
                    playerY - jumpHeight - 1 + i*2
                );
            }
        }
        
        private void drawDetailedTail(Graphics2D g2d) {
            GradientPaint tailGradient = new GradientPaint(
                playerX - 20, playerY - jumpHeight,
                new Color(200, 180, 220), // Soft lilac
                playerX + 10, playerY - jumpHeight + 40,
                new Color(180, 160, 200)  // Darker lilac
            );
            g2d.setPaint(tailGradient);
            
            // Bentuk dasar ekor
            g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(playerX - 20, playerY - jumpHeight + 20, 30, 40, 0, 180);
            
            // Detail bulu ekor
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.setColor(new Color(230, 120, 0, 70));
            for(int i = 0; i < 8; i++) {
                g2d.drawLine(
                    playerX - 15 + i*2, playerY - jumpHeight + 35,
                    playerX - 17 + i*2, playerY - jumpHeight + 40
                );
            }
        }
        
        // Add these fields at the top of the class
        private double walkCycle = 0;
        private double walkSpeed = 0.2;
        private double bounceHeight = 0;
        
        private void drawDetailedLegs(Graphics2D g2d) {
            int height = 50;
            GradientPaint legGradient = new GradientPaint(
                playerX, playerY - jumpHeight + height - 10,
                new Color(200, 180, 220),
                playerX, playerY - jumpHeight + height + 10,
                new Color(180, 160, 200)
            );
            g2d.setPaint(legGradient);
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
            // Update walking animation
            walkCycle += walkSpeed;
            bounceHeight = Math.sin(walkCycle) * 3; // Subtle bounce effect
        
            // Dynamic leg positions based on walk cycle
            int[][] legPositions = {
                {10, (int)(-5 - Math.sin(walkCycle) * 8), 5, (int)(10 + Math.cos(walkCycle) * 5)},      // Front left
                {30, (int)(-5 + Math.sin(walkCycle) * 8), 35, (int)(10 - Math.cos(walkCycle) * 5)},     // Front right
                {15, (int)(-5 + Math.cos(walkCycle) * 8), 10, (int)(10 - Math.sin(walkCycle) * 5)},     // Back left
                {25, (int)(-5 - Math.cos(walkCycle) * 8), 30, (int)(10 + Math.sin(walkCycle) * 5)}      // Back right
            };
            
            for(int[] leg : legPositions) {
                // Bentuk dasar kaki
                g2d.drawLine(
                    playerX + leg[0],
                    playerY - jumpHeight + height + leg[1],
                    playerX + leg[2],
                    playerY - jumpHeight + height + leg[3]
                );
                
                // Detail bulu pada kaki
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.setColor(new Color(230, 120, 0, 70));
                for(int i = 0; i < 3; i++) {
                    g2d.drawLine(
                        playerX + leg[0] + i,
                        playerY - jumpHeight + height + leg[1] + i*2,
                        playerX + leg[0] + i - 2,
                        playerY - jumpHeight + height + leg[1] + i*2 + 2
                    );
                }
            }
            
            // Telapak kaki yang lebih detail
            g2d.setColor(new Color(255, 120, 80));
            int[] pawPositions = {3, 33, 8, 28};
            for(int x : pawPositions) {
                g2d.fillOval(playerX + x, playerY - jumpHeight + height + 8, 5, 4);
                // Bantalan kaki
                g2d.setColor(new Color(255, 100, 60));
                g2d.fillOval(playerX + x + 1, playerY - jumpHeight + height + 9, 3, 2);
            }
        }
        
        // Metode helper untuk menggambar kumis dengan efek gradien
        private void drawWhisker(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            GradientPaint whiskerGradient = new GradientPaint(
                x1, y1, new Color(0, 0, 0),
                x2, y2, new Color(0, 0, 0, 50)
            );
            g2d.setPaint(whiskerGradient);
            g2d.drawLine(x1, y1, x2, y2);
        }
    
        // Tambahkan variabel untuk mengontrol kecepatan
        private int baseSpeed = 5;
        private int currentSpeed = baseSpeed;
        private int speedIncreaseInterval = 1000; // Interval untuk menambah kecepatan (dalam milidetik)
        private long lastSpeedIncrease = System.currentTimeMillis();
    
        // Remove @Override as these are just field declarations
        private boolean isDizzy = false;
        private long dizzyStartTime = 0;
        private static final long DIZZY_DURATION = 3000; // 3 seconds of dizzy effect
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isGameOver) {
                // Update dizzy state
                if (isDizzy && System.currentTimeMillis() - dizzyStartTime > DIZZY_DURATION) {
                    isDizzy = false;
                }
    
                // Update kecepatan berdasarkan waktu
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSpeedIncrease > speedIncreaseInterval) {
                    currentSpeed = baseSpeed + (score / 5); // Kecepatan bertambah lebih cepat
                    lastSpeedIncrease = currentTime;
                }
                
                // Update posisi background dengan kecepatan yang meningkat
                backgroundOffset += currentSpeed;
                
                // Hapus auto-jumping code
                
                // Logika lompatan dengan physics yang lebih halus
                if (isJumping) {
                    jumpHeight += 8;  // Increased jump speed
                    if (jumpHeight >= 150) {  // Higher jump
                        isJumping = false;
                    }
                } else if (jumpHeight > 0) {
                    jumpHeight -= 6;  // Smoother landing
                }
                
                // Gerakkan rintangan
                for (int i = obstacles.size() - 1; i >= 0; i--) {
                    Obstacle obs = obstacles.get(i);
                    obs.y += currentSpeed;  // Gunakan currentSpeed yang baru
                    
                    // Collision detection yang lebih akurat
                    if (obs.y >= playerY - 30 && obs.y <= playerY + 30 &&
                        obs.lane == playerLane) {
                        // Game over hanya jika benar-benar menabrak obstacle
                        if (jumpHeight < 50 && obs.y >= playerY - 10) {
                            handleGameOver();
                        }
                    }
                    
                    // Hapus rintangan yang sudah lewat
                    if (obs.y > getHeight()) {
                        obstacles.remove(i);
                        score++;
                    }
                }
                
                // Tambah rintangan baru dengan posisi yang dibatasi
                if (random.nextInt(50) == 0) {
                    int trackWidth = 300;
                    int startX = (getWidth() - trackWidth) / 2;
                    Obstacle obs = new Obstacle(random.nextInt(3));
                    // Pastikan posisi x rintangan berada dalam jalur
                    obs.x = startX + (obs.lane * (trackWidth/3)) + 5;
                    obstacles.add(obs);
                }
            }
            repaint();
        }
    
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isGameOver) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT && playerLane > 0) {
                    playerLane--;
                    int trackWidth = 300;
                    int startX = (getWidth() - trackWidth) / 2;
                    playerX = startX + (playerLane * (trackWidth/3)) + 5;
                    if (moveSound != null) {
                        moveSound.setFramePosition(0);
                        moveSound.start();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerLane < 2) {
                    playerLane++;
                    int trackWidth = 300;
                    int startX = (getWidth() - trackWidth) / 2;
                    playerX = startX + (playerLane * (trackWidth/3)) + 5;
                    if (moveSound != null) {
                        moveSound.setFramePosition(0);
                        moveSound.start();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !isJumping) {
                    isJumping = true;
                    jumpHeight = 0;
                    if (jumpSound != null) {
                        jumpSound.setFramePosition(0);
                        jumpSound.start();
                    }
                }
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {}
    
        @Override
        public void keyReleased(KeyEvent e) {}
    
        private void drawGameOver(Graphics2D g2d) {
            // Set font untuk text game over
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics metrics = g2d.getFontMetrics();
            
            // Gambar text "GAME OVER" dengan efek bayangan
            String gameOverText = "GAME OVER";
            int gameOverX = (getWidth() - metrics.stringWidth(gameOverText)) / 2;
            int gameOverY = getHeight() / 2 - 40;
            
            // Bayangan
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(gameOverText, gameOverX + 3, gameOverY + 3);
            
            // Text utama dengan warna merah
            g2d.setColor(Color.RED);
            g2d.drawString(gameOverText, gameOverX, gameOverY);
            
            // Set font untuk skor
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            metrics = g2d.getFontMetrics();
            
            // Gambar text skor
            String scoreText = "Skor Akhir: " + score;
            int scoreX = (getWidth() - metrics.stringWidth(scoreText)) / 2;
            int scoreY = gameOverY + 50;
            
            // Bayangan untuk skor
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(scoreText, scoreX + 2, scoreY + 2);
            
            // Text skor utama
            g2d.setColor(Color.WHITE);
            g2d.drawString(scoreText, scoreX, scoreY);
            
            // Atur posisi tombol-tombol
            // Update button positions
            playAgainButton.setBounds(getWidth()/2 - 160, scoreY + 30, 100, 30);
            exitButton.setBounds(getWidth()/2 + 60, scoreY + 30, 100, 30);
            exitToMainButton.setBounds(getWidth()/2 - 60, scoreY + 70, 120, 30);
            
            // Show buttons
            playAgainButton.setVisible(true);
            exitButton.setVisible(true);
            exitToMainButton.setVisible(true);
            
            // Enable buttons
            playAgainButton.setEnabled(true);
            exitButton.setEnabled(true);
            exitToMainButton.setEnabled(true);
        }
    
        // Replace the main method with this method
        public static void startGame(JFrame parentFrame) {
            parentFrame.getContentPane().removeAll();
            SubwayGame game = new SubwayGame(parentFrame);
            parentFrame.add(game);
            parentFrame.pack();
            parentFrame.setLocationRelativeTo(null);
            game.requestFocusInWindow();
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    
        private void drawStar(Graphics2D g2d, int x, int y, int size) {
            // Gambar bintang sederhana
            g2d.drawLine(x - size, y, x + size, y);  // Horizontal
            g2d.drawLine(x, y - size, x, y + size);  // Vertikal
            g2d.drawLine(x - size/2, y - size/2, x + size/2, y + size/2);  // Diagonal 1
            g2d.drawLine(x - size/2, y + size/2, x + size/2, y - size/2);  // Diagonal 2
            
            // Apply bounce effect to player position
            playerY = 400 + (int)bounceHeight;
            
            // Add sparkle effect
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fillOval(x - size/4, y - size/4, size/2, size/2);
        }
    }

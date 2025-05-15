package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.net.URL;
import java.io.File;

public class MainGame extends JFrame {
    private JPanel mainPanel;
    private CatPanel catPanel; // Replace catLabel with catPanel
    private JButton feedButton;
    private JButton playButton;
    private JButton sleepButton;  // Change cleanButton to sleepButton
    private JProgressBar hungerBar;
    private JProgressBar happinessBar;
    private JProgressBar sleepinessBar;  // Changed from cleanlinessBar

    // Definisi warna tema yang selaras dengan login
    private static final Color SOFT_PURPLE = new Color(230, 230, 250);
    private static final Color DARK_PURPLE = new Color(103, 58, 183);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    private static final Color LIGHT_PURPLE = new Color(209, 196, 233);
    
    // Add new color constants
    private static final Color SUNSET_PINK = new Color(255, 182, 193);
    private static final Color PASTEL_BLUE = new Color(174, 198, 255);
    private static final Color CLOUD_WHITE = new Color(255, 255, 255, 40);
    
    // Add sleep sound field at the top with other fields
    private Clip sleepSound;
    private Clip eatSound;

    // After setting window properties in constructor
    public MainGame() {
        setTitle("Virtual Cat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 650);
        setLocationRelativeTo(null);

        // Initialize sleep sound
        try {
            AudioInputStream sleepStream = AudioSystem.getAudioInputStream(
                new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\sleep.wav"));
            sleepSound = AudioSystem.getClip();
            sleepSound.open(sleepStream);
        } catch (Exception e) {
            System.out.println("Error loading sleep sound: " + e.getMessage());
            e.printStackTrace();
        }

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create warm living room gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 248, 220), // Warm cream color
                    0, getHeight(), new Color(255, 235, 205) // Lighter warm tone
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add living room decorations
                drawDecorations(g2d);
            }
            
            private void drawDecorations(Graphics2D g2d) {
                // Draw wallpaper pattern
                g2d.setColor(new Color(139, 69, 19, 30)); // Semi-transparent brown
                int patternSize = 40;
                for (int i = 0; i < getWidth(); i += patternSize) {
                    for (int j = 0; j < getHeight(); j += patternSize) {
                        g2d.drawRect(i, j, patternSize, patternSize);
                        g2d.drawLine(i, j, i + patternSize, j + patternSize);
                    }
                }

                // Draw furniture silhouettes
                g2d.setColor(new Color(101, 67, 33, 40)); // Dark brown transparent
                
                // Draw sofa
                g2d.fillRoundRect(getWidth()/2 - 100, getHeight() - 200, 200, 80, 20, 20);
                g2d.fillRoundRect(getWidth()/2 - 90, getHeight() - 220, 180, 30, 10, 10);
                
                // Draw side table
                g2d.fillRect(getWidth() - 80, getHeight() - 150, 50, 70);
                
                // Draw lamp
                g2d.setColor(new Color(255, 223, 186, 60));
                g2d.fillOval(getWidth() - 70, getHeight() - 250, 30, 30);
                g2d.setColor(new Color(101, 67, 33, 40));
                g2d.fillRect(getWidth() - 60, getHeight() - 220, 10, 70);
                
                // Add subtle lighting effect
                RadialGradientPaint lampGlow = new RadialGradientPaint(
                    new Point(getWidth() - 55, getHeight() - 235),
                    100f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{
                        new Color(255, 255, 200, 30),
                        new Color(255, 255, 200, 0)
                    }
                );
                g2d.setPaint(lampGlow);
                g2d.fillOval(getWidth() - 105, getHeight() - 285, 100, 100);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(SOFT_PURPLE);
        
        // Cat display area
        catPanel = new CatPanel();
        catPanel.setOpaque(false);  // Make panel transparent to show background
        JPanel catContainer = new JPanel(new BorderLayout());
        catContainer.setOpaque(false);  // Make container transparent too
        catContainer.add(catPanel, BorderLayout.CENTER);
        mainPanel.add(catContainer, BorderLayout.CENTER);

        // Status bars dengan tema yang selaras
        // Status bars panel with matching theme
        // Status bars panel with enhanced styling
        JPanel statusPanel = new JPanel(new GridLayout(3, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Match CatPanel's gradient with enhanced colors
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 238, 248, 240), // Light lilac with transparency
                    0, getHeight(), new Color(235, 227, 253, 240)  // Soft purple with transparency
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add sparkly pattern
                long currentTime = System.currentTimeMillis();
                for (int i = 0; i < 20; i++) {
                    double angle = (currentTime / 1000.0 + i) * Math.PI / 6;
                    int x = (int)(getWidth() / 2 + Math.cos(angle) * getWidth() / 3);
                    int y = (int)(getHeight() / 2 + Math.sin(angle) * getHeight() / 3);
                    int sparkSize = 3 + (int)(Math.sin(currentTime / 500.0 + i) * 2);
                    
                    // Sparkle with changing opacity
                    int alpha = 40 + (int)(Math.sin(currentTime / 700.0 + i) * 20);
                    g2d.setColor(new Color(255, 255, 255, alpha));
                    g2d.fillOval(x, y, sparkSize, sparkSize);
                }
                
                // Add subtle geometric pattern
                g2d.setColor(new Color(255, 255, 255, 30));
                int patternSize = Math.min(getWidth(), getHeight()) / 8;
                for (int i = 0; i < getWidth(); i += patternSize * 2) {
                    for (int j = 0; j < getHeight(); j += patternSize * 2) {
                        g2d.fillRoundRect(i, j, patternSize, patternSize, 8, 8);
                    }
                }
                
                // Add shimmering effect
                RadialGradientPaint shimmer = new RadialGradientPaint(
                    new Point(getWidth()/2, getHeight()/2),
                    Math.max(getWidth(), getHeight()),
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{
                        new Color(255, 255, 255, 0),
                        new Color(255, 255, 255, 20),
                        new Color(255, 255, 255, 0)
                    }
                );
                g2d.setPaint(shimmer);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel hungerLabel = createStyledLabel("Hunger:");
        hungerBar = createStyledProgressBar();
        
        JLabel happinessLabel = createStyledLabel("Happiness:");
        happinessBar = createStyledProgressBar();
        
        JLabel sleepinessLabel = createStyledLabel("Sleepiness:");
        sleepinessBar = createStyledProgressBar();
        
        statusPanel.add(hungerLabel);
        statusPanel.add(hungerBar);
        statusPanel.add(happinessLabel);
        statusPanel.add(happinessBar);
        statusPanel.add(sleepinessLabel);
        statusPanel.add(sleepinessBar);
        
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // Button panel with enhanced styling
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Match status panel's gradient with enhanced colors
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(245, 238, 248, 240), // Light lilac with transparency
                    0, getHeight(), new Color(235, 227, 253, 240)  // Soft purple with transparency
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add sparkly pattern
                long currentTime = System.currentTimeMillis();
                for (int i = 0; i < 20; i++) {
                    double angle = (currentTime / 1000.0 + i) * Math.PI / 6;
                    int x = (int)(getWidth() / 2 + Math.cos(angle) * getWidth() / 3);
                    int y = (int)(getHeight() / 2 + Math.sin(angle) * getHeight() / 3);
                    int sparkSize = 3 + (int)(Math.sin(currentTime / 500.0 + i) * 2);
                    
                    // Sparkle with changing opacity
                    int alpha = 40 + (int)(Math.sin(currentTime / 700.0 + i) * 20);
                    g2d.setColor(new Color(255, 255, 255, alpha));
                    g2d.fillOval(x, y, sparkSize, sparkSize);
                }
                
                // Add subtle geometric pattern
                g2d.setColor(new Color(255, 255, 255, 30));
                int patternSize = Math.min(getWidth(), getHeight()) / 8;
                for (int i = 0; i < getWidth(); i += patternSize * 2) {
                    for (int j = 0; j < getHeight(); j += patternSize * 2) {
                        g2d.fillRoundRect(i, j, patternSize, patternSize, 8, 8);
                    }
                }
                
                // Add shimmering effect
                RadialGradientPaint shimmer = new RadialGradientPaint(
                    new Point(getWidth()/2, getHeight()/2),
                    Math.max(getWidth(), getHeight()),
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{
                        new Color(255, 255, 255, 0),
                        new Color(255, 255, 255, 20),
                        new Color(255, 255, 255, 0)
                    }
                );
                g2d.setPaint(shimmer);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        feedButton = createStyledButton("Feed");
        playButton = createStyledButton("Play");
        sleepButton = createStyledButton("Sleep");
        
        // In the button panel section
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        // Create a panel for main game buttons
        JPanel gameButtonPanel = new JPanel();
        gameButtonPanel.setOpaque(false);
        gameButtonPanel.add(feedButton);
        gameButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        gameButtonPanel.add(playButton);
        gameButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        gameButtonPanel.add(sleepButton);
        
        // Add game buttons panel
        buttonPanel.add(gameButtonPanel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Vertical spacing
        
        // Create logout button panel
        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(false);
        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setPreferredSize(new Dimension(120, 35));
        
        // Add hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Stop any playing sounds
                if (sleepSound != null) sleepSound.stop();
                if (eatSound != null) eatSound.stop();
                
                // Get all windows owned by this application
                Window[] windows = Window.getWindows();
                
                // Create and show new login window
                Login loginFrame = new Login();
                loginFrame.setVisible(true);
                
                // Close all other windows
                for (Window window : windows) {
                    if (window instanceof JFrame && window != loginFrame) {
                        window.dispose();
                    }
                }
            }
        });
        
        logoutPanel.add(logoutButton);
        buttonPanel.add(logoutPanel);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Update action listeners
        feedButton.addActionListener(e -> feed());
        playButton.addActionListener(e -> {
            happinessBar.setValue(Math.min(100, happinessBar.getValue() + 20));
            // Store current state
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            
            // Start SubwayGame
            SubwayGame.startGame(currentFrame);
            
            // Add return button to SubwayGame that restores MainGame
            JButton returnButton = new JButton("Return to Cat");
            returnButton.addActionListener(ev -> {
                currentFrame.getContentPane().removeAll();
                currentFrame.add(mainPanel);
                currentFrame.revalidate();
                currentFrame.repaint();
                
                // Reset focus and continue cat game loop
                mainPanel.requestFocusInWindow();
                startGameLoop();
            });
        });
        sleepButton.addActionListener(e -> sleep());

        add(mainPanel);

        // Start the game loop
        startGameLoop();
    }

    // Remove the play() method since we're using SubwayGame directly
    void feed() {
        hungerBar.setValue(Math.min(100, hungerBar.getValue() + 20));
        FoodItem food = new FoodItem(this);
        mainPanel.add(food);
        mainPanel.revalidate();
        mainPanel.repaint();
        
        // Play eating sound
        if (eatSound != null) {
            eatSound.setFramePosition(0);
            eatSound.start();
        }
    }

    private void sleep() {
        catPanel.toggleSleep();
        if (sleepButton.getText().equals("Sleep")) {
            sleepButton.setText("Wake Up");
            sleepinessBar.setValue(Math.min(100, sleepinessBar.getValue() + 20));
            happinessBar.setValue(Math.min(100, happinessBar.getValue() + 10));
            // Play sleep sound
            if (sleepSound != null) {
                sleepSound.setFramePosition(0);
                sleepSound.start();
            }
        } else {
            sleepButton.setText("Sleep");
            if (sleepSound != null) {
                sleepSound.stop();
            }
        }
    }

    private void startGameLoop() {
        Timer timer = new Timer(3000, e -> {
            hungerBar.setValue(Math.max(0, hungerBar.getValue() - 2));
            happinessBar.setValue(Math.max(0, happinessBar.getValue() - 1));
            sleepinessBar.setValue(Math.max(0, sleepinessBar.getValue() - 1));  // Changed variable name
            
            updateCatMood();
        });
        timer.start();
    }

    private void updateCatMood() {
        // Check if all bars are at 100%
        if (hungerBar.getValue() == 100 && happinessBar.getValue() == 100 && sleepinessBar.getValue() == 100) {
            JOptionPane.showMessageDialog(this, "Peliharaanmu sehat dan bahagia!", "Status Kucing", JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Update cat's animations
        catPanel.updateBlinking();
        catPanel.updateBreathing();
        
        // Existing mood logic
        if (hungerBar.getValue() < 30 || happinessBar.getValue() < 30 || sleepinessBar.getValue() < 30) {
            catPanel.setMood(0); // sad
        } else if (hungerBar.getValue() < 60 || happinessBar.getValue() < 60 || sleepinessBar.getValue() < 60) {
            catPanel.setMood(1); // normal
        } else {
            catPanel.setMood(2); // happy
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGame game = new MainGame();
            game.setVisible(true);
        });
    }
    // Add this getter method
    public CatPanel getCatPanel() {
        return catPanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(DARK_PURPLE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }
    
    private JProgressBar createStyledProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setForeground(DARK_PURPLE);
        bar.setBackground(WHITE);
        bar.setBorderPainted(false);
        return bar;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DARK_PURPLE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 35));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(81, 45, 168));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DARK_PURPLE);
            }
        });
        
        return button;
    }
}
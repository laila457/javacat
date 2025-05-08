package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;

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
    
    public MainGame() {
        setTitle("Virtual Cat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create soft gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(230, 230, 250),
                    0, getHeight(), new Color(209, 196, 233)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add decorative circles
                drawDecorations(g2d);
            }
            
            private void drawDecorations(Graphics2D g2d) {
                // Draw floating bubbles
                for (int i = 0; i < 15; i++) {
                    int size = (int) (Math.random() * 40) + 20;
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    
                    // Create bubble gradient
                    GradientPaint bubbleGradient = new GradientPaint(
                        x, y, new Color(255, 255, 255, 30),
                        x + size, y + size, new Color(255, 255, 255, 5)
                    );
                    g2d.setPaint(bubbleGradient);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Draw subtle pattern
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 30) {
                    for (int j = 0; j < getHeight(); j += 30) {
                        g2d.drawRoundRect(i, j, 20, 20, 5, 5);
                    }
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(SOFT_PURPLE);
        
        // Cat display area
        catPanel = new CatPanel();
        mainPanel.add(catPanel, BorderLayout.CENTER);

        // Status bars dengan tema yang selaras
        JPanel statusPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        statusPanel.setBackground(SOFT_PURPLE);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Styling labels dan progress bars
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

        // Button panel dengan styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(SOFT_PURPLE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        feedButton = createStyledButton("Feed");
        playButton = createStyledButton("Play");
        sleepButton = createStyledButton("Sleep");
        
        buttonPanel.add(feedButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(sleepButton);
        
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
    }

    private void sleep() {
        catPanel.toggleSleep();
        if (sleepButton.getText().equals("Sleep")) {
            sleepButton.setText("Wake Up");
            sleepinessBar.setValue(Math.min(100, sleepinessBar.getValue() + 20));
            happinessBar.setValue(Math.min(100, happinessBar.getValue() + 10));
        } else {
            sleepButton.setText("Sleep");
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
        if (hungerBar.getValue() < 30 || happinessBar.getValue() < 30 || sleepinessBar.getValue() < 30) {  // Changed variable
            catPanel.setMood(0); // sad
        } else if (hungerBar.getValue() < 60 || happinessBar.getValue() < 60 || sleepinessBar.getValue() < 60) {  // Changed variable
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
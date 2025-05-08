package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton goToRegisterButton;
    
    // Definisi warna tema
    private static final Color SOFT_PURPLE = new Color(230, 230, 250);
    private static final Color DARK_PURPLE = new Color(103, 58, 183);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    
    public Login() {
        setTitle("Virtual Cat - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Set warna background frame
        getContentPane().setBackground(SOFT_PURPLE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SOFT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Judul
        JLabel titleLabel = new JLabel("Virtual Cat Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_PURPLE);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 30, 10);
        panel.add(titleLabel, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(BLACK);
        panel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setBackground(WHITE);
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(BLACK);
        panel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBackground(WHITE);
        panel.add(passwordField, gbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(SOFT_PURPLE);
        loginButton = new JButton("Login");
        goToRegisterButton = new JButton("Register");
        
        // Styling buttons
        styleButton(loginButton);
        styleButton(goToRegisterButton);
        
        loginButton.addActionListener(e -> handleLogin());
        goToRegisterButton.addActionListener(e -> {
            dispose();
            new Register().setVisible(true);
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0))); // Spacing between buttons
        buttonPanel.add(goToRegisterButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(buttonPanel, gbc);
        
        add(panel);
    }
    
    private void styleButton(JButton button) {
        button.setBackground(DARK_PURPLE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(81, 45, 168));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DARK_PURPLE);
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new MainGame().setVisible(true);
                LoginManager.setCurrentUsername(username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and show splash screen
            JWindow splash = new JWindow();
            splash.setSize(400, 300);
            splash.setLocationRelativeTo(null);
            
            // Create panel with gradient background
            JPanel splashPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    GradientPaint gradient = new GradientPaint(0, 0, SOFT_PURPLE, 0, getHeight(), DARK_PURPLE);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            splashPanel.setLayout(new BorderLayout());
            
            // Load and display logo
            try {
                ImageIcon logoIcon = new ImageIcon("c:\\Java\\kucing(update 30 april)\\src\\main\\resources\\logo.png");
                Image logoImage = logoIcon.getImage();
                if (logoImage != null) {
                    logoImage = logoImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
                    logoLabel.setHorizontalAlignment(JLabel.CENTER);
                    splashPanel.add(logoLabel, BorderLayout.CENTER);
                    
                    // Add loading text below logo
                    JLabel loadingLabel = new JLabel("Loading...");
                    loadingLabel.setForeground(WHITE);
                    loadingLabel.setHorizontalAlignment(JLabel.CENTER);
                    loadingLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    splashPanel.add(loadingLabel, BorderLayout.SOUTH);
                } else {
                    System.out.println("Failed to load logo image");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error loading logo: " + e.getMessage());
            }
            
            splash.setContentPane(splashPanel);
            splash.setVisible(true);
            
            // Show splash for 3 seconds then start main app
            Timer timer = new Timer(3000, e -> {
                splash.dispose();
                new Login().setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
}
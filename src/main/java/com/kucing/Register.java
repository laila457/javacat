package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Register extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton goToLoginButton;
    
    // Definisi warna tema
    private static final Color SOFT_PURPLE = new Color(230, 230, 250);
    private static final Color DARK_PURPLE = new Color(103, 58, 183);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    
    public Register() {
        setTitle("Virtual Cat - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);  // Changed height from 300 to 350
        setLocationRelativeTo(null);
        
        // Add FICT logo at bottom
        JLabel fictLabel = new JLabel();
        try {
            ImageIcon fictIcon = new ImageIcon("c:\\Java\\javacat\\src\\main\\resources\\fict.png");
            Image fictImage = fictIcon.getImage();
            Image scaledFict = fictImage.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
            fictLabel = new JLabel(new ImageIcon(scaledFict));
            fictLabel.setBounds(12, 255, 100, 50);  // Adjusted Y position to 255
            add(fictLabel);
        } catch (Exception e) {
            System.out.println("Error loading FICT logo: " + e.getMessage());
        }
        
        // Set warna background frame
        getContentPane().setBackground(SOFT_PURPLE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SOFT_PURPLE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Judul
        JLabel titleLabel = new JLabel("Virtual Cat Register");
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
        registerButton = new JButton("Register");
        goToLoginButton = new JButton("Back to Login");
        
        // Styling buttons
        styleButton(registerButton);
        styleButton(goToLoginButton);
        goToLoginButton.setBackground(new Color(169, 169, 169)); // Changed to gray
        
        // Add hover effect specifically for back to login button
        goToLoginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                goToLoginButton.setBackground(new Color(128, 128, 128));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                goToLoginButton.setBackground(new Color(169, 169, 169));
            }
        });
        
        registerButton.addActionListener(e -> handleRegister());
        goToLoginButton.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0))); // Spacing between buttons
        buttonPanel.add(goToLoginButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 40, 10);  // Changed bottom inset from 10 to 40
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
    
    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Enhanced input validation
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!");
            return;
        }

        // Password length validation
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password harus minimal 6 karakter!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if username exists
            String checkQuery = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan!");
                return;
            }
            
            // Hash the password using SHA-256
            String hashedPassword = hashPassword(password);
            
            // Insert new user with hashed password
            String insertQuery = "INSERT INTO users (username, password, created_at) VALUES (?, ?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            dispose();
            new Login().setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
    
    // Add this new method for password hashing
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
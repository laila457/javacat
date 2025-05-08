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
        setSize(400, 300);
        setLocationRelativeTo(null);
        
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
    
    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Validasi input
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registrasi berhasil!");
            dispose();
            new Login().setVisible(true);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username sudah digunakan!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
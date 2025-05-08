package main.java.com.kucing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Leaderboard {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/virtual_cat";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void addScore(int score) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Get the current logged-in username from LoginManager
            String username = LoginManager.getCurrentUsername();
            System.out.println("Current username: " + username);
            System.out.println("Score to save: " + score);
            
            // Check if username is not null
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: No user logged in");
                return;
            }
            
            String sql = "INSERT INTO game_scores (user_id, score) VALUES ((SELECT id FROM users WHERE username = ?), ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving score: " + e.getMessage());
        }
    }

    public static void showLeaderboard(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Leaderboard", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(230, 230, 250));
        
        // Create title panel with 3D effect
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(103, 58, 183),
                    0, getHeight(), new Color(81, 45, 168)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add 3D text effect
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String title = "LEADERBOARD";
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(title, x + 2, y + 2);
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.drawString(title, x, y);
            }
        };
        titlePanel.setPreferredSize(new Dimension(500, 60));
        dialog.add(titlePanel, BorderLayout.NORTH);
        
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Rank");
        model.addColumn("Player");
        model.addColumn("Score");
        model.addColumn("Date");
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom rendering for alternating rows and 3D effect
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 255) : new Color(248, 248, 255));
                } else {
                    c.setBackground(new Color(103, 58, 183, 100));
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                setHorizontalAlignment(column == 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                
                return c;
            }
        });
        
        // Style the header
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,

            boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(103, 58, 183));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                label.setHorizontalAlignment(column == 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                label.setOpaque(true);
                return label;
            }
        });
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(103, 58, 183));
        
        // Add table data
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT u.username, g.score, g.date_created " +
                        "FROM game_scores g " +
                        "JOIN users u ON g.user_id = u.id " +
                        "ORDER BY g.score DESC LIMIT 10";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    String.format("#%d", rank++),
                    rs.getString("username"),
                    rs.getInt("score"),
                    rs.getTimestamp("date_created")
                });
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading leaderboard: " + e.getMessage());
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Styled close button
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(103, 58, 183));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dialog.dispose());
        
        // Add hover effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(81, 45, 168));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(103, 58, 183));
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
}
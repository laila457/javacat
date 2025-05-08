package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FoodItem extends JLabel {
    private Point dragStart = null;
    private MainGame mainGame;
    private boolean isEaten = false;

    public FoodItem(MainGame game) {
        mainGame = game;
        setText("🍱");
        setFont(new Font("Dialog", Font.PLAIN, 30));
        setBounds(50, 50, 40, 40);
        
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null && !isEaten) {
                    Point current = e.getLocationOnScreen();
                    Point offset = getLocationOnScreen();
                    int newX = getX() + current.x - offset.x - dragStart.x;
                    int newY = getY() + current.y - offset.y - dragStart.y;
                    
                    // Keep food within window bounds
                    newX = Math.max(0, Math.min(newX, getParent().getWidth() - getWidth()));
                    newY = Math.max(0, Math.min(newY, getParent().getHeight() - getHeight()));
                    
                    setLocation(newX, newY);
                    
                    // Check if food is near cat's mouth
                    Rectangle catBounds = mainGame.getCatPanel().getBounds();
                    if (getBounds().intersects(catBounds)) {
                        mainGame.getCatPanel().startEating();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Rectangle catBounds = mainGame.getCatPanel().getBounds();
                if (getBounds().intersects(catBounds) && !isEaten) {
                    isEaten = true;
                    mainGame.getCatPanel().startEating();
                    fadeOut();
                    mainGame.feed(); // Add this method to MainGame to update hunger bar
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void fadeOut() {
        Timer fadeTimer = new Timer(50, new ActionListener() {
            float alpha = 1.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.1f;
                if (alpha <= 0) {
                    ((Timer)e.getSource()).stop();
                    getParent().remove(FoodItem.this);
                    getParent().revalidate();
                    getParent().repaint();
                }
                setForeground(new Color(0, 0, 0, Math.max(0, Math.min(1, alpha))));
            }
        });
        fadeTimer.start();
    }
}
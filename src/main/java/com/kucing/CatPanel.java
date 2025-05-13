package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CatPanel extends JPanel {
    // Definisi warna tema yang selaras dengan login
    private static final Color SOFT_PURPLE = new Color(230, 230, 250);
    private static final Color DARK_PURPLE = new Color(103, 58, 183);
    private static final Color LIGHT_PURPLE = new Color(209, 196, 233);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = Color.BLACK;
    
    // Cat colors yang diselaraskan dengan tema login
    private Color catColor = DARK_PURPLE; // Warna utama kucing
    private Color catSecondaryColor = WHITE; // Warna sekunder kucing
    private Color catDarkColor = new Color(81, 45, 168); // Warna detail kucing
    
    // Stats
    private int hunger = 100;
    private int happiness = 100;
    private int energy = 100;
    private Font statsFont = new Font("Arial", Font.BOLD, 12);
    
    // Animation states
    private int mood = 2;
    private boolean isEating = false;
    private boolean isSleeping = false;
    private int animationFrame = 0;
    private Timer animationTimer;
    private int sleepZCount = 0;

    // Blinking animation states
    private boolean isBlinking = false;
    private int blinkDuration = 0;
    private Timer blinkTimer;
    
    // Add new field for tail animation
    private double tailAngle = 0;
    private boolean tailWagging = true;

    // Update the blinkTimer initialization in the constructor
    public CatPanel() {
        setPreferredSize(new Dimension(300, 300));
        animationTimer = new Timer(200, e -> {
            animationFrame = (animationFrame + 1) % 4;
            if (isEating && animationFrame == 0) {
                isEating = false;
                animationTimer.stop();
            }
            if (isSleeping) {
                sleepZCount = animationFrame;
            }
            // Add tail animation
            if (tailWagging && !isSleeping) {
                tailAngle += 0.2;
                if (tailAngle > Math.PI/6) {
                    tailWagging = false;
                }
            } else if (!isSleeping) {
                tailAngle -= 0.2;
                if (tailAngle < -Math.PI/6) {
                    tailWagging = true;
                }
            }
            repaint();
        });
        animationTimer.start(); // Start the animation timer immediately

        // Initialize and start blink timer
        // Update blink timer to 2 seconds with fixed blinking
        blinkTimer = new Timer(2000, e -> {
            if (!isSleeping) {
                isBlinking = true;
                new Timer(150, ev -> {
                    isBlinking = false;
                    ((Timer)ev.getSource()).stop();
                    repaint();
                }).start();
                repaint();
            }
        });
        blinkTimer.start();
    }

    public void startEating() {
        if (!isEating) {  // Prevent multiple eating animations
            isEating = true;
            animationFrame = 0;
            animationTimer.start();  // Use start() instead of restart()
        }
    }

    public void toggleSleep() {
        isSleeping = !isSleeping;
        if (isSleeping) {
            animationTimer.start();
        } else {
            animationTimer.stop();
            sleepZCount = 0;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw full background first
        drawBackground(g2d);

        // Calculate center position
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        
        // Translate for game elements
        g2d.translate(centerX - 150, centerY - 150);
        
        drawCatBed(g2d);
        drawBasicCat(g2d);
        
        // Reset translation for stats
        g2d.translate(-(centerX - 150), -(centerY - 150));
        drawStats(g2d);

        // Translate back for animations
        g2d.translate(centerX - 150, centerY - 150);
        if (isEating) {
            drawEatingAnimation(g2d);
        } else if (isSleeping) {
            drawSleepingCat(g2d);
        } else {
            drawMood(g2d);
        }
        
        // Add darkness overlay when sleeping
        if (isSleeping) {
            g2d.translate(-(centerX - 150), -(centerY - 150));
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            
            // Add moonlight effect
            RadialGradientPaint moonlight = new RadialGradientPaint(
                new Point(panelWidth - 100, 100),
                200f,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{
                    new Color(255, 255, 255, 15),
                    new Color(200, 200, 255, 10),
                    new Color(0, 0, 0, 0)
                }
            );
            g2d.setPaint(moonlight);
            g2d.fillOval(panelWidth - 300, -100, 400, 400);
            g2d.translate(centerX - 150, centerY - 150);
        }
        
        // Final reset translation
        g2d.translate(-(centerX - 150), -(centerY - 150));
    }
    
    private void drawStats(Graphics2D g2d) {
        g2d.setFont(statsFont);
        
        // Stats di pojok kanan atas

    }

    // Add these methods to update stats
    public void updateHunger(int value) {
        hunger = Math.max(0, Math.min(100, hunger + value));
        repaint();
    }

    public void updateHappiness(int value) {
        happiness = Math.max(0, Math.min(100, happiness + value));
        repaint();
    }

    public void updateEnergy(int value) {
        energy = Math.max(0, Math.min(100, energy + value));
        repaint();
    }

    private void drawBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 248, 220),
            0, height, new Color(255, 235, 205)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Wallpaper pattern scaled to screen
        g2d.setColor(new Color(139, 69, 19, 30));
        int patternSize = Math.min(width, height) / 8;
        for (int i = 0; i < width; i += patternSize) {
            for (int j = 0; j < height; j += patternSize) {
                g2d.drawRect(i, j, patternSize, patternSize);
                g2d.drawLine(i, j, i + patternSize, j + patternSize);
            }
        }

        // Furniture scaled to screen size
        int sofaWidth = width / 3;
        int sofaHeight = height / 8;
        int sofaY = height - (height / 4);
        
        // Draw sofa
        g2d.setColor(new Color(101, 67, 33, 40));
        g2d.fillRoundRect(width/3, sofaY, sofaWidth, sofaHeight, 20, 20);
        g2d.fillRoundRect(width/3 + 10, sofaY - 20, sofaWidth - 20, sofaHeight/2, 10, 10);
        
        // Side table
        int tableWidth = width / 10;
        g2d.fillRect(width - tableWidth - 20, sofaY - 20, tableWidth, height/6);
        
        // Lamp with dynamic positioning
        int lampSize = width / 20;
        g2d.setColor(new Color(255, 223, 186, 60));
        g2d.fillOval(width - tableWidth - lampSize/2, sofaY - height/4, lampSize, lampSize);
        
        // Lamp stand
        g2d.setColor(new Color(101, 67, 33, 40));
        g2d.fillRect(width - tableWidth - lampSize/3, sofaY - height/6, lampSize/4, height/12);
        
        // Enhanced lamp glow
        RadialGradientPaint lampGlow = new RadialGradientPaint(
            new Point(width - tableWidth - lampSize/2, sofaY - height/4),
            width/6f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 255, 200, 40),
                new Color(255, 255, 200, 20),
                new Color(255, 255, 200, 0)
            }
        );
        g2d.setPaint(lampGlow);
        g2d.fillOval(width - width/3, sofaY - height/3, width/3, height/3);
    }

    private void drawCatBed(Graphics2D g2d) {
        // Cat bed base
        g2d.setColor(DARK_PURPLE);
        g2d.fillOval(90, 200, 120, 40);
        
        // Bed inner
        g2d.setColor(LIGHT_PURPLE);
        g2d.fillOval(100, 205, 100, 30);
        
        // Bed pattern
        g2d.setColor(new Color(DARK_PURPLE.getRed(), 
                              DARK_PURPLE.getGreen(), 
                              DARK_PURPLE.getBlue(), 100));
        for(int i = 0; i < 3; i++) {
            g2d.drawArc(110 + (i * 30), 210, 20, 20, 0, 180);
        }

        // Add bedside lamp
        // Lamp base
        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRect(220, 190, 15, 30);
        
        // Lamp neck (adjustable part)
        g2d.setColor(new Color(180, 180, 180));
        int[] xPoints = {227, 230, 233, 230};
        int[] yPoints = {190, 160, 163, 193};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Lamp shade
        g2d.setColor(new Color(255, 223, 186));
        g2d.fillOval(215, 145, 30, 20);
        g2d.setColor(new Color(101, 67, 33));
        g2d.drawOval(215, 145, 30, 20);
        
        // Enhanced bedside lamp
        // Larger lamp base
        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRoundRect(250, 180, 25, 40, 8, 8);
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRoundRect(245, 210, 35, 10, 5, 5);
        
        // Improved adjustable neck
        g2d.setColor(new Color(192, 192, 192));
        int[] neckX = {262, 265, 268, 265, 262, 259};
        int[] neckY = {180, 150, 152, 155, 153, 182};
        g2d.fillPolygon(neckX, neckY, 6);
        
        // Larger, more detailed lamp shade
        g2d.setColor(new Color(255, 223, 186));
        g2d.fillOval(240, 125, 50, 35);
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawOval(240, 125, 50, 35);
        
        // Decorative pattern on shade
        g2d.setColor(new Color(139, 69, 19, 60));
        for (int i = 0; i < 4; i++) {
            g2d.drawLine(245 + (i * 12), 125, 245 + (i * 12), 160);
        }
        
        // Enhanced lamp glow effect
        if (!isSleeping) {
            RadialGradientPaint lampGlow = new RadialGradientPaint(
                new Point(265, 142),
                120f,
                new float[]{0.0f, 0.3f, 0.7f, 1.0f},
                new Color[]{
                    new Color(255, 255, 200, 50),
                    new Color(255, 255, 200, 35),
                    new Color(255, 255, 200, 20),
                    new Color(255, 255, 200, 0)
                }
            );
            g2d.setPaint(lampGlow);
            g2d.fillOval(205, 82, 120, 120);
        }
    }

    // Add breathing animation states
    private double breathingScale = 1.0;
    private boolean breathingIn = true;
    private Timer breathingTimer;

    public void updateBreathing() {
        if (breathingTimer == null) {
            breathingTimer = new Timer(50, e -> {
                if (!isEating && !isSleeping) {
                    if (breathingIn) {
                        breathingScale += 0.003;
                        if (breathingScale >= 1.03) {
                            breathingIn = false;
                        }
                    } else {
                        breathingScale -= 0.003;
                        if (breathingScale <= 1.0) {
                            breathingIn = true;
                        }
                    }
                    repaint();
                }
            });
            breathingTimer.start();
        }
    }

    // Modify drawBasicCat to include breathing animation
    private void drawBasicCat(Graphics2D g2d) {
        // Save the current transform
        AffineTransform oldTransform = g2d.getTransform();
        
        // Apply breathing scale
        g2d.translate(150, 150);
        g2d.scale(breathingScale, breathingScale);
        g2d.translate(-150, -150);
        
        // Body yang lebih ramping dan elegan untuk Anggora
        g2d.setColor(catColor);
        g2d.fillOval(95, 95, 110, 115); // Mengurangi lebar dan menyesuaikan tinggi
        
        // Ekor yang lebih panjang dan anggun
        g2d.setColor(catColor);
        QuadCurve2D tail = new QuadCurve2D.Float(195, 150, 245, 110, 225, 85);
        g2d.fill(tail);
        
        // Kaki yang lebih ramping
        g2d.setColor(catSecondaryColor);
        g2d.fillOval(105, 180, 30, 20); // Kaki kiri depan
        g2d.fillOval(165, 180, 30, 20); // Kaki kanan depan
        
        // Dada putih yang lebih ramping
        g2d.setColor(catSecondaryColor);
        g2d.fillOval(130, 140, 40, 65);
        
        // Telinga yang lebih runcing
        g2d.setColor(catColor);
        int[] xPoints = {110, 130, 145};
        int[] yPoints = {110, 65, 110}; // Telinga lebih tinggi
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(new Color(255, 192, 203));
        int[] xPointsInner = {115, 130, 140};
        int[] yPointsInner = {110, 70, 110};
        g2d.fillPolygon(xPointsInner, yPointsInner, 3);
        
        // Telinga kanan
        g2d.setColor(catColor);
        int[] xPoints2 = {155, 170, 190};
        int[] yPoints2 = {110, 65, 110}; // Telinga lebih tinggi
        g2d.fillPolygon(xPoints2, yPoints2, 3);
        g2d.setColor(new Color(255, 192, 203));
        int[] xPointsInner2 = {160, 170, 185};
        int[] yPointsInner2 = {110, 70, 110};
        g2d.fillPolygon(xPointsInner2, yPointsInner2, 3);
        
        // Mata yang lebih imut dan berkaca-kaca
        if (!isSleeping) {
            // Mata almond shape yang lebih besar dan bulat
            g2d.setColor(Color.WHITE);
            g2d.fillOval(120, 125, 30, 28);
            g2d.fillOval(155, 125, 30, 28);
            
            // Iris mata ungu yang lebih besar dan lembut
            g2d.setColor(new Color(180, 130, 255));
            g2d.fillOval(126, 130, 18, 18);
            g2d.fillOval(161, 130, 18, 18);
            
            // Kilau mata utama (lebih besar)
            g2d.setColor(Color.WHITE);
            g2d.fillOval(128, 132, 8, 8);
            g2d.fillOval(163, 132, 8, 8);
            
            // Kilau mata tambahan untuk efek berkaca-kaca
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.fillOval(132, 138, 6, 6);
            g2d.fillOval(167, 138, 6, 6);
            
            // Efek berkaca-kaca di bagian bawah mata
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.fillOval(126, 142, 18, 6);
            g2d.fillOval(161, 142, 18, 6);
            
            // Bulu mata yang lebih lentik
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Bulu mata atas yang lebih lentik
            for(int i = 0; i < 5; i++) {
                double angle = Math.PI/3 + (i * Math.PI/12);
                int length = 8;
                int startX = 135 + (i == 2 ? 0 : (i < 2 ? -15 : 15));
                int startY = 130;
                int endX = startX + (int)(Math.cos(angle) * length);
                int endY = startY - (int)(Math.sin(angle) * length);
                g2d.drawLine(startX, startY, endX, endY);
                
                // Bulu mata untuk mata kanan
                startX += 35;
                endX += 35;
                g2d.drawLine(startX, startY, endX, endY);
            }
        }
        
        // Hidung yang lebih bulat dan pink
        g2d.setColor(new Color(255, 182, 193));
        g2d.fillOval(143, 152, 14, 12);
        g2d.setColor(new Color(255, 150, 150));
        g2d.drawOval(143, 152, 14, 12);
    
        // Kumis yang sederhana
        g2d.setColor(Color.BLACK);
        g2d.drawLine(120, 160, 85, 155);
        g2d.drawLine(120, 165, 85, 165);
        g2d.drawLine(180, 160, 215, 155);
        g2d.drawLine(180, 165, 215, 165);
    }

    // Metode helper untuk menggambar kumis melengkung
    private void drawCurvedLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        QuadCurve2D curve = new QuadCurve2D.Float(
            x1, y1,
            (x1 + x2) / 2, y1 + ((y2 > y1) ? 5 : -5),
            x2, y2
        );
        g2d.draw(curve);
    }

    private void drawSleepingCat(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Closed eyes with curved lines
        g2d.draw(new Arc2D.Double(125, 135, 20, 10, 0, 180, Arc2D.OPEN));
        g2d.draw(new Arc2D.Double(155, 135, 20, 10, 0, 180, Arc2D.OPEN));
        
        // Sleeping mouth (slightly open)
        g2d.setColor(new Color(255, 150, 150));
        g2d.fillOval(140, 160, 20, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(140, 160, 20, 15);
        
        // Drooling animation
        g2d.setColor(new Color(135, 206, 235, 200)); // Light blue, semi-transparent
        int droolLength = 5 + (animationFrame * 2);
        g2d.fillOval(145, 175, 4, droolLength);
        
        // Add small bubbles for snoring effect
        int bubbleSize = 5 + (animationFrame * 2);
        g2d.setColor(new Color(0, 0, 0, 0.3f));
        g2d.drawOval(170, 150 - bubbleSize/2, bubbleSize, bubbleSize);
        
        // Animated Zs with fade effect
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        for (int i = 0; i <= sleepZCount; i++) {
            float alpha = 1.0f - (i * 0.25f);
            g2d.setColor(new Color(0, 0, 0, Math.max(0.2f, alpha)));
            g2d.drawString("Z", 190 + (i * 20), 100 - (i * 15));
        }
    }

    private void drawEatingAnimation(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Draw plate instead of bowl
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(120, 180, 60, 15);
        
        switch (animationFrame) {
            case 0:
                // Draw detailed fish and open mouth
                drawFish(g2d, 135, 170);
                g2d.setColor(new Color(255, 150, 150));
                g2d.fillOval(140, 165, 20, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(140, 165, 20, 10);
                break;
            case 1:
                // Fish closer to mouth
                drawFish(g2d, 140, 170);
                g2d.setColor(new Color(255, 150, 150));
                g2d.fillOval(140, 168, 20, 15);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(140, 168, 20, 15);
                break;
            case 2:
                // Eating fish (only tail visible)
                g2d.setColor(new Color(255, 150, 150));
                g2d.fillOval(140, 170, 20, 15);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(140, 170, 20, 15);
                drawFishTail(g2d, 155, 172);
                break;
            case 3:
                // Chewing
                g2d.setColor(new Color(255, 150, 150));
                g2d.fillOval(140, 168, 20, 12);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(140, 168, 20, 12);
                break;
        }
        
        // Draw eating effects
        if (animationFrame == 2) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("nom", 165, 160);
            g2d.drawString("yum!", 155, 170);
        }
    }

    // Helper method to draw detailed fish
    private void drawFish(Graphics2D g2d, int x, int y) {
        // Fish body
        g2d.setColor(new Color(255, 140, 0));
        g2d.fillOval(x, y, 20, 10);
        
        // Fish tail
        drawFishTail(g2d, x + 20, y + 2);
        
        // Fish fins
        int[] finX = {x + 10, x + 15, x + 10};
        int[] finY = {y, y - 5, y + 2};
        g2d.fillPolygon(finX, finY, 3);
        
        // Fish eye
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + 3, y + 2, 3, 3);
        
        // Fish scales
        g2d.setColor(new Color(255, 160, 0));
        g2d.drawArc(x + 8, y + 2, 5, 5, 0, 180);
        g2d.drawArc(x + 13, y + 2, 5, 5, 0, 180);
    }

    // Helper method to draw fish tail
    private void drawFishTail(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(255, 140, 0));
        int[] tailX = {x, x + 8, x};
        int[] tailY = {y - 3, y + 2, y + 7};
        g2d.fillPolygon(tailX, tailY, 3);
    }

    private void drawMood(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        if (mood == 0) {
            g2d.draw(new Arc2D.Double(135, 160, 30, 20, 0, 180, Arc2D.OPEN));
        } else if (mood == 1) {
            g2d.drawLine(135, 170, 165, 170);
        } else {
            g2d.draw(new Arc2D.Double(135, 155, 30, 20, 180, 180, Arc2D.OPEN));
        }
    }

    public void setMood(int mood) {
        this.mood = mood;
        repaint();
    }

    public void updateBlinking() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBlinking'");
    }
}
package main.java.com.kucing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.sound.sampled.*;
import java.io.File;

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
    // Add at the top with other fields
    private Clip eatSound;
    
    // In the constructor, after other initializations
    public CatPanel() {
        setPreferredSize(new Dimension(300, 300));
        
        // Initialize eat sound
        try {
            AudioInputStream eatStream = AudioSystem.getAudioInputStream(
                new File("c:\\Java\\javacat\\src\\main\\resources\\sounds\\eat.wav"));
            eatSound = AudioSystem.getClip();
            eatSound.open(eatStream);
        } catch (Exception e) {
            System.out.println("Error loading eat sound: " + e.getMessage());
            e.printStackTrace();
        }

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
            animationTimer.start();
            
            // Play eating sound
            if (eatSound != null) {
                eatSound.setFramePosition(0);
                eatSound.start();
            }
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

    // Add this field at the top of the class with other fields
    private long currentTime = 0;

    // In the drawBackground method, update the currentTime before drawing the lamp
    private void drawBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Modern gradient background with soft colors
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(245, 238, 248), // Light lilac
            width, height, new Color(235, 227, 253)  // Soft purple
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Add modern geometric patterns
        g2d.setColor(new Color(255, 255, 255, 40));
        int patternSize = Math.min(width, height) / 15;
        for (int i = 0; i < width; i += patternSize * 2) {
            for (int j = 0; j < height; j += patternSize * 2) {
                g2d.fillRoundRect(i, j, patternSize, patternSize, 15, 15);
            }
        }

        // Modern minimalist furniture
        int sofaWidth = width / 3;
        int sofaHeight = height / 7;
        int sofaY = height - (height / 4);
        
        // Add modern TV on the wall
        int tvWidth = width / 4;
        int tvHeight = height / 5;
        int tvY = height / 3;
        
        // TV Mount
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRoundRect(width/2 - tvWidth/2 - 5, tvY - 5, tvWidth + 10, tvHeight + 10, 10, 10);
        
        // TV Screen
        GradientPaint screenGradient = new GradientPaint(
            width/2, tvY, new Color(20, 20, 20),
            width/2, tvY + tvHeight, new Color(40, 40, 40)
        );
        g2d.setPaint(screenGradient);
        g2d.fillRoundRect(width/2 - tvWidth/2, tvY, tvWidth, tvHeight, 5, 5);
        
        // TV Stand
        g2d.setColor(new Color(80, 80, 80));
        g2d.fillRect(width/2 - 10, tvY + tvHeight, 20, 10);
        
        // Add modern ceiling lamp
        int lampRadius = width / 6;
        int lampY = height / 7;
        
        // Update current time for animations
        currentTime = System.currentTimeMillis();

        // Crystal-like decorative elements
        g2d.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            int crystalX = width/2 + (int)(Math.cos(angle) * lampRadius/3);
            int crystalY = lampY + (int)(Math.sin(angle) * lampRadius/4);
            g2d.fillOval(crystalX - 5, crystalY - 5, 10, 10);
            
            // Add sparkle effect to each crystal
            g2d.setColor(new Color(255, 255, 255, 150 + (int)(Math.sin(currentTime/500.0 + i) * 50)));
            g2d.fillOval(crystalX - 2, crystalY - 2, 4, 4);
        }
        
        // Decorative ceiling mount with metallic effect
        GradientPaint metallicPaint = new GradientPaint(
            width/2 - 20, 0, new Color(220, 220, 220),
            width/2 + 20, 15, new Color(180, 180, 180)
        );
        g2d.setPaint(metallicPaint);
        g2d.fillRoundRect(width/2 - 20, 0, 40, 15, 8, 8);
        
        // Multiple lamp cables with shimmer
        g2d.setColor(new Color(192, 192, 192));
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 3; i++) {
            int offset = (i - 1) * 10;
            g2d.setColor(new Color(192, 192, 192, 150 + (int)(Math.sin(currentTime/800.0 + i) * 50)));
            g2d.drawLine(width/2 + offset, 15, width/2, lampY);
        }
        
        // Enhanced lamp shade with crystal-like gradient
        GradientPaint crystalGradient = new GradientPaint(
            width/2, lampY, new Color(255, 255, 255, 220),
            width/2, lampY + lampRadius, new Color(240, 240, 255, 200)
        );
        g2d.setPaint(crystalGradient);
        g2d.fillArc(width/2 - lampRadius/2, lampY, lampRadius, lampRadius/2, 0, 180);
        
        // Metallic rim with shine
        g2d.setColor(new Color(220, 220, 220));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawArc(width/2 - lampRadius/2, lampY, lampRadius, lampRadius/2, 0, 180);
        
        // Enhanced dynamic glow effect
        float glowIntensity = 0.6f + (float)Math.sin(currentTime/1000.0) * 0.1f;
        RadialGradientPaint enhancedGlow = new RadialGradientPaint(
            new Point(width/2, lampY + lampRadius/4),
            lampRadius * 2f,
            new float[]{0.0f, 0.2f, 0.4f, 1.0f},
            new Color[]{
                new Color(255, 255, 240, (int)(80 * glowIntensity)),
                new Color(255, 255, 240, (int)(60 * glowIntensity)),
                new Color(255, 255, 240, (int)(40 * glowIntensity)),
                new Color(255, 255, 240, 0)
            }
        );
        g2d.setPaint(enhancedGlow);
        g2d.fillOval(width/2 - lampRadius, lampY, lampRadius * 2, lampRadius);
        
        // Sparkle effects
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * 2 * i / 12 + currentTime/1000.0;
            int sparkleX = width/2 + (int)(Math.cos(angle) * lampRadius/2);
            int sparkleY = lampY + lampRadius/4 + (int)(Math.sin(angle) * lampRadius/4);
            int sparkleSize = 2 + (int)(Math.sin(currentTime/500.0 + i) * 2);
            g2d.fillOval(sparkleX - sparkleSize/2, sparkleY - sparkleSize/2, sparkleSize, sparkleSize);
        }

        // Sleek sofa with modern gradient
        GradientPaint sofaGradient = new GradientPaint(
            width/3, sofaY, new Color(190, 180, 220),
            width/3, sofaY + sofaHeight, new Color(160, 150, 190)
        );
        g2d.setPaint(sofaGradient);
        g2d.fillRoundRect(width/3, sofaY, sofaWidth, sofaHeight, 40, 40);
        g2d.fillRoundRect(width/3 + 10, sofaY - 25, sofaWidth - 20, sofaHeight/2, 30, 30);

        // Modern cushions with subtle shadows
        g2d.setColor(new Color(210, 200, 230, 180));
        for (int i = 0; i < 3; i++) {
            g2d.fillRoundRect(width/3 + 25 + (i * sofaWidth/3), sofaY + 8, 
                            sofaWidth/4 - 10, sofaHeight - 15, 25, 25);
            // Cushion shadow
            g2d.setColor(new Color(0, 0, 0, 15));
            g2d.fillRoundRect(width/3 + 27 + (i * sofaWidth/3), sofaY + 10, 
                            sofaWidth/4 - 10, sofaHeight - 15, 25, 25);
            g2d.setColor(new Color(210, 200, 230, 180));
        }

        // Floating side table
        int tableWidth = width / 7;
        GradientPaint tableGradient = new GradientPaint(
            width - tableWidth - 20, sofaY - 20, new Color(180, 170, 210),
            width - 20, sofaY + height/6, new Color(150, 140, 180)
        );
        g2d.setPaint(tableGradient);
        g2d.fillRoundRect(width - tableWidth - 20, sofaY - 20, tableWidth, height/6, 30, 30);
        
        // Table shadow
        g2d.setColor(new Color(0, 0, 0, 20));
        g2d.fillRoundRect(width - tableWidth - 18, sofaY - 18, tableWidth, height/6, 30, 30);

        // Modern ambient lighting with multiple layers
        createAmbientLight(g2d, width, height);
        
        // Add floating particles effect
        drawFloatingParticles(g2d, width, height);
        
        // Add decorative paintings
        drawPaintings(g2d, width, height);
    }

    // Add this new method for paintings
    private void drawPaintings(Graphics2D g2d, int width, int height) {
        // First painting (abstract)
        int paintingWidth = width / 6;
        int paintingHeight = height / 5;
        int y = height / 4;

        // Frame
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(50, y, paintingWidth + 10, paintingHeight + 10);
        
        // Canvas
        GradientPaint abstractPaint = new GradientPaint(
            50, y, new Color(230, 190, 255),
            50 + paintingWidth, y + paintingHeight, new Color(190, 230, 255)
        );
        g2d.setPaint(abstractPaint);
        g2d.fillRect(55, y + 5, paintingWidth, paintingHeight);
        
        // Abstract design
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.fillOval(60, y + 10, paintingWidth - 10, paintingWidth - 10);
        g2d.setColor(new Color(190, 150, 255, 150));
        g2d.fillArc(70, y + 20, paintingWidth - 30, paintingHeight - 30, 0, 270);

        // Second painting (landscape)
        int x2 = width - paintingWidth - 60;
        
        // Frame
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x2, y, paintingWidth + 10, paintingHeight + 10);
        
        // Sky
        GradientPaint skyPaint = new GradientPaint(
            x2, y, new Color(135, 206, 235),
            x2, y + paintingHeight/2, new Color(255, 255, 255)
        );
        g2d.setPaint(skyPaint);
        g2d.fillRect(x2 + 5, y + 5, paintingWidth, paintingHeight/2);
        
        // Ground
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(x2 + 5, y + 5 + paintingHeight/2, paintingWidth, paintingHeight/2);
        
        // Sun
        g2d.setColor(new Color(255, 215, 0, 180));
        g2d.fillOval(x2 + paintingWidth - 30, y + 15, 25, 25);
        
        // Small house
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x2 + 20, y + paintingHeight - 40, 30, 20);
        g2d.setColor(new Color(165, 42, 42));
        int[] roofX = {x2 + 15, x2 + 35, x2 + 55};
        int[] roofY = {y + paintingHeight - 40, y + paintingHeight - 60, y + paintingHeight - 40};
        g2d.fillPolygon(roofX, roofY, 3);
    }

    private void createAmbientLight(Graphics2D g2d, int width, int height) {
        // Main ambient light
        RadialGradientPaint mainLight = new RadialGradientPaint(
            new Point(width/2, height/3),
            height * 1.2f,
            new float[]{0.0f, 0.3f, 0.7f, 1.0f},
            new Color[]{
                new Color(255, 255, 240, 30),
                new Color(255, 250, 240, 20),
                new Color(255, 245, 240, 10),
                new Color(255, 245, 240, 0)
            }
        );
        g2d.setPaint(mainLight);
        g2d.fillRect(0, 0, width, height);

        // Secondary light sources
        RadialGradientPaint cornerLight = new RadialGradientPaint(
            new Point(0, 0),
            height/2f,
            new float[]{0.0f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 15),
                new Color(255, 255, 255, 0)
            }
        );
        g2d.setPaint(cornerLight);
        g2d.fillRect(0, 0, width/2, height/2);
    }

    private void drawFloatingParticles(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 40));
        long currentTime = System.currentTimeMillis() / 50;
        
        for (int i = 0; i < 30; i++) {
            double angle = (currentTime + i * 50) / 1000.0;
            int x = (int)(width/2 + Math.sin(angle) * width/4);
            int y = (int)(height/2 + Math.cos(angle * 0.7) * height/4);
            int size = (int)(2 + Math.sin(angle * 2) * 2);
            
            g2d.fillOval(x, y, size, size);
        }
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

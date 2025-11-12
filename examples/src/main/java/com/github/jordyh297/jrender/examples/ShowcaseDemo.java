package com.github.jordyh297.jrender.examples;

import com.github.jordyh297.jrender.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SHOWCASE DEMO - The most impressive visual demonstration
 * Features:
 * - Solar system simulation with orbiting planets
 * - Particle effects
 * - Dynamic lighting
 * - Smooth camera transitions
 * - Post-processing effects
 * - Performance metrics
 */
public class ShowcaseDemo extends JPanel implements Runnable {
    private Renderer3D renderer;
    private Camera camera;
    private List<CelestialBody> bodies;
    private List<Particle> particles;
    private Thread renderThread;
    private boolean running = false;
    
    private double time = 0;
    private int frameCount = 0;
    private long lastFpsTime = System.currentTimeMillis();
    private int fps = 0;
    
    // Visual effects
    private boolean glowEffect = true;
    private boolean trailEffect = true;
    private double cameraAngle = 0;
    private double cameraHeight = 15;
    private double cameraDistance = 25;
    
    private boolean isPaused = false;
    private boolean mouseDragging = false;
    private int lastMouseX, lastMouseY;
    private double manualCameraAngle = 0;
    private double manualCameraHeight = 15;
    
    private int culledObjects = 0;
    
    private List<TrailPoint> trails = new ArrayList<>();
    private static final int MAX_TRAIL_POINTS = 200;
    
    // Scene modes
    private enum SceneMode {
        SOLAR_SYSTEM,
        PARTICLE_VORTEX,
        GEOMETRIC_WAVE,
        SPINNING_GALAXY,
        CUBE_MATRIX
    }
    private SceneMode currentMode = SceneMode.SOLAR_SYSTEM;
    
    public ShowcaseDemo() {
        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Initialize renderer with high quality settings
        renderer = new Renderer3D(1600, 900);
        camera = new Camera(new Vector3D(0, 15, -25), new Vector3D(0, 0, 0));
        renderer.setCamera(camera);
        
        // Setup dramatic lighting
        setupLighting();
        
        // Initialize scene
        bodies = new ArrayList<>();
        particles = new ArrayList<>();
        trails = new ArrayList<>();
        createSolarSystem();
        
        // Setup controls
        setupControls();
        
        // Component listener for resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w > 0 && h > 0) {
                    renderer.resize(w, h);
                }
            }
        });
    }
    
    private void setupLighting() {
        renderer.clearLights();
        // Ambient
        renderer.addLight(Light.createAmbient(new Color(20, 20, 40), 0.15));
        // Sun light (golden)
        renderer.addLight(Light.createPoint(
            new Vector3D(0, 0, 0), new Color(255, 220, 150), 3.0));
        // Rim light (blue)
        renderer.addLight(Light.createDirectional(
            new Vector3D(1, -0.5, 1), new Color(100, 150, 255)));
    }
    
    private void createSolarSystem() {
        bodies.clear();
        
        // Sun (center)
        CelestialBody sun = new CelestialBody(
            Mesh.createCube(3.0), 
            new Vector3D(0, 0, 0),
            new Color(255, 200, 50),
            0, 0, 0.2
        );
        bodies.add(sun);
        
        // Planets with different orbits
        bodies.add(new CelestialBody(
            Mesh.createCube(1.2), new Vector3D(0, 0, 0),
            new Color(100, 100, 255), 6, 1.0, 0.5
        ));
        
        bodies.add(new CelestialBody(
            Mesh.createPyramid(1.5), new Vector3D(0, 0, 0),
            new Color(255, 100, 100), 10, 0.7, 0.3
        ));
        
        bodies.add(new CelestialBody(
            Mesh.createCube(0.8), new Vector3D(0, 0, 0),
            new Color(100, 255, 100), 14, 0.5, 0.8
        ));
        
        bodies.add(new CelestialBody(
            Mesh.createPyramid(2.0), new Vector3D(0, 0, 0),
            new Color(255, 150, 255), 18, 0.4, 0.4
        ));
        
        bodies.add(new CelestialBody(
            Mesh.createCube(1.0), new Vector3D(0, 0, 0),
            new Color(255, 255, 100), 22, 0.3, 0.6
        ));
    }
    
    private void createParticleStorm() {
        bodies.clear();
        particles.clear();
        Random rand = new Random();
        
        // Create swirling particles in a vortex pattern
        for (int i = 0; i < 80; i++) {
            double angle = (i / 80.0) * Math.PI * 4;
            double radius = (i / 80.0) * 15;
            Mesh particle = Mesh.createCube(0.4);
            Vector3D pos = new Vector3D(
                Math.cos(angle) * radius,
                (i / 80.0) * 15 - 7.5,
                Math.sin(angle) * radius
            );
            Color color = new Color(
                100 + (int)((i / 80.0) * 155),
                150,
                255 - (int)((i / 80.0) * 155)
            );
            particles.add(new Particle(particle, pos, color));
        }
    }
    
    private void createGeometricDance() {
        bodies.clear();
        particles.clear();
        
        // Create a wave pattern of cubes
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Mesh cube = Mesh.createCube(0.6);
                Color color = new Color(
                    Math.abs(x) * 20 + 50,
                    Math.abs(z) * 20 + 50,
                    200
                );
                bodies.add(new CelestialBody(
                    cube, new Vector3D(x * 2.5, 0, z * 2.5),
                    color, 0, 0, 0.5 + (x + z) * 0.05
                ));
            }
        }
    }
    
    private void createSpinningGalaxy() {
        bodies.clear();
        particles.clear();
        Random rand = new Random();
        
        // Central star
        CelestialBody center = new CelestialBody(
            Mesh.createCube(2.5), 
            new Vector3D(0, 0, 0),
            new Color(255, 220, 100),
            0, 0, 0.3
        );
        bodies.add(center);
        
        // Spiral arms
        for (int arm = 0; arm < 3; arm++) {
            double armAngle = (arm / 3.0) * Math.PI * 2;
            for (int i = 0; i < 15; i++) {
                double dist = 5 + i * 1.2;
                double angle = armAngle + (i * 0.3);
                
                Mesh star = Mesh.createCube(0.5 + rand.nextDouble() * 0.3);
                Vector3D pos = new Vector3D(
                    Math.cos(angle) * dist,
                    (rand.nextDouble() - 0.5) * 2,
                    Math.sin(angle) * dist
                );
                Color color = new Color(
                    200 + rand.nextInt(55),
                    150 + rand.nextInt(105),
                    100 + rand.nextInt(55)
                );
                bodies.add(new CelestialBody(star, pos, color, 0, 0, 0.5 + rand.nextDouble()));
            }
        }
    }
    
    private void createCubeMatrix() {
        bodies.clear();
        particles.clear();
        
        // Create a 3D matrix of cubes
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    // Skip some cubes for interesting pattern
                    if ((x + y + z) % 2 == 0) continue;
                    
                    Mesh cube = Mesh.createCube(0.5);
                    Color color = new Color(
                        Math.abs(x) * 30 + 50,
                        Math.abs(y) * 30 + 50,
                        Math.abs(z) * 30 + 50
                    );
                    bodies.add(new CelestialBody(
                        cube, 
                        new Vector3D(x * 3, y * 3, z * 3),
                        color, 0, 0, 0.3 + (x + y + z) * 0.05
                    ));
                }
            }
        }
    }
    
    private void setupControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        currentMode = SceneMode.SOLAR_SYSTEM;
                        time = 0;
                        createSolarSystem();
                        break;
                    case KeyEvent.VK_2:
                        currentMode = SceneMode.PARTICLE_VORTEX;
                        time = 0;
                        createParticleStorm();
                        break;
                    case KeyEvent.VK_3:
                        currentMode = SceneMode.GEOMETRIC_WAVE;
                        time = 0;
                        createGeometricDance();
                        break;
                    case KeyEvent.VK_4:
                        currentMode = SceneMode.SPINNING_GALAXY;
                        time = 0;
                        createSpinningGalaxy();
                        break;
                    case KeyEvent.VK_5:
                        currentMode = SceneMode.CUBE_MATRIX;
                        time = 0;
                        createCubeMatrix();
                        break;
                    case KeyEvent.VK_F:
                        renderer.setWireframeMode(!renderer.wireframeMode);
                        break;
                    case KeyEvent.VK_G:
                        glowEffect = !glowEffect;
                        break;
                    case KeyEvent.VK_UP:
                        cameraHeight += 2;
                        break;
                    case KeyEvent.VK_DOWN:
                        cameraHeight -= 2;
                        break;
                    case KeyEvent.VK_LEFT:
                        cameraDistance += 2;
                        break;
                    case KeyEvent.VK_RIGHT:
                        cameraDistance -= 2;
                        break;
                    case KeyEvent.VK_SPACE:
                        isPaused = !isPaused;
                        break;
                    case KeyEvent.VK_R:
                        time = 0;
                        trails.clear();
                        break;
                    case KeyEvent.VK_T:
                        trailEffect = !trailEffect;
                        if (!trailEffect) trails.clear();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
            }
        });
        
        // Mouse controls - drag to rotate camera
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDragging = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDragging = false;
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isPaused = !isPaused;
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseDragging) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;
                    
                    manualCameraAngle += dx * 0.01;
                    manualCameraHeight -= dy * 0.1;
                    manualCameraHeight = Math.max(-50, Math.min(50, manualCameraHeight));
                    
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!mouseDragging) {
                    double centerX = getWidth() / 2.0;
                    cameraAngle = (e.getX() - centerX) / centerX * Math.PI / 4;
                }
            }
        });
        
        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            cameraDistance += e.getWheelRotation() * 2;
            cameraDistance = Math.max(5, Math.min(100, cameraDistance));
        });
    }
    
    public void start() {
        if (!running) {
            running = true;
            renderThread = new Thread(this);
            renderThread.start();
        }
    }
    
    public void stop() {
        running = false;
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1_000_000_000.0 / 60.0;
        double delta = 0;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;
            
            if (delta >= 1) {
                update(delta * (1.0 / 60.0));
                repaint();
                delta--;
                
                // FPS calculation
                frameCount++;
                if (System.currentTimeMillis() - lastFpsTime >= 1000) {
                    fps = frameCount;
                    frameCount = 0;
                    lastFpsTime = System.currentTimeMillis();
                }
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void update(double deltaTime) {
        if (!isPaused) {
            time += deltaTime;
        }
        
        // Smooth camera movement
        double targetAngle = mouseDragging ? manualCameraAngle : cameraAngle + time * 0.1;
        double targetHeight = mouseDragging ? manualCameraHeight : cameraHeight + Math.sin(time * 0.5) * 2;
        
        camera.position = new Vector3D(
            Math.cos(targetAngle) * cameraDistance,
            targetHeight,
            Math.sin(targetAngle) * cameraDistance
        );
        camera.target = new Vector3D(0, 0, 0);
        
        // Update scene based on mode
        if (!isPaused) {
            switch (currentMode) {
                case SOLAR_SYSTEM:
                    updateSolarSystem();
                    break;
                case PARTICLE_VORTEX:
                    updateParticles();
                    break;
                case GEOMETRIC_WAVE:
                    updateGeometricWave();
                    break;
                case SPINNING_GALAXY:
                    updateSpinningGalaxy();
                    break;
                case CUBE_MATRIX:
                    updateCubeMatrix();
                    break;
            }
            
            // Update trails
            if (trailEffect) {
                updateTrails();
            }
        }
    }
    
    private void updateSolarSystem() {
        for (CelestialBody body : bodies) {
            body.update(time);
        }
    }
    
    private void updateParticles() {
        for (Particle p : particles) {
            p.update(time);
        }
    }
    
    private void updateGeometricWave() {
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);
            body.update(time);
            // Create wave effect
            double dist = Math.sqrt(
                body.mesh.position.x * body.mesh.position.x + 
                body.mesh.position.z * body.mesh.position.z
            );
            body.mesh.position.y = Math.sin(time * 2 + dist * 0.3) * 3;
        }
    }
    
    private void updateSpinningGalaxy() {
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);
            if (i == 0) {
                // Central star just rotates
                body.mesh.rotation.y = time * 0.3;
                body.mesh.rotation.x = time * 0.2;
            } else {
                // Spiral arms rotate around center
                double currentAngle = Math.atan2(body.basePosition.z, body.basePosition.x);
                double radius = Math.sqrt(
                    body.basePosition.x * body.basePosition.x + 
                    body.basePosition.z * body.basePosition.z
                );
                double newAngle = currentAngle + time * 0.2;
                body.mesh.position = new Vector3D(
                    Math.cos(newAngle) * radius,
                    body.basePosition.y + Math.sin(time * 3 + radius * 0.5) * 0.5,
                    Math.sin(newAngle) * radius
                );
                body.mesh.rotation.y = time * body.rotationSpeed;
            }
        }
    }
    
    private void updateCubeMatrix() {
        for (int i = 0; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);
            // Pulsing effect
            double pulse = Math.sin(time * 2 + i * 0.1);
            body.mesh.scale = new Vector3D(
                1.0 + pulse * 0.3,
                1.0 + pulse * 0.3,
                1.0 + pulse * 0.3
            );
            body.mesh.rotation.y = time * body.rotationSpeed;
            body.mesh.rotation.x = time * body.rotationSpeed * 0.7;
        }
    }
    
    private void updateTrails() {
        // Add trail points from moving objects
        if (currentMode == SceneMode.SOLAR_SYSTEM || currentMode == SceneMode.SPINNING_GALAXY) {
            for (int i = 1; i < Math.min(bodies.size(), 4); i++) {
                CelestialBody body = bodies.get(i);
                trails.add(new TrailPoint(
                    new Vector3D(body.mesh.position.x, body.mesh.position.y, body.mesh.position.z),
                    body.color,
                    1.0f
                ));
            }
        } else if (currentMode == SceneMode.PARTICLE_VORTEX) {
            for (int i = 0; i < Math.min(particles.size(), 10); i += 2) {
                Particle p = particles.get(i);
                trails.add(new TrailPoint(
                    new Vector3D(p.mesh.position.x, p.mesh.position.y, p.mesh.position.z),
                    p.color,
                    1.0f
                ));
            }
        }
        
        // Update and remove old trails
        trails.removeIf(trail -> {
            trail.age += 0.02f;
            trail.alpha = Math.max(0, 1.0f - trail.age);
            return trail.age >= 1.0f;
        });
        
        // Limit trail count for performance
        while (trails.size() > MAX_TRAIL_POINTS) {
            trails.remove(0);
        }
    }
    
    private boolean isInFrustum(Vector3D position) {
        // Simple frustum culling - check if object is roughly in view
        Vector3D toObject = position.subtract(camera.position);
        Vector3D cameraForward = camera.target.subtract(camera.position).normalize();
        
        double dot = toObject.normalize().dot(cameraForward);
        double distance = toObject.magnitude();
        
        // Cull if behind camera or too far
        return dot > -0.5 && distance < 150;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Render scene
        renderer.clear(new Color(5, 5, 15));
        
        // Reset culling counter
        culledObjects = 0;
        
        // Render bodies with frustum culling
        for (CelestialBody body : bodies) {
            if (isInFrustum(body.mesh.position)) {
                renderer.render(body.mesh);
            } else {
                culledObjects++;
            }
        }
        
        // Render particles with frustum culling
        for (Particle p : particles) {
            if (isInFrustum(p.mesh.position)) {
                renderer.render(p.mesh);
            } else {
                culledObjects++;
            }
        }
        
        // Draw buffer
        g2d.drawImage(renderer.getBuffer(), 0, 0, null);
        
        // Draw particle trails
        if (trailEffect && !trails.isEmpty()) {
            drawTrails(g2d);
        }
        
        // Apply glow effect
        if (glowEffect) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.drawImage(renderer.getBuffer(), 2, 2, null);
            g2d.drawImage(renderer.getBuffer(), -2, -2, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw stunning UI
        drawUI(g2d);
    }
    
    private void drawTrails(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 1; i < trails.size(); i++) {
            TrailPoint prev = trails.get(i - 1);
            TrailPoint curr = trails.get(i);
            
            // Project 3D positions to 2D screen space
            Vector3D screenPrev = projectToScreen(prev.position);
            Vector3D screenCurr = projectToScreen(curr.position);
            
            if (screenPrev != null && screenCurr != null) {
                float avgAlpha = (prev.alpha + curr.alpha) / 2.0f;
                Color trailColor = new Color(
                    curr.color.getRed() / 255f,
                    curr.color.getGreen() / 255f,
                    curr.color.getBlue() / 255f,
                    avgAlpha * 0.6f
                );
                
                g2d.setColor(trailColor);
                g2d.setStroke(new BasicStroke(3.0f * avgAlpha));
                g2d.drawLine(
                    (int)screenPrev.x, (int)screenPrev.y,
                    (int)screenCurr.x, (int)screenCurr.y
                );
            }
        }
    }
    
    private Vector3D projectToScreen(Vector3D worldPos) {
        // Simple projection (mimics what renderer does)
        Vector3D relative = worldPos.subtract(camera.position);
        Vector3D forward = camera.target.subtract(camera.position).normalize();
        
        double distance = relative.dot(forward);
        if (distance < 1) return null;
        
        // Perspective projection
        double fov = Math.PI / 3;
        double scale = (getHeight() / 2.0) / Math.tan(fov / 2);
        
        double x = getWidth() / 2.0 + (relative.x / distance) * scale;
        double y = getHeight() / 2.0 - (relative.y / distance) * scale;
        
        return new Vector3D(x, y, distance);
    }
    
    private void drawUI(Graphics2D g2d) {
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Title background bar
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(30, 30, 650, 80, 15, 15);
        g2d.setColor(new Color(100, 150, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(30, 30, 650, 80, 15, 15);
        
        // Title with multiple shadow layers for depth
        Font titleFont = new Font("Arial", Font.BOLD, 52);
        g2d.setFont(titleFont);
        
        String title = "JAVA 3D ENGINE";
        
        // Deep shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(title, 55, 77);
        
        // Mid shadow
        g2d.setColor(new Color(50, 100, 200, 120));
        g2d.drawString(title, 53, 75);
        
        // Main text with gradient effect
        g2d.setColor(new Color(120, 180, 255));
        g2d.drawString(title, 50, 72);
        
        // Highlight
        g2d.setColor(new Color(200, 230, 255));
        g2d.drawString(title, 51, 71);
        
        // Subtitle with backdrop
        Font subtitleFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(subtitleFont);
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("Pure Java", 52, 98);
        g2d.setColor(new Color(255, 200, 100));
        g2d.drawString("Pure Java", 50, 96);
        
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawString(" | ", 145, 96);
        
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("No Dependencies", 177, 98);
        g2d.setColor(new Color(100, 255, 150));
        g2d.drawString("No Dependencies", 175, 96);
        
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawString(" | ", 350, 96);
        
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawString("60 FPS Real-time", 382, 98);
        g2d.setColor(new Color(100, 200, 255));
        g2d.drawString("60 FPS Real-time", 380, 96);
        
        // Stats panel
        int statsX = getWidth() - 300;
        int statsY = 20;
        
        // Semi-transparent background with gradient effect
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(statsX - 15, statsY - 10, 290, 450, 20, 20);
        
        // Stylish border
        g2d.setColor(new Color(100, 150, 255, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(statsX - 15, statsY - 10, 290, 450, 20, 20);
        
        // Stats
        Font statsFont = new Font("Monospaced", Font.BOLD, 15);
        g2d.setFont(statsFont);
        g2d.setStroke(new BasicStroke(1));
        
        int y = statsY + 25;
        int lineHeight = 22;
        
        // Performance header
        g2d.setColor(new Color(255, 200, 100));
        g2d.drawString("=== PERFORMANCE ===", statsX, y);
        y += lineHeight + 5;
        
        // FPS with color coding and visual bar
        g2d.setColor(fps > 55 ? new Color(100, 255, 100) : fps > 40 ? Color.YELLOW : Color.RED);
        g2d.drawString(String.format("FPS: %d", fps), statsX, y);
        
        // FPS bar
        int barWidth = (int) (fps * 2.5);
        g2d.fillRect(statsX + 80, y - 10, Math.min(barWidth, 150), 12);
        y += lineHeight;
        
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString(String.format("Objects: %d", bodies.size() + particles.size()), statsX, y);
        y += lineHeight;
        
        g2d.setColor(new Color(100, 255, 150));
        g2d.drawString(String.format("Culled: %d", culledObjects), statsX, y);
        y += lineHeight;
        
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString(String.format("Triangles: %d", 
            (bodies.size() + particles.size()) * 12), statsX, y);
        y += lineHeight;
        
        if (trailEffect) {
            g2d.setColor(new Color(255, 150, 255));
            g2d.drawString(String.format("Trails: %d", trails.size()), statsX, y);
            y += lineHeight;
        }
        
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawString(String.format("Resolution: %dx%d", getWidth(), getHeight()), statsX, y);
        y += lineHeight + 10;
        
        // Paused indicator
        if (isPaused) {
            g2d.setColor(new Color(255, 200, 100));
            g2d.drawString("=== PAUSED ===", statsX, y);
            y += lineHeight + 5;
        }
        
        // Scene mode
        g2d.setColor(new Color(255, 200, 100));
        g2d.drawString("=== SCENE MODE ===", statsX, y);
        y += lineHeight + 5;
        
        g2d.setColor(new Color(150, 220, 255));
        Font sceneFont = new Font("Arial", Font.BOLD, 13);
        g2d.setFont(sceneFont);
        g2d.drawString(currentMode.toString().replace("_", " "), statsX, y);
        y += lineHeight + 10;
        
        // Controls header
        g2d.setColor(new Color(255, 200, 100));
        Font headerFont = new Font("Monospaced", Font.BOLD, 15);
        g2d.setFont(headerFont);
        g2d.drawString("=== CONTROLS ===", statsX, y);
        y += lineHeight;
        
        // Controls
        Font controlFont = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(controlFont);
        
        String[][] controls = {
            {"1-5", "Switch scenes"},
            {"Mouse", "Drag to rotate"},
            {"Wheel", "Zoom in/out"},
            {"Click", "Pause/Resume"},
            {"Arrows", "Camera adjust"},
            {"F", "Wireframe"},
            {"G", "Glow effect"},
            {"T", "Trails"},
            {"R", "Reset time"},
            {"ESC", "Exit"}
        };
        
        for (String[] control : controls) {
            // Key background
            g2d.setColor(new Color(50, 50, 80));
            int keyWidth = g2d.getFontMetrics().stringWidth(control[0]) + 8;
            g2d.fillRoundRect(statsX, y - 12, keyWidth, 16, 5, 5);
            
            // Key text
            g2d.setColor(new Color(255, 255, 100));
            g2d.drawString(control[0], statsX + 4, y);
            
            // Description
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawString(control[1], statsX + keyWidth + 10, y);
            
            y += lineHeight - 4;
        }
        
        // Feature badges
        drawFeatureBadges(g2d);
    }
    
    private void drawFeatureBadges(Graphics2D g2d) {
        int badgeY = getHeight() - 55;
        int badgeX = 30;
        
        // Define features with their colors
        class FeatureBadge {
            String check, text;
            Color color;
            FeatureBadge(String c, String t, Color col) { check = c; text = t; color = col; }
        }
        
        FeatureBadge[] features = {
            new FeatureBadge("[OK]", "Real-time 3D", new Color(100, 200, 255)),
            new FeatureBadge("[OK]", "Dynamic Lighting", new Color(255, 200, 100)),
            new FeatureBadge("[OK]", "Frustum Culling", new Color(150, 255, 100)),
            new FeatureBadge("[OK]", "Pure Java", new Color(255, 150, 100)),
            new FeatureBadge("[OK]", "60 FPS", new Color(100, 255, 200)),
            new FeatureBadge("[OK]", "Interactive", new Color(255, 100, 200))
        };
        
        Font badgeFont = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(badgeFont);
        
        for (FeatureBadge feature : features) {
            int width = g2d.getFontMetrics().stringWidth(feature.check + " " + feature.text) + 24;
            
            // Badge shadow
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRoundRect(badgeX + 2, badgeY + 2, width, 28, 14, 14);
            
            // Badge background with gradient effect
            g2d.setColor(new Color(30, 30, 40, 220));
            g2d.fillRoundRect(badgeX, badgeY, width, 28, 14, 14);
            
            // Badge border with accent color
            g2d.setColor(new Color(feature.color.getRed(), feature.color.getGreen(), 
                feature.color.getBlue(), 200));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(badgeX, badgeY, width, 28, 14, 14);
            
            // Check mark in accent color
            g2d.setColor(feature.color);
            g2d.drawString(feature.check, badgeX + 10, badgeY + 19);
            
            // Badge text
            g2d.setColor(Color.WHITE);
            g2d.drawString(feature.text, badgeX + g2d.getFontMetrics().stringWidth(feature.check) + 14, badgeY + 19);
            
            badgeX += width + 10;
        }
    }
    
    // Helper classes
    private static class CelestialBody {
        Mesh mesh;
        Vector3D basePosition;
        Color color;
        double orbitRadius;
        double orbitSpeed;
        double rotationSpeed;
        
        CelestialBody(Mesh mesh, Vector3D pos, Color color, 
                     double radius, double orbitSpeed, double rotSpeed) {
            this.mesh = mesh;
            this.basePosition = pos;
            this.color = color;
            this.orbitRadius = radius;
            this.orbitSpeed = orbitSpeed;
            this.rotationSpeed = rotSpeed;
            
            // Apply color to mesh
            for (Triangle tri : mesh.triangles) {
                tri.color = color;
            }
        }
        
        void update(double time) {
            if (orbitRadius > 0) {
                mesh.position = new Vector3D(
                    basePosition.x + Math.cos(time * orbitSpeed) * orbitRadius,
                    basePosition.y + Math.sin(time * orbitSpeed * 0.5) * 2,
                    basePosition.z + Math.sin(time * orbitSpeed) * orbitRadius
                );
            } else {
                mesh.position = basePosition;
            }
            
            mesh.rotation.y = time * rotationSpeed;
            mesh.rotation.x = time * rotationSpeed * 0.7;
        }
    }
    
    private static class Particle {
        Mesh mesh;
        Vector3D basePosition;
        Color color;
        double phase;
        
        Particle(Mesh mesh, Vector3D pos, Color color) {
            this.mesh = mesh;
            this.basePosition = pos;
            this.color = color;
            this.phase = Math.random() * Math.PI * 2;
            
            for (Triangle tri : mesh.triangles) {
                tri.color = color;
            }
        }
        
        void update(double time) {
            mesh.position = new Vector3D(
                basePosition.x + Math.sin(time + phase) * 5,
                basePosition.y + Math.cos(time * 0.7 + phase) * 5,
                basePosition.z + Math.sin(time * 1.3 + phase) * 5
            );
            mesh.rotation.y = time * 2;
            mesh.rotation.x = time * 1.5;
        }
    }
    
    private static class TrailPoint {
        Vector3D position;
        Color color;
        float alpha;
        float age;
        
        TrailPoint(Vector3D pos, Color color, float alpha) {
            this.position = pos;
            this.color = color;
            this.alpha = alpha;
            this.age = 0;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java 3D Graphics Engine - Showcase Demo");
            ShowcaseDemo demo = new ShowcaseDemo();
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(demo);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            demo.requestFocusInWindow();
            demo.start();
        });
    }
}
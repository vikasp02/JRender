package com.github.jordyh297.jrender.examples;

import com.github.jordyh297.jrender.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive 3D demo application using Swing.
 * Features:
 * - Multiple 3D objects (cube, pyramid)
 * - Interactive camera controls (WASD, mouse drag)
 * - Lighting system
 * - Wireframe toggle
 * - Real-time rendering
 */
public class Demo3D extends JPanel implements Runnable {
    private Renderer3D renderer;
    private Camera camera;
    private List<Mesh> meshes;
    private Thread renderThread;
    private boolean running = false;
    
    // Interaction state
    private Point lastMousePos;
    private boolean isDragging = false;
    private double rotationX = 0;
    private double rotationY = 0;
    
    // Animation
    private double time = 0;
    
    // Settings
    private boolean wireframeMode = false;
    private boolean autoRotate = true;
    
    public Demo3D() {
        setPreferredSize(new Dimension(1200, 800));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // Initialize renderer and camera
        renderer = new Renderer3D(1200, 800);
        camera = new Camera(new Vector3D(0, 2, -8), new Vector3D(0, 0, 0));
        renderer.setCamera(camera);
        
        // Setup lighting
        renderer.addLight(Light.createAmbient(Color.WHITE, 0.3));
        renderer.addLight(Light.createDirectional(
            new Vector3D(-1, -1, -1), Color.WHITE));
        renderer.addLight(Light.createPoint(
            new Vector3D(3, 3, -3), new Color(255, 200, 150), 0.8));
        
        // Create 3D objects
        meshes = new ArrayList<>();
        
        // Rotating cube
        Mesh cube = Mesh.createCube(2.0);
        cube.position = new Vector3D(-3, 0, 0);
        meshes.add(cube);
        
        // Pyramid
        Mesh pyramid = Mesh.createPyramid(2.0);
        pyramid.position = new Vector3D(3, -1, 0);
        meshes.add(pyramid);
        
        // Small cube
        Mesh smallCube = Mesh.createCube(1.0);
        smallCube.position = new Vector3D(0, 3, 2);
        meshes.add(smallCube);
        
        // Setup mouse controls
        setupMouseControls();
        
        // Setup keyboard controls
        setupKeyboardControls();
        
        // Add component listener for resizing
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
    
    private void setupMouseControls() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
                isDragging = true;
                autoRotate = false;
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && lastMousePos != null) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;
                    
                    rotationX -= dy * 0.01;
                    rotationY += dx * 0.01;
                    
                    camera.orbit(dy * 0.01, dx * 0.01);
                    lastMousePos = e.getPoint();
                }
            }
        });
        
        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            double zoom = e.getPreciseWheelRotation();
            camera.moveForward(-zoom * 0.5);
        });
    }
    
    private void setupKeyboardControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                double moveSpeed = 0.2;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        camera.moveForward(moveSpeed);
                        break;
                    case KeyEvent.VK_S:
                        camera.moveForward(-moveSpeed);
                        break;
                    case KeyEvent.VK_A:
                        camera.strafe(-moveSpeed);
                        break;
                    case KeyEvent.VK_D:
                        camera.strafe(moveSpeed);
                        break;
                    case KeyEvent.VK_SPACE:
                        camera.moveVertical(moveSpeed);
                        break;
                    case KeyEvent.VK_SHIFT:
                        camera.moveVertical(-moveSpeed);
                        break;
                    case KeyEvent.VK_F:
                        wireframeMode = !wireframeMode;
                        renderer.setWireframeMode(wireframeMode);
                        break;
                    case KeyEvent.VK_R:
                        autoRotate = !autoRotate;
                        break;
                    case KeyEvent.VK_L:
                        renderer.setUseLighting(!renderer.lights.isEmpty());
                        if (!renderer.lights.isEmpty()) {
                            renderer.clearLights();
                        } else {
                            renderer.addLight(Light.createAmbient(Color.WHITE, 0.3));
                            renderer.addLight(Light.createDirectional(
                                new Vector3D(-1, -1, -1), Color.WHITE));
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
            }
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
        double nsPerFrame = 1_000_000_000.0 / 60.0; // 60 FPS
        double delta = 0;
        
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;
            
            if (delta >= 1) {
                update(delta * (1.0 / 60.0));
                repaint();
                delta--;
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void update(double deltaTime) {
        time += deltaTime;
        
        // Animate meshes
        if (autoRotate) {
            // Rotating cube
            meshes.get(0).rotation.y = time * 0.5;
            meshes.get(0).rotation.x = time * 0.3;
            
            // Spinning pyramid
            meshes.get(1).rotation.y = -time * 0.4;
            
            // Orbiting small cube
            meshes.get(2).position.x = Math.cos(time * 0.5) * 4;
            meshes.get(2).position.z = Math.sin(time * 0.5) * 4;
            meshes.get(2).rotation.y = time * 0.8;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Clear and render scene
        renderer.clear(new Color(20, 20, 30));
        
        for (Mesh mesh : meshes) {
            renderer.render(mesh);
        }
        
        // Draw buffer to screen
        g.drawImage(renderer.getBuffer(), 0, 0, null);
        
        // Draw UI overlay
        drawUI(g);
    }
    
    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        
        int y = 20;
        int lineHeight = 18;
        
        g.drawString("3D Graphics Engine Demo", 10, y);
        y += lineHeight * 2;
        
        g.drawString("Controls:", 10, y);
        y += lineHeight;
        g.drawString("  WASD - Move camera", 10, y);
        y += lineHeight;
        g.drawString("  Space/Shift - Move up/down", 10, y);
        y += lineHeight;
        g.drawString("  Mouse Drag - Rotate camera", 10, y);
        y += lineHeight;
        g.drawString("  Mouse Wheel - Zoom", 10, y);
        y += lineHeight;
        g.drawString("  F - Toggle wireframe: " + (wireframeMode ? "ON" : "OFF"), 10, y);
        y += lineHeight;
        g.drawString("  R - Toggle auto-rotate: " + (autoRotate ? "ON" : "OFF"), 10, y);
        y += lineHeight;
        g.drawString("  L - Toggle lighting", 10, y);
        y += lineHeight;
        g.drawString("  ESC - Exit", 10, y);
        
        // FPS counter
        y += lineHeight * 2;
        g.drawString("Camera: " + String.format("(%.1f, %.1f, %.1f)", 
            camera.position.x, camera.position.y, camera.position.z), 10, y);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java 3D Graphics Engine");
            Demo3D demo = new Demo3D();
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(demo);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            demo.start();
        });
    }
}
package com.github.jordyh297.jrender.examples;

import com.github.jordyh297.jrender.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Advanced example showing multiple cameras and rendering techniques.
 * Demonstrates how to create more complex 3D applications.
 */
public class AdvancedExample extends JFrame {
    private Renderer3D renderer;
    private Camera camera;
    private Mesh[] meshes;
    private JPanel renderPanel;
    private JPanel controlPanel;
    private double time = 0;
    
    public AdvancedExample() {
        super("Advanced 3D Example - Multi-Object Scene");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create render panel
        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                render(g);
            }
        };
        renderPanel.setPreferredSize(new Dimension(1000, 700));
        renderPanel.setBackground(Color.BLACK);
        
        // Initialize renderer
        renderer = new Renderer3D(1000, 700);
        camera = new Camera(new Vector3D(0, 3, -10), new Vector3D(0, 0, 0));
        renderer.setCamera(camera);
        
        // Setup lighting
        renderer.addLight(Light.createAmbient(Color.WHITE, 0.2));
        renderer.addLight(Light.createDirectional(
            new Vector3D(-1, -1, -1), new Color(255, 255, 200)));
        renderer.addLight(Light.createPoint(
            new Vector3D(0, 5, 0), new Color(100, 150, 255), 1.5));
        
        // Create multiple objects
        createScene();
        
        // Create control panel
        createControlPanel();
        
        // Add panels
        add(renderPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        // Animation timer
        Timer timer = new Timer(16, e -> {
            time += 0.016;
            updateScene();
            renderPanel.repaint();
        });
        timer.start();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void createScene() {
        meshes = new Mesh[5];
        
        // Central rotating cube
        meshes[0] = Mesh.createCube(2.0);
        meshes[0].position = new Vector3D(0, 0, 0);
        
        // Orbiting pyramids
        meshes[1] = Mesh.createPyramid(1.5);
        meshes[2] = Mesh.createPyramid(1.5);
        
        // Smaller cubes
        meshes[3] = Mesh.createCube(0.8);
        meshes[3].position = new Vector3D(-4, 1, 0);
        
        meshes[4] = Mesh.createCube(0.8);
        meshes[4].position = new Vector3D(4, 1, 0);
    }
    
    private void updateScene() {
        // Rotate central cube
        meshes[0].rotation.y = time * 0.5;
        meshes[0].rotation.x = time * 0.3;
        
        // Orbit pyramids around center
        double radius = 5.0;
        meshes[1].position.x = Math.cos(time) * radius;
        meshes[1].position.z = Math.sin(time) * radius;
        meshes[1].position.y = Math.sin(time * 2) * 1.5;
        meshes[1].rotation.y = -time * 0.8;
        
        meshes[2].position.x = Math.cos(time + Math.PI) * radius;
        meshes[2].position.z = Math.sin(time + Math.PI) * radius;
        meshes[2].position.y = Math.sin(time * 2 + Math.PI) * 1.5;
        meshes[2].rotation.y = time * 0.8;
        
        // Oscillate small cubes
        meshes[3].position.y = 1 + Math.sin(time * 3) * 0.5;
        meshes[3].rotation.z = time * 0.6;
        
        meshes[4].position.y = 1 + Math.cos(time * 3) * 0.5;
        meshes[4].rotation.z = -time * 0.6;
    }
    
    private void render(Graphics g) {
        renderer.clear(new Color(10, 10, 20));
        
        for (Mesh mesh : meshes) {
            renderer.render(mesh);
        }
        
        g.drawImage(renderer.getBuffer(), 0, 0, null);
        
        // Draw info overlay
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("FPS: ~60", 10, 20);
        g.drawString("Objects: " + meshes.length, 10, 35);
        g.drawString("Camera: " + String.format("(%.1f, %.1f, %.1f)", 
            camera.position.x, camera.position.y, camera.position.z), 10, 50);
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(200, 700));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel title = new JLabel("Controls");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(title);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Wireframe toggle
        JCheckBox wireframeCheck = new JCheckBox("Wireframe Mode");
        wireframeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        wireframeCheck.addActionListener(e -> 
            renderer.setWireframeMode(wireframeCheck.isSelected()));
        controlPanel.add(wireframeCheck);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Backface culling toggle
        JCheckBox cullingCheck = new JCheckBox("Backface Culling", true);
        cullingCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        cullingCheck.addActionListener(e -> 
            renderer.setBackfaceCulling(cullingCheck.isSelected()));
        controlPanel.add(cullingCheck);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Lighting toggle
        JCheckBox lightingCheck = new JCheckBox("Lighting", true);
        lightingCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        lightingCheck.addActionListener(e -> 
            renderer.setUseLighting(lightingCheck.isSelected()));
        controlPanel.add(lightingCheck);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Camera controls
        JLabel cameraLabel = new JLabel("Camera Distance");
        cameraLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(cameraLabel);
        
        JSlider distanceSlider = new JSlider(5, 20, 10);
        distanceSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        distanceSlider.addChangeListener(e -> {
            double dist = distanceSlider.getValue();
            camera.position = new Vector3D(0, 3, -dist);
        });
        controlPanel.add(distanceSlider);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // FOV control
        JLabel fovLabel = new JLabel("Field of View");
        fovLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(fovLabel);
        
        JSlider fovSlider = new JSlider(30, 120, 60);
        fovSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        fovSlider.addChangeListener(e -> {
            camera.fov = Math.toRadians(fovSlider.getValue());
        });
        controlPanel.add(fovSlider);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Reset button
        JButton resetButton = new JButton("Reset Camera");
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.addActionListener(e -> {
            camera.position = new Vector3D(0, 3, -10);
            camera.target = new Vector3D(0, 0, 0);
            camera.fov = Math.toRadians(60);
            distanceSlider.setValue(10);
            fovSlider.setValue(60);
        });
        controlPanel.add(resetButton);
        
        // Info section
        controlPanel.add(Box.createVerticalStrut(30));
        JLabel infoLabel = new JLabel("<html><b>Info:</b><br>" +
            "This demo shows:<br>" +
            "- Multiple objects<br>" +
            "- Dynamic animation<br>" +
            "- Real-time lighting<br>" +
            "- Interactive controls</html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlPanel.add(infoLabel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdvancedExample app = new AdvancedExample();
            app.setVisible(true);
        });
    }
}
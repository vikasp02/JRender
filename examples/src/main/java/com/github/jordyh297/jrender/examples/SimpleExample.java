package com.github.jordyh297.jrender.examples;

import com.github.jordyh297.jrender.*;
import javax.swing.*;
import java.awt.*;

/**
 * Simple example showing how to use the 3D graphics engine in your own application.
 * This is a minimal Swing application that renders a rotating cube.
 */
public class SimpleExample extends JPanel {
    private Renderer3D renderer;
    private Camera camera;
    private Mesh cube;
    private double rotation = 0;
    
    public SimpleExample() {
        // Set panel size
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        
        // Create renderer
        renderer = new Renderer3D(800, 600);
        
        // Create and position camera
        camera = new Camera(
            new Vector3D(0, 1, -4),  // Camera position
            new Vector3D(0, 0, 0)    // Look at center
        );
        renderer.setCamera(camera);
        
        // Add lighting
        renderer.addLight(Light.createAmbient(Color.WHITE, 0.4));
        renderer.addLight(Light.createDirectional(
            new Vector3D(-1, -1, -1), 
            Color.WHITE
        ));
        
        // Create a cube
        cube = Mesh.createCube(1.5);
        cube.position = new Vector3D(0, 0, 0);
        
        // Start animation timer
        Timer timer = new Timer(16, e -> {
            // Update rotation
            rotation += 0.02;
            cube.rotation.y = rotation;
            cube.rotation.x = rotation * 0.5;
            
            // Repaint
            repaint();
        });
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Clear and render
        renderer.clear(new Color(20, 20, 30));
        renderer.render(cube);
        
        // Draw to screen
        g.drawImage(renderer.getBuffer(), 0, 0, null);
        
        // Draw simple UI
        g.setColor(Color.WHITE);
        g.drawString("Simple 3D Cube Example", 10, 20);
        g.drawString("Rotation: " + String.format("%.2f", rotation), 10, 40);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple 3D Example");
            SimpleExample example = new SimpleExample();
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(example);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
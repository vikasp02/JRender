package com.github.jordyh297.jrender;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D mesh composed of triangles.
 */
public class Mesh {
    public List<Triangle> triangles;
    public Vector3D position;
    public Vector3D rotation;
    public Vector3D scale;
    
    public Mesh() {
        triangles = new ArrayList<>();
        position = new Vector3D(0, 0, 0);
        rotation = new Vector3D(0, 0, 0);
        scale = new Vector3D(1, 1, 1);
    }
    
    public void addTriangle(Triangle triangle) {
        triangles.add(triangle);
    }
    
    // Create a cube mesh
    public static Mesh createCube(double size) {
        Mesh mesh = new Mesh();
        double s = size / 2.0;
        
        Color[] faceColors = {
            new Color(255, 100, 100),  // Front - Red
            new Color(100, 255, 100),  // Back - Green
            new Color(100, 100, 255),  // Top - Blue
            new Color(255, 255, 100),  // Bottom - Yellow
            new Color(255, 100, 255),  // Right - Magenta
            new Color(100, 255, 255)   // Left - Cyan
        };
        
        // Front face
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, -s), new Vertex(s, -s, -s), new Vertex(s, s, -s), faceColors[0]));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, -s), new Vertex(s, s, -s), new Vertex(-s, s, -s), faceColors[0]));
        
        // Back face
        mesh.addTriangle(new Triangle(
            new Vertex(s, -s, s), new Vertex(-s, -s, s), new Vertex(-s, s, s), faceColors[1]));
        mesh.addTriangle(new Triangle(
            new Vertex(s, -s, s), new Vertex(-s, s, s), new Vertex(s, s, s), faceColors[1]));
        
        // Top face
        mesh.addTriangle(new Triangle(
            new Vertex(-s, s, -s), new Vertex(s, s, -s), new Vertex(s, s, s), faceColors[2]));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, s, -s), new Vertex(s, s, s), new Vertex(-s, s, s), faceColors[2]));
        
        // Bottom face
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, s), new Vertex(s, -s, s), new Vertex(s, -s, -s), faceColors[3]));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, s), new Vertex(s, -s, -s), new Vertex(-s, -s, -s), faceColors[3]));
        
        // Right face
        mesh.addTriangle(new Triangle(
            new Vertex(s, -s, -s), new Vertex(s, -s, s), new Vertex(s, s, s), faceColors[4]));
        mesh.addTriangle(new Triangle(
            new Vertex(s, -s, -s), new Vertex(s, s, s), new Vertex(s, s, -s), faceColors[4]));
        
        // Left face
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, s), new Vertex(-s, -s, -s), new Vertex(-s, s, -s), faceColors[5]));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, -s, s), new Vertex(-s, s, -s), new Vertex(-s, s, s), faceColors[5]));
        
        return mesh;
    }
    
    // Create a pyramid mesh
    public static Mesh createPyramid(double size) {
        Mesh mesh = new Mesh();
        double s = size / 2.0;
        double h = size;
        
        Color baseColor = new Color(200, 150, 100);
        Color sideColor1 = new Color(150, 100, 200);
        Color sideColor2 = new Color(100, 200, 150);
        Color sideColor3 = new Color(200, 100, 150);
        Color sideColor4 = new Color(150, 200, 100);
        
        // Base
        mesh.addTriangle(new Triangle(
            new Vertex(-s, 0, -s), new Vertex(s, 0, -s), new Vertex(s, 0, s), baseColor));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, 0, -s), new Vertex(s, 0, s), new Vertex(-s, 0, s), baseColor));
        
        // Sides
        Vertex apex = new Vertex(0, h, 0);
        mesh.addTriangle(new Triangle(
            new Vertex(-s, 0, -s), apex, new Vertex(s, 0, -s), sideColor1));
        mesh.addTriangle(new Triangle(
            new Vertex(s, 0, -s), apex, new Vertex(s, 0, s), sideColor2));
        mesh.addTriangle(new Triangle(
            new Vertex(s, 0, s), apex, new Vertex(-s, 0, s), sideColor3));
        mesh.addTriangle(new Triangle(
            new Vertex(-s, 0, s), apex, new Vertex(-s, 0, -s), sideColor4));
        
        return mesh;
    }
    
    // Get transformation matrix for this mesh
    public Matrix4x4 getTransformMatrix() {
        Matrix4x4 transform = Matrix4x4.identity();
        
        // Scale
        transform = transform.multiply(Matrix4x4.scaling(scale.x, scale.y, scale.z));
        
        // Rotation
        transform = transform.multiply(Matrix4x4.rotationX(rotation.x));
        transform = transform.multiply(Matrix4x4.rotationY(rotation.y));
        transform = transform.multiply(Matrix4x4.rotationZ(rotation.z));
        
        // Translation
        transform = transform.multiply(Matrix4x4.translation(position.x, position.y, position.z));
        
        return transform;
    }
}

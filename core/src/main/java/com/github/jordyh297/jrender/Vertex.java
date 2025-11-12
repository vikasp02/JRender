package com.github.jordyh297.jrender;

import java.awt.Color;

/**
 * Represents a vertex in 3D space with position, normal, and color.
 */
public class Vertex {
    public Vector3D position;
    public Vector3D normal;
    public Color color;
    
    public Vertex(Vector3D position) {
        this.position = position;
        this.normal = new Vector3D(0, 0, 1);
        this.color = Color.WHITE;
    }
    
    public Vertex(Vector3D position, Vector3D normal) {
        this.position = position;
        this.normal = normal;
        this.color = Color.WHITE;
    }
    
    public Vertex(Vector3D position, Vector3D normal, Color color) {
        this.position = position;
        this.normal = normal;
        this.color = color;
    }
    
    public Vertex(double x, double y, double z) {
        this(new Vector3D(x, y, z));
    }
    
    public Vertex copy() {
        return new Vertex(new Vector3D(position), new Vector3D(normal), color);
    }
}

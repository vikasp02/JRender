package com.github.jordyh297.jrender;

import java.awt.Color;

/**
 * Represents a triangle with three vertices.
 */
public class Triangle {
    public Vertex v1, v2, v3;
    public Color color;
    
    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = Color.WHITE;
    }
    
    public Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
    
    // Calculate face normal
    public Vector3D getNormal() {
        Vector3D edge1 = v2.position.subtract(v1.position);
        Vector3D edge2 = v3.position.subtract(v1.position);
        return edge1.cross(edge2).normalize();
    }
    
    // Calculate center point
    public Vector3D getCenter() {
        return new Vector3D(
            (v1.position.x + v2.position.x + v3.position.x) / 3.0,
            (v1.position.y + v2.position.y + v3.position.y) / 3.0,
            (v1.position.z + v2.position.z + v3.position.z) / 3.0
        );
    }
    
    public Triangle copy() {
        return new Triangle(v1.copy(), v2.copy(), v3.copy(), color);
    }
}

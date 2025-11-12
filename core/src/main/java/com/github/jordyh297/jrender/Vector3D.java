package com.github.jordyh297.jrender;

/**
 * Represents a 3D vector with x, y, z components.
 * Provides operations for vector mathematics.
 */
public class Vector3D {
    public double x, y, z;
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D() {
        this(0, 0, 0);
    }
    
    public Vector3D(Vector3D other) {
        this(other.x, other.y, other.z);
    }
    
    // Vector addition
    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }
    
    // Vector subtraction
    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }
    
    // Scalar multiplication
    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
    
    // Dot product
    public double dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    // Cross product
    public Vector3D cross(Vector3D other) {
        return new Vector3D(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    // Vector magnitude
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    // Normalize vector
    public Vector3D normalize() {
        double mag = magnitude();
        if (mag > 0.0001) {
            return new Vector3D(x / mag, y / mag, z / mag);
        }
        return new Vector3D(0, 0, 0);
    }
    
    // Distance to another vector
    public double distance(Vector3D other) {
        return subtract(other).magnitude();
    }
    
    @Override
    public String toString() {
        return String.format("Vector3D(%.2f, %.2f, %.2f)", x, y, z);
    }
}

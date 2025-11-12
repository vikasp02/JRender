package com.github.jordyh297.jrender;

/**
 * Camera for 3D rendering with position, rotation, and projection settings.
 */
public class Camera {
    public Vector3D position;
    public Vector3D target;
    public Vector3D up;
    
    // Projection parameters
    public double fov;          // Field of view in radians
    public double aspectRatio;
    public double nearPlane;
    public double farPlane;
    
    public Camera() {
        position = new Vector3D(0, 0, -5);
        target = new Vector3D(0, 0, 0);
        up = new Vector3D(0, 1, 0);
        
        fov = Math.toRadians(60);
        aspectRatio = 16.0 / 9.0;
        nearPlane = 0.1;
        farPlane = 100.0;
    }
    
    public Camera(Vector3D position, Vector3D target) {
        this();
        this.position = position;
        this.target = target;
    }
    
    // Get view matrix
    public Matrix4x4 getViewMatrix() {
        return Matrix4x4.lookAt(position, target, up);
    }
    
    // Get projection matrix
    public Matrix4x4 getProjectionMatrix() {
        return Matrix4x4.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
    
    // Move camera forward/backward
    public void moveForward(double distance) {
        Vector3D direction = target.subtract(position).normalize();
        position = position.add(direction.multiply(distance));
        target = target.add(direction.multiply(distance));
    }
    
    // Move camera left/right
    public void strafe(double distance) {
        Vector3D direction = target.subtract(position).normalize();
        Vector3D right = direction.cross(up).normalize();
        position = position.add(right.multiply(distance));
        target = target.add(right.multiply(distance));
    }
    
    // Move camera up/down
    public void moveVertical(double distance) {
        position = position.add(up.multiply(distance));
        target = target.add(up.multiply(distance));
    }
    
    // Orbit around target
    public void orbit(double angleX, double angleY) {
        Vector3D direction = position.subtract(target);
        double distance = direction.magnitude();
        
        // Convert to spherical coordinates
        double theta = Math.atan2(direction.x, direction.z) + angleY;
        double phi = Math.acos(direction.y / distance) + angleX;
        
        // Clamp phi to avoid gimbal lock
        phi = Math.max(0.1, Math.min(Math.PI - 0.1, phi));
        
        // Convert back to cartesian
        position = new Vector3D(
            target.x + distance * Math.sin(phi) * Math.sin(theta),
            target.y + distance * Math.cos(phi),
            target.z + distance * Math.sin(phi) * Math.cos(theta)
        );
    }
}

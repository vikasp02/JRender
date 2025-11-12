package com.github.jordyh297.jrender;

import java.awt.Color;

/**
 * Represents a light source in the 3D scene.
 */
public class Light {
    public enum LightType {
        DIRECTIONAL,
        POINT,
        AMBIENT
    }
    
    public LightType type;
    public Vector3D position;      // For point lights
    public Vector3D direction;     // For directional lights
    public Color color;
    public double intensity;
    
    public Light(LightType type, Vector3D position, Color color, double intensity) {
        this.type = type;
        this.position = position;
        this.direction = new Vector3D(0, -1, 0);
        this.color = color;
        this.intensity = intensity;
    }
    
    public Light(LightType type, Vector3D direction, Color color) {
        this.type = type;
        this.position = new Vector3D(0, 0, 0);
        this.direction = direction.normalize();
        this.color = color;
        this.intensity = 1.0;
    }
    
    // Create a directional light
    public static Light createDirectional(Vector3D direction, Color color) {
        return new Light(LightType.DIRECTIONAL, direction, color);
    }
    
    // Create a point light
    public static Light createPoint(Vector3D position, Color color, double intensity) {
        return new Light(LightType.POINT, position, color, intensity);
    }
    
    // Create ambient light
    public static Light createAmbient(Color color, double intensity) {
        Light light = new Light(LightType.AMBIENT, new Vector3D(0, 0, 0), color, intensity);
        return light;
    }
    
    // Calculate lighting for a surface
    public Color calculateLighting(Vector3D surfacePoint, Vector3D normal, Color surfaceColor) {
        if (type == LightType.AMBIENT) {
            return multiplyColors(surfaceColor, color, intensity);
        }
        
        Vector3D lightDir;
        double attenuation = 1.0;
        
        if (type == LightType.DIRECTIONAL) {
            lightDir = direction.multiply(-1).normalize();
        } else { // POINT
            lightDir = position.subtract(surfacePoint);
            double distance = lightDir.magnitude();
            lightDir = lightDir.normalize();
            attenuation = 1.0 / (1.0 + 0.1 * distance + 0.01 * distance * distance);
        }
        
        // Diffuse lighting (Lambertian)
        double diffuse = Math.max(0, normal.dot(lightDir));
        
        double finalIntensity = intensity * diffuse * attenuation;
        return multiplyColors(surfaceColor, color, finalIntensity);
    }
    
    private Color multiplyColors(Color c1, Color c2, double intensity) {
        int r = (int) Math.min(255, c1.getRed() * c2.getRed() / 255.0 * intensity);
        int g = (int) Math.min(255, c1.getGreen() * c2.getGreen() / 255.0 * intensity);
        int b = (int) Math.min(255, c1.getBlue() * c2.getBlue() / 255.0 * intensity);
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }
}

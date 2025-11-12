package com.github.jordyh297.jrender;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 3D Renderer that transforms 3D geometry to 2D screen space and renders it.
 */
public class Renderer3D {
    private int width;
    private int height;
    private BufferedImage buffer;
    private Graphics2D g2d;
    private double[][] zBuffer;
    
    private Camera camera;
    public List<Light> lights;
    
    // Rendering options
    public boolean wireframeMode = false;
    private boolean backfaceCulling = true;
    private boolean useLighting = true;
    
    public Renderer3D(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2d = buffer.createGraphics();
        this.zBuffer = new double[width][height];
        this.lights = new ArrayList<>();
        
        // Setup rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
        if (camera != null) {
            camera.aspectRatio = (double) width / height;
        }
    }
    
    public void addLight(Light light) {
        lights.add(light);
    }
    
    public void clearLights() {
        lights.clear();
    }
    
    public void setWireframeMode(boolean wireframe) {
        this.wireframeMode = wireframe;
    }
    
    public void setBackfaceCulling(boolean culling) {
        this.backfaceCulling = culling;
    }
    
    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }
    
    // Clear the buffer
    public void clear(Color color) {
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        
        // Clear z-buffer
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zBuffer[x][y] = Double.POSITIVE_INFINITY;
            }
        }
    }
    
    // Render a mesh
    public void render(Mesh mesh) {
        if (camera == null) return;
        
        Matrix4x4 modelMatrix = mesh.getTransformMatrix();
        Matrix4x4 viewMatrix = camera.getViewMatrix();
        Matrix4x4 projectionMatrix = camera.getProjectionMatrix();
        
        // Combined transformation matrix
        Matrix4x4 mvp = projectionMatrix.multiply(viewMatrix).multiply(modelMatrix);
        
        List<TriangleProjected> projectedTriangles = new ArrayList<>();
        
        for (Triangle tri : mesh.triangles) {
            // Transform vertices
            Vector3D v1World = modelMatrix.transform(tri.v1.position);
            Vector3D v2World = modelMatrix.transform(tri.v2.position);
            Vector3D v3World = modelMatrix.transform(tri.v3.position);
            
            // Calculate face normal in world space
            Vector3D edge1 = v2World.subtract(v1World);
            Vector3D edge2 = v3World.subtract(v1World);
            Vector3D normal = edge1.cross(edge2).normalize();
            
            // Backface culling
            if (backfaceCulling) {
                Vector3D viewDir = v1World.subtract(camera.position).normalize();
                if (normal.dot(viewDir) >= 0) {
                    continue;
                }
            }
            
            // Transform to clip space
            Vector3D v1Clip = mvp.transform(tri.v1.position);
            Vector3D v2Clip = mvp.transform(tri.v2.position);
            Vector3D v3Clip = mvp.transform(tri.v3.position);
            
            // Simple clipping (skip triangles outside view frustum)
            if (!isInViewFrustum(v1Clip) && !isInViewFrustum(v2Clip) && !isInViewFrustum(v3Clip)) {
                continue;
            }
            
            // Convert to screen space
            Point p1 = toScreenSpace(v1Clip);
            Point p2 = toScreenSpace(v2Clip);
            Point p3 = toScreenSpace(v3Clip);
            
            // Calculate lighting
            Color finalColor = tri.color;
            if (useLighting && !lights.isEmpty()) {
                Vector3D center = new Vector3D(
                    (v1World.x + v2World.x + v3World.x) / 3.0,
                    (v1World.y + v2World.y + v3World.y) / 3.0,
                    (v1World.z + v2World.z + v3World.z) / 3.0
                );
                
                finalColor = calculateLighting(center, normal, tri.color);
            }
            
            // Calculate average depth for sorting
            double avgDepth = (v1Clip.z + v2Clip.z + v3Clip.z) / 3.0;
            
            projectedTriangles.add(new TriangleProjected(p1, p2, p3, finalColor, avgDepth));
        }
        
        // Sort triangles by depth (painter's algorithm)
        projectedTriangles.sort((a, b) -> Double.compare(b.depth, a.depth));
        
        // Draw triangles
        for (TriangleProjected tri : projectedTriangles) {
            drawTriangle(tri);
        }
    }
    
    private boolean isInViewFrustum(Vector3D v) {
        return v.x >= -1.5 && v.x <= 1.5 &&
               v.y >= -1.5 && v.y <= 1.5 &&
               v.z >= -1.5 && v.z <= 1.5;
    }
    
    private Point toScreenSpace(Vector3D clipSpace) {
        int x = (int) ((clipSpace.x + 1.0) * 0.5 * width);
        int y = (int) ((1.0 - clipSpace.y) * 0.5 * height);
        return new Point(x, y);
    }
    
    private Color calculateLighting(Vector3D point, Vector3D normal, Color baseColor) {
        int r = 0, g = 0, b = 0;
        
        for (Light light : lights) {
            Color lightContribution = light.calculateLighting(point, normal, baseColor);
            r += lightContribution.getRed();
            g += lightContribution.getGreen();
            b += lightContribution.getBlue();
        }
        
        return new Color(
            Math.min(255, r),
            Math.min(255, g),
            Math.min(255, b)
        );
    }
    
    private void drawTriangle(TriangleProjected tri) {
        if (wireframeMode) {
            g2d.setColor(tri.color);
            g2d.drawLine(tri.p1.x, tri.p1.y, tri.p2.x, tri.p2.y);
            g2d.drawLine(tri.p2.x, tri.p2.y, tri.p3.x, tri.p3.y);
            g2d.drawLine(tri.p3.x, tri.p3.y, tri.p1.x, tri.p1.y);
        } else {
            int[] xPoints = {tri.p1.x, tri.p2.x, tri.p3.x};
            int[] yPoints = {tri.p1.y, tri.p2.y, tri.p3.y};
            
            g2d.setColor(tri.color);
            g2d.fillPolygon(xPoints, yPoints, 3);
            
            // Optional: draw edges
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
    }
    
    public BufferedImage getBuffer() {
        return buffer;
    }
    
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2d = buffer.createGraphics();
        this.zBuffer = new double[width][height];
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        if (camera != null) {
            camera.aspectRatio = (double) width / height;
        }
    }
    
    // Helper class for projected triangles
    private static class TriangleProjected {
        Point p1, p2, p3;
        Color color;
        double depth;
        
        TriangleProjected(Point p1, Point p2, Point p3, Color color, double depth) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.color = color;
            this.depth = depth;
        }
    }
}

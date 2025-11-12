# Java 3D Graphics Engine

A fully native Java 3D graphics engine built from scratch without external libraries. Features real-time rendering, interactive camera controls, lighting, and can be integrated with Swing or JavaFX.

## Features

### Core Engine
- **3D Mathematics**: Vector3D, Matrix4x4 for transformations
- **Geometry**: Vertex, Triangle, and Mesh structures
- **Camera System**: Perspective projection with orbit and movement controls
- **Rendering Pipeline**: 
  - Model-View-Projection transformations
  - Backface culling
  - Painter's algorithm for depth sorting
  - Wireframe and solid rendering modes
- **Lighting System**:
  - Ambient lighting
  - Directional lights
  - Point lights with attenuation
  - Diffuse (Lambertian) shading

### Interactive Demo
- **Camera Controls**:
  - WASD - Move camera forward/backward/left/right
  - Space/Shift - Move up/down
  - Mouse drag - Rotate camera (orbit)
  - Mouse wheel - Zoom in/out
- **Rendering Options**:
  - F - Toggle wireframe mode
  - R - Toggle auto-rotation
  - L - Toggle lighting
  - ESC - Exit application
- **Multiple 3D Objects**: Rotating cube, spinning pyramid, orbiting cube

## Architecture

### Class Structure

```
Vector3D.java          - 3D vector mathematics (add, subtract, dot, cross, normalize)
Matrix4x4.java         - 4x4 transformation matrices (rotation, translation, scaling, projection)
Vertex.java            - 3D vertex with position, normal, and color
Triangle.java          - Triangle primitive with three vertices
Mesh.java              - 3D mesh composed of triangles
Camera.java            - Virtual camera with view and projection
Light.java             - Light sources (ambient, directional, point)
Renderer3D.java        - Main rendering engine
Demo3D.java            - Interactive Swing-based demo application
```

### Rendering Pipeline

1. **Model Transformation**: Apply mesh rotation, scaling, and translation
2. **View Transformation**: Transform to camera space
3. **Projection**: Apply perspective projection
4. **Backface Culling**: Remove triangles facing away from camera
5. **Lighting**: Calculate lighting for visible surfaces
6. **Clipping**: Remove triangles outside view frustum
7. **Screen Mapping**: Convert clip space to screen coordinates
8. **Depth Sorting**: Sort triangles by depth (painter's algorithm)
9. **Rasterization**: Draw triangles to screen

## Build and Run (Maven)

### Compile All Modules
```bash
mvn clean install
```

### Run the Interactive Demo
```bash
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D
```

### Run Other Examples
```bash
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.SimpleExample
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.AdvancedExample
```

## Usage Examples

### Creating a Simple Mesh
```java
// Create a cube
Mesh cube = Mesh.createCube(2.0);
cube.position = new Vector3D(0, 0, 0);
cube.rotation = new Vector3D(0, Math.PI / 4, 0);

// Create a pyramid
Mesh pyramid = Mesh.createPyramid(2.0);
pyramid.position = new Vector3D(3, 0, 0);
```

### Setting Up a Camera
```java
Camera camera = new Camera(
    new Vector3D(0, 2, -5),  // Camera position
    new Vector3D(0, 0, 0)    // Look at target
);
camera.fov = Math.toRadians(60);
```

### Adding Lights
```java
Renderer3D renderer = new Renderer3D(800, 600);
renderer.setCamera(camera);

// Ambient light
renderer.addLight(Light.createAmbient(Color.WHITE, 0.3));

// Directional light (like sun)
renderer.addLight(Light.createDirectional(
    new Vector3D(-1, -1, -1), Color.WHITE));

// Point light
renderer.addLight(Light.createPoint(
    new Vector3D(5, 5, -5), Color.YELLOW, 1.0));
```

### Rendering
```java
// Clear the buffer
renderer.clear(Color.BLACK);

// Render meshes
for (Mesh mesh : meshes) {
    renderer.render(mesh);
}

// Get the rendered image
BufferedImage image = renderer.getBuffer();
```

### Integration with Swing
```java
public class MyPanel extends JPanel {
    private Renderer3D renderer;
    
    public MyPanel() {
        renderer = new Renderer3D(800, 600);
        // Setup camera, lights, meshes...
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.clear(Color.BLACK);
        renderer.render(myMesh);
        g.drawImage(renderer.getBuffer(), 0, 0, null);
    }
}
```

### Integration with JavaFX
```java
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

ImageView imageView = new ImageView();
renderer.clear(Color.BLACK);
renderer.render(mesh);
BufferedImage buffer = renderer.getBuffer();
imageView.setImage(SwingFXUtils.toFXImage(buffer, null));
```

## Performance Considerations

- **Painter's Algorithm**: Simple but not optimal for complex scenes. Consider implementing a Z-buffer for better depth handling.
- **Backface Culling**: Enabled by default, significantly reduces triangles to render.
- **Triangle Count**: Performance scales with triangle count. For complex scenes, consider level-of-detail (LOD) systems.
- **Rendering Resolution**: Lower resolutions render faster. Adjust based on your needs.

## Extending the Engine

### Adding New Mesh Types
Extend the `Mesh` class with static factory methods:
```java
public static Mesh createSphere(double radius, int segments) {
    Mesh mesh = new Mesh();
    // Generate sphere triangles...
    return mesh;
}
```

### Custom Shading Models
Modify `Light.calculateLighting()` to implement:
- Specular highlights (Phong/Blinn-Phong)
- Normal mapping
- Ambient occlusion
- Custom shader effects

### Advanced Features to Add
- **Texturing**: UV mapping and texture sampling
- **Z-Buffer**: Proper depth testing instead of painter's algorithm
- **Clipping**: Full view frustum clipping for partially visible triangles
- **Shadows**: Shadow mapping or shadow volumes
- **Post-Processing**: Bloom, anti-aliasing, depth of field
- **Scene Graph**: Hierarchical object organization
- **Spatial Partitioning**: Octrees or BSP trees for large scenes

## Technical Details

### Coordinate Systems
- **World Space**: Global coordinate system
- **View Space**: Relative to camera
- **Clip Space**: After projection (-1 to 1 on all axes)
- **Screen Space**: Pixel coordinates

### Matrix Operations
- All transformations use 4x4 homogeneous matrices
- Order of operations: Scale → Rotate → Translate
- Combined MVP matrix: Projection × View × Model

### Lighting Model
- **Ambient**: Constant base lighting
- **Diffuse**: Angle-dependent (Lambertian)
- **Attenuation**: Distance falloff for point lights: `1 / (1 + 0.1*d + 0.01*d²)`

## License

This is a educational/demonstration project. Feel free to use and modify as needed.

## Future Enhancements

- [ ] Texture mapping
- [ ] Z-buffer for proper depth testing
- [ ] Specular lighting
- [ ] More primitive shapes (sphere, cylinder, torus)
- [ ] Model loading (OBJ, STL formats)
- [ ] Scene graph with parent-child relationships
- [ ] Frustum culling optimization
- [ ] Multi-threading for parallel rendering
- [ ] Shader system for custom materials
- [ ] Physics integration

## Author

Created as a fully native Java 3D graphics engine without external dependencies.

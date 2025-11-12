# Quick Start Guide - Java 3D Graphics Engine

## Running the Demo

```bash
# Build all modules
mvn clean install

# Run the interactive demo
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D
```

## Keyboard Controls

| Key | Action |
|-----|--------|
| W | Move camera forward |
| S | Move camera backward |
| A | Move camera left |
| D | Move camera right |
| Space | Move camera up |
| Shift | Move camera down |
| F | Toggle wireframe mode |
| R | Toggle auto-rotation |
| L | Toggle lighting |
| ESC | Exit application |

## Mouse Controls

- **Left Click + Drag**: Rotate camera around scene
- **Mouse Wheel**: Zoom in/out

## Core Components

### 1. Vector3D
Basic 3D vector operations:
```java
Vector3D v1 = new Vector3D(1, 2, 3);
Vector3D v2 = new Vector3D(4, 5, 6);
Vector3D sum = v1.add(v2);
double dot = v1.dot(v2);
Vector3D cross = v1.cross(v2);
Vector3D normalized = v1.normalize();
```

### 2. Matrix4x4
Transformation matrices:
```java
Matrix4x4 translate = Matrix4x4.translation(5, 0, 0);
Matrix4x4 rotateY = Matrix4x4.rotationY(Math.PI / 4);
Matrix4x4 scale = Matrix4x4.scaling(2, 2, 2);
Matrix4x4 combined = translate.multiply(rotateY).multiply(scale);
Vector3D transformed = combined.transform(myVector);
```

### 3. Creating Meshes
```java
// Built-in shapes
Mesh cube = Mesh.createCube(2.0);
Mesh pyramid = Mesh.createPyramid(2.0);

// Position and rotate
cube.position = new Vector3D(0, 0, 0);
cube.rotation = new Vector3D(0, Math.PI / 4, 0);
cube.scale = new Vector3D(1.5, 1.5, 1.5);

// Custom mesh
Mesh custom = new Mesh();
Triangle tri = new Triangle(
    new Vertex(0, 1, 0),
    new Vertex(-1, -1, 0),
    new Vertex(1, -1, 0),
    Color.RED
);
custom.addTriangle(tri);
```

### 4. Camera Setup
```java
Camera camera = new Camera(
    new Vector3D(0, 2, -5),  // Position
    new Vector3D(0, 0, 0)    // Look at
);
camera.fov = Math.toRadians(60);

// Move camera
camera.moveForward(1.0);
camera.strafe(0.5);
camera.moveVertical(0.5);
camera.orbit(0.1, 0.1); // Rotate around target
```

### 5. Lighting
```java
Renderer3D renderer = new Renderer3D(800, 600);

// Ambient light (base illumination)
renderer.addLight(Light.createAmbient(Color.WHITE, 0.3));

// Directional light (sun-like)
renderer.addLight(Light.createDirectional(
    new Vector3D(-1, -1, -1), 
    Color.WHITE
));

// Point light (lamp-like)
renderer.addLight(Light.createPoint(
    new Vector3D(5, 5, -5), 
    new Color(255, 200, 100), 
    1.0  // intensity
));
```

### 6. Rendering
```java
// Setup
Renderer3D renderer = new Renderer3D(800, 600);
renderer.setCamera(camera);

// Render loop
renderer.clear(Color.BLACK);
for (Mesh mesh : meshes) {
    renderer.render(mesh);
}
BufferedImage image = renderer.getBuffer();

// Options
renderer.setWireframeMode(true);
renderer.setBackfaceCulling(true);
renderer.setUseLighting(true);
```

## Integration Examples

### Swing Application
```java
public class My3DApp extends JPanel {
    private Renderer3D renderer;
    private Mesh mesh;
    
    public My3DApp() {
        renderer = new Renderer3D(800, 600);
        Camera camera = new Camera(
            new Vector3D(0, 0, -5),
            new Vector3D(0, 0, 0)
        );
        renderer.setCamera(camera);
        renderer.addLight(Light.createAmbient(Color.WHITE, 0.5));
        
        mesh = Mesh.createCube(2.0);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.clear(Color.BLACK);
        mesh.rotation.y += 0.01; // Rotate
        renderer.render(mesh);
        g.drawImage(renderer.getBuffer(), 0, 0, null);
        repaint(); // Continuous rendering
    }
}
```

### JavaFX Application
```java
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class My3DFXApp extends Application {
    private Renderer3D renderer;
    private Mesh mesh;
    private ImageView imageView;
    
    @Override
    public void start(Stage stage) {
        renderer = new Renderer3D(800, 600);
        Camera camera = new Camera(
            new Vector3D(0, 0, -5),
            new Vector3D(0, 0, 0)
        );
        renderer.setCamera(camera);
        renderer.addLight(Light.createAmbient(Color.WHITE, 0.5));
        
        mesh = Mesh.createCube(2.0);
        imageView = new ImageView();
        
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("3D Engine - JavaFX");
        stage.show();
        
        // Animation loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderer.clear(Color.BLACK);
                mesh.rotation.y += 0.01;
                renderer.render(mesh);
                imageView.setImage(
                    SwingFXUtils.toFXImage(renderer.getBuffer(), null)
                );
            }
        }.start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
```

## Performance Tips

1. **Reduce Triangle Count**: Fewer triangles = faster rendering
2. **Enable Backface Culling**: Skip triangles facing away from camera
3. **Lower Resolution**: Smaller buffer = faster rendering
4. **Optimize Lighting**: Fewer lights = faster calculations
5. **Use Wireframe**: For debugging, wireframe is much faster

## Common Issues

### Objects Not Visible
- Check camera position and target
- Ensure objects are within view frustum
- Verify object positions are reasonable
- Check near/far plane settings

### Slow Performance
- Reduce triangle count
- Lower rendering resolution
- Disable lighting temporarily
- Enable backface culling

### Incorrect Colors/Lighting
- Check light positions and intensities
- Verify normal vectors are calculated correctly
- Ensure lighting is enabled

## Next Steps

1. **Add Textures**: Implement UV mapping
2. **Better Depth Testing**: Replace painter's algorithm with Z-buffer
3. **More Shapes**: Add sphere, cylinder, etc.
4. **Load Models**: Import OBJ or STL files
5. **Physics**: Add collision detection
6. **Optimize**: Multi-threading, spatial partitioning

## Resources

- All source code is well-commented
- See `README.md` for detailed architecture
- Experiment with the `Demo3D.java` to understand the system

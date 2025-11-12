# Java 3D Graphics Engine

A fully native Java 3D graphics engine built from scratch without external dependencies. Features real-time rendering, interactive camera controls, lighting, and can be integrated with Swing or JavaFX.

## Directory Structure

```
JRender/
├── core/                 # jrender-core module (engine source)
│   └── src/main/java/com/github/jordyh297/jrender/
├── examples/             # jrender-examples module (demo apps)
│   └── src/main/java/com/github/jordyh297/jrender/examples/
├── docs/                 # Documentation set
│   ├── README.md
│   ├── QUICKSTART.md
│   └── PROJECT_SUMMARY.md
├── AGENTS.md             # Contributor guidelines
├── QUICKREF.md           # Quick reference guide
└── pom.xml               # Maven parent (aggregates modules)
```

## Quick Start

### 1. Build Everything
```bash
mvn clean install
```

### 2. Run Examples
```bash
# Full interactive demo (recommended)
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D

# Minimal rotating cube
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.SimpleExample

# Advanced multi-object scene
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.AdvancedExample
```

### Alternative: Manual Execution
```bash
mvn -pl examples package
java -cp \"core/target/jrender-core-1.0.0-SNAPSHOT.jar:examples/target/jrender-examples-1.0.0-SNAPSHOT.jar\" \\
     com.github.jordyh297.jrender.examples.Demo3D
```
> On Windows, replace the colon in the classpath with a semicolon.

## Features

### Core Engine
- **3D Mathematics**: Vector3D, Matrix4x4 for transformations
- **Geometry**: Vertex, Triangle, and Mesh structures
- **Camera System**: Perspective projection with orbit and movement controls
- **Rendering Pipeline**: Model-View-Projection transformations, backface culling, depth sorting
- **Lighting System**: Ambient, directional, and point lights with diffuse shading
- **Rendering Modes**: Wireframe and solid rendering

### Demo Applications

#### Demo3D (Full Interactive Demo)
- Multiple animated 3D objects
- **Controls:**
  - WASD - Move camera
  - Space/Shift - Move up/down
  - Mouse drag - Rotate camera
  - Mouse wheel - Zoom
  - F - Toggle wireframe
  - R - Toggle auto-rotation
  - L - Toggle lighting
  - ESC - Exit

#### SimpleExample
- Minimal code example
- Single rotating cube
- Perfect starting point for integration

#### AdvancedExample
- Multi-object scene
- GUI control panel with sliders
- Real-time parameter adjustment

## Integration Examples

### Basic Swing Integration
```java
Renderer3D renderer = new Renderer3D(800, 600);
Camera camera = new Camera(new Vector3D(0, 0, -5), new Vector3D(0, 0, 0));
renderer.setCamera(camera);
Mesh cube = Mesh.createCube(2.0);

// In paintComponent:
renderer.clear(Color.BLACK);
renderer.render(cube);
g.drawImage(renderer.getBuffer(), 0, 0, null);
```

### JavaFX Integration
```java
import javafx.embed.swing.SwingFXUtils;

Renderer3D renderer = new Renderer3D(800, 600);
ImageView imageView = new ImageView();

// In animation loop:
renderer.clear(Color.BLACK);
renderer.render(mesh);
imageView.setImage(SwingFXUtils.toFXImage(renderer.getBuffer(), null));
```

## Documentation

- **[README.md](docs/README.md)** - Complete technical documentation
- **[QUICKSTART.md](docs/QUICKSTART.md)** - Quick reference guide with code examples
- **[PROJECT_SUMMARY.md](docs/PROJECT_SUMMARY.md)** - Project overview and architecture

## Requirements

- Java JDK 8 or higher
- No external dependencies required

## Performance

- Target: 60 FPS
- Resolution: 800x600 to 1920x1080
- Triangle budget: ~1000-5000 for smooth performance
- Memory: ~10-50 MB depending on scene complexity

## Extending the Engine

The engine is designed to be easily extensible:

- Add new mesh types in `Mesh.java`
- Implement custom shading in `Light.java`
- Add texturing support to `Renderer3D.java`
- Create scene graphs for hierarchical objects
- Implement Z-buffer for proper depth testing

## License

Educational/demonstration project. Free to use and modify.

## Troubleshooting

### Build Issues
```bash
mvn clean
mvn -U install
```

### Runtime Issues
- Ensure modules were built (`core/target` and `examples/target` exist)
- Run demos through Maven to pick up dependencies automatically
- Verify Java version: `java -version` (JDK 8+)

## Future Enhancements

- [ ] Texture mapping
- [ ] Z-buffer depth testing
- [ ] More primitive shapes
- [ ] Model loading (OBJ, STL)
- [ ] Specular lighting
- [ ] Shadow mapping
- [ ] Multi-threading

---

**Created**: November 2025  
**Version**: 1.0

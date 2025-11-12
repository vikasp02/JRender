# Quick Reference Card - Java 3D Engine

## Project Structure

```
JRender/
├── core/                    → Engine module (src/main/java/com/github/jordyh297/jrender)
├── examples/                → Demo module (src/main/java/com/github/jordyh297/jrender/examples)
├── docs/                    → Documentation bundle
├── AGENTS.md                → Contributor guide
├── QUICKREF.md              → This file
└── pom.xml                  → Maven parent (runs all modules)
```

## Quick Commands

```bash
# Build everything
mvn clean install

# Run demos (pick one)
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D      # ⭐ Full demo
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.SimpleExample
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.AdvancedExample

# Clean build outputs
mvn clean
```

## Demo3D Controls

| Key/Mouse | Action |
|-----------|--------|
| **W** | Forward |
| **S** | Backward |
| **A** | Left |
| **D** | Right |
| **Space** | Up |
| **Shift** | Down |
| **Mouse Drag** | Rotate camera |
| **Mouse Wheel** | Zoom |
| **F** | Wireframe toggle |
| **R** | Auto-rotate toggle |
| **L** | Lighting toggle |
| **ESC** | Exit |

## Core Classes

| Class | Purpose |
|-------|---------|
| `Vector3D` | 3D vectors & math |
| `Matrix4x4` | Transformations |
| `Vertex` | 3D point + normal |
| `Triangle` | 3D triangle |
| `Mesh` | Collection of triangles |
| `Camera` | View & projection |
| `Light` | Lighting sources |
| `Renderer3D` | Main render engine |

## Quick Code Examples

### Create a Cube
```java
Mesh cube = Mesh.createCube(2.0);
cube.position = new Vector3D(0, 0, 0);
cube.rotation.y = Math.PI / 4;
```

### Setup Camera
```java
Camera camera = new Camera(
    new Vector3D(0, 2, -5),  // Position
    new Vector3D(0, 0, 0)    // Look at
);
```

### Add Lighting
```java
renderer.addLight(Light.createAmbient(Color.WHITE, 0.3));
renderer.addLight(Light.createDirectional(
    new Vector3D(-1, -1, -1), Color.WHITE));
```

### Render Loop
```java
renderer.clear(Color.BLACK);
mesh.rotation.y += 0.01;  // Animate
renderer.render(mesh);
g.drawImage(renderer.getBuffer(), 0, 0, null);
```

## Documentation

| File | Content |
|------|---------|
| `README.md` | Main documentation |
| `docs/README.md` | Technical details |
| `docs/QUICKSTART.md` | Code examples |
| `docs/PROJECT_SUMMARY.md` | Architecture |
| `STRUCTURE.md` | Directory layout |

## 🔧 Troubleshooting

**Compilation fails?**
```bash
mvn clean
mvn install
```

**Can't run example?**
```bash
mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D
```

**Objects not visible?**
- Check camera position
- Verify object positions
- Enable lighting

## Performance Tips

Enable backface culling (default: ON)  
Use wireframe for debugging  
Keep triangle count < 5000  
Lower resolution for more FPS  
Reduce number of lights  

## Next Steps

1. Run `mvn clean install`
2. Run `mvn -pl examples exec:java -Dexec.mainClass=com.github.jordyh297.jrender.examples.Demo3D`
3. Read `docs/QUICKSTART.md` for examples
4. Modify `examples/SimpleExample.java`
5. Create your own 3D scenes!

## Features

Pure Java - no dependencies  
Real-time 60 FPS rendering  
Interactive camera controls  
Multiple lighting types  
Wireframe & solid modes  
Easy Swing/JavaFX integration  
Well-documented code  
Ready-to-run examples  

---

**Version**: 1.0 | **Date**: Nov 2025 | **Lines**: ~1300

# OpenGL Java 3D Engine - Project Context for Claude

## Project Overview
This is a modern OpenGL-based 3D engine written in Java 24 (with preview features enabled). The engine implements a complete graphics pipeline with PBR (Physically Based Rendering), particle systems, terrain rendering, and an integrated editor using ImGui.

## Technology Stack
- **Java Version**: 24 (with --enable-preview)
- **OpenGL Version**: 4.6 Core Profile
- **Build System**: Maven
- **Main Dependencies**:
  - LWJGL 3.3.2 (OpenGL, GLFW, OpenAL, Assimp, STB)
  - ImGui-Java 1.86.10 (Editor UI)
  - PhysX-JNI 2.0.6 (Physics)
  - JOML 1.10.5 (Math library)
  - Gson 2.10.1 (Serialization)

## Project Structure

```
src/main/java/app/
├── audio/              # Audio system (OpenAL)
├── boot/               # Application entry points
├── ecs/                # Entity Component System
│   ├── components/     # All component types
│   ├── Entity.java
│   └── EntitySystem.java
├── editor/             # Editor functionality
│   ├── component/      # Editor components
│   ├── imgui/          # ImGui integration
│   └── GlfwWindow.java # Main window management
├── math/               # Mathematical utilities
│   └── components/     # Transform, Camera, RayCast
├── renderer/           # Rendering subsystems
│   ├── debug/          # Debug rendering (grid, billboards)
│   ├── fog/            # Fog effects
│   ├── framebuffer/    # FBO management
│   ├── ibl/            # Image-based lighting & skybox
│   ├── lights/         # Lighting system
│   ├── particle/       # Particle systems
│   ├── pbr/            # PBR mesh rendering
│   ├── shaders/        # Shader management
│   └── terrain/        # Terrain with quadtree LOD
└── utilities/          # Helper utilities
    ├── data/
    ├── logger/
    ├── resource/       # Resource management
    └── serialize/      # Serialization system

src/main/resources/
├── shaders/            # GLSL shader files
├── editor/             # Editor resources
└── other assets...
```

## Architecture & Design Patterns

### SOLID Principles Application

#### 1. Single Responsibility Principle (SRP)
- Each class has a single, well-defined purpose
- Examples:
  - `Entity`: Manages entity hierarchy and components
  - `Component`: Base for all ECS components
  - `ShaderProgram`: Handles shader compilation and uniforms
  - `MeshRenderer`: Responsible only for mesh rendering
  - `ResourceManager`: Manages resource loading

#### 2. Open/Closed Principle (OCP)
- Abstract base classes allow extension without modification
- Examples:
  - `Component` abstract class for new component types
  - `ShaderProgram` abstract class for shader variants
  - `ImguiLayer` interface for UI extensions

#### 3. Liskov Substitution Principle (LSP)
- Derived classes can replace base classes
- All components extend `Component` consistently
- Shader implementations extend `ShaderProgram`

#### 4. Interface Segregation Principle (ISP)
- Interfaces are focused and minimal
- `Serializable` interface for persistence
- `ImguiLayer` for UI rendering

#### 5. Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions
- Renderer depends on abstract `Component`, not concrete implementations
- Resource loading through `ResourceManager` abstraction

### Key Design Patterns

1. **Entity Component System (ECS)**
   - Composition over inheritance
   - Flexible game object architecture
   - Components: Transform, Mesh, Light, Audio, etc.

2. **Abstract Factory Pattern**
   - `ShaderProgram` base class
   - Concrete implementations for different shader types

3. **Singleton Pattern**
   - `ResourceManager` for centralized resource management
   - `ShaderManager` for shader lifecycle

4. **Observer Pattern**
   - Component update system
   - Entity hierarchy updates

5. **Strategy Pattern**
   - Different rendering strategies (PBR, particle, terrain)
   - Material system with configurable properties

## Coding Standards & Conventions

### Naming Conventions
- **Classes**: PascalCase (e.g., `MeshRenderer`, `EntitySystem`)
- **Methods**: camelCase (e.g., `loadToVAO`, `updateComponent`)
- **Constants**: UPPER_SNAKE_CASE (e.g., in `UniformsNames`)
- **Packages**: lowercase (e.g., `app.renderer.pbr`)
- **Prefix Convention**: "OL" prefix for math classes (e.g., `OLVector3f`, `OLMatrix4f`)

### Code Organization
- One class per file
- Package structure mirrors architectural layers
- Abstract classes for extensible systems
- Interfaces for contracts

### Error Handling
- Custom `Logger` and `LogError` classes
- Graceful shader compilation error reporting
- Resource loading validation

### Memory Management
- Explicit cleanup methods (`cleanUp()`)
- Proper OpenGL resource disposal
- Buffer management with LWJGL

## Key Components

### Core Systems

1. **Window Management** (`GlfwWindow`)
   - GLFW window creation and management
   - OpenGL context setup
   - Input handling
   - V-sync enabled by default

2. **Entity Component System**
   - `Entity`: Container for components with parent-child hierarchy
   - `Component`: Abstract base for all components
   - `EntitySystem`: Manages all entities
   - Components update recursively through hierarchy

3. **Rendering Pipeline**
   - Forward rendering with PBR
   - Multiple render passes (geometry, particles, debug)
   - Deferred shading preparation (framebuffer system)
   - Post-processing support

4. **Shader System**
   - Automatic shader compilation from combined .glsl files
   - Uniform management with caching
   - Support for UBOs (Uniform Buffer Objects)
   - Shader hot-reloading capability

5. **Resource Management**
   - Centralized loading for all asset types
   - Support for: meshes, textures, shaders, audio, fonts
   - Path-based resource identification

### Rendering Features

1. **PBR (Physically Based Rendering)**
   - Metallic-roughness workflow
   - Image-based lighting (IBL)
   - Multiple texture maps support

2. **Lighting**
   - Directional lights
   - Point lights with attenuation
   - Spot lights with cone angles
   - Shadow mapping preparation

3. **Terrain System**
   - Quadtree-based LOD
   - Height-based rendering
   - Material blending support

4. **Particle Systems**
   - Sprite-based particles
   - Mesh-based particles
   - GPU-accelerated updates

5. **Post-processing**
   - Fog effects
   - Skybox with HDR support
   - Framebuffer effects

### Editor Features
- ImGui integration for UI
- Scene graph visualization
- Component inspector
- Content browser
- Material editor
- Real-time debugging tools

## Development Guidelines

### When Adding New Features

1. **New Component Types**
   - Extend `Component` abstract class
   - Implement required methods: `imguiDraw()`, `cleanUp()`
   - Add to appropriate entity
   - Register with serialization if needed

2. **New Shaders**
   - Create .glsl file with vertex/fragment sections
   - Extend `ShaderProgram` class
   - Implement `getAllUniformLocations()`
   - Add to `ShaderManager`

3. **New Rendering Features**
   - Create renderer class in appropriate package
   - Integrate with existing pipeline
   - Ensure proper cleanup

### Best Practices

1. **Resource Management**
   - Always implement `cleanUp()` methods
   - Dispose OpenGL resources properly
   - Use `ResourceManager` for loading

2. **Performance**
   - Minimize state changes
   - Batch similar draw calls
   - Use instancing where applicable
   - Profile with Remotery integration

3. **OpenGL Practices**
   - Check for errors in debug mode
   - Use modern OpenGL (4.6 core)
   - Avoid deprecated functionality
   - Proper buffer management

4. **Code Quality**
   - Follow SOLID principles
   - Keep methods focused and small
   - Use meaningful variable names
   - Document complex algorithms

## Testing & Debugging

### Debug Features
- Grid rendering for spatial reference
- Billboard rendering for debug markers
- OpenGL debug context enabled
- Remotery profiler integration
- Logger with error tracking

### Common Commands
```bash
# Build the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="app.boot.Run"

# Run tests (when implemented)
mvn test
```

## Known Areas of Improvement
- TODO: Proper UUID generation for entities (currently uses identityHashCode)
- Shadow mapping implementation pending
- Deferred rendering full implementation
- More comprehensive error handling
- Unit test coverage needed

## File Extensions & Formats
- `.glsl` - Combined shader files (vertex + fragment)
- `.obj` - 3D model files (Wavefront)
- `.png/.jpg` - Texture files
- `.json` - Serialized scene/component data
- `.ttf` - Font files for ImGui

## Performance Considerations
- VBO/VAO management for efficient rendering
- Instanced rendering for repeated objects
- Frustum culling preparation
- Level-of-detail (LOD) for terrain
- Texture atlasing support

## Thread Safety
- Main rendering on primary thread
- Audio system on separate thread
- Resource loading can be async
- ImGui must be on main thread

This document provides comprehensive context for understanding and working with the OpenGL Java 3D Engine project. Follow SOLID principles and existing patterns when extending functionality.
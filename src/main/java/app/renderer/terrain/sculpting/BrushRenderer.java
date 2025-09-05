package app.renderer.terrain.sculpting;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class BrushRenderer {
    private ShaderBrush shader;
    private int vao;
    private int vbo;
    private int ebo;
    private int vertexCount;
    private boolean initialized = false;
    private float currentTime = 0.0f;
    
    public BrushRenderer() {
        System.out.println("🖌️ BrushRenderer: Initializing 3D brush visualization");
    }
    
    public void initialize() {
        if (initialized) {
            System.out.println("🖌️ BrushRenderer: Already initialized, skipping");
            return;
        }
        
        try {
            System.out.println("🖌️ BrushRenderer: Loading brush shader from brush.glsl");
            shader = new ShaderBrush(Path.of("src/main/resources/shaders/terrain/brush.glsl"));
            
            System.out.println("🖌️ BrushRenderer: Creating brush geometry");
            createBrushGeometry();
            
            initialized = true;
            System.out.println("✅ BrushRenderer: Successfully initialized");
        } catch (Exception e) {
            System.out.println("❌ BrushRenderer: Failed to initialize - " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createBrushGeometry() {
        // Create a quad that will be scaled by the brush size
        // The shader will handle the circular/square shape clipping
        float[] vertices = {
            // Position (2D, will be extruded to 3D in vertex shader)
            -1.0f, -1.0f,  // Bottom-left
             1.0f, -1.0f,  // Bottom-right
             1.0f,  1.0f,  // Top-right
            -1.0f,  1.0f   // Top-left
        };
        
        int[] indices = {
            0, 1, 2,  // First triangle
            2, 3, 0   // Second triangle
        };
        
        vertexCount = indices.length;
        
        System.out.println("🖌️ BrushRenderer: Creating OpenGL buffers");
        
        // Generate VAO, VBO, EBO
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        
        glBindVertexArray(vao);
        
        // Upload vertex data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        
        // Upload index data
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        // Set vertex attributes (position attribute at location 0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        glBindVertexArray(0);
        
        System.out.println("✅ BrushRenderer: Geometry created - VAO: " + vao + ", VBO: " + vbo + ", EBO: " + ebo + ", Vertices: " + vertexCount);
    }
    
    public void render(OLVector3f brushPosition, BrushSettings brush, OLMatrix4f viewMatrix, OLMatrix4f projectionMatrix) {
        if (!initialized) {
            System.out.println("⚠️ BrushRenderer: Not initialized, skipping render");
            return;
        }
        
        if (brushPosition == null || brush == null) {
            return;
        }
        
        // Update time for pulsing effect
        currentTime += 0.016f; // Approximate 60 FPS delta time
        
        System.out.println("🎨 BrushRenderer: Rendering brush at (" + brushPosition.x + ", " + brushPosition.z + ") with size " + brush.getSize());
        System.out.println("   📊 Brush details - Type: " + brush.getBrushType() + ", Shape: " + brush.getShape() + ", Alpha: " + brush.getStrength() + ", Falloff: " + brush.getFalloff());
        
        // Enable blending for transparent brush
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Disable depth testing to ensure brush always renders on top
        glDisable(GL_DEPTH_TEST);
        
        shader.start();
        
        // Load brush data to shader uniforms with visibility modifications
        // Load individual uniforms with enhanced visibility
        // Raise brush significantly above terrain surface for visibility
        OLVector3f elevatedPosition = new OLVector3f(brushPosition.x, brushPosition.y + 10.0f, brushPosition.z);
        shader.loadBrushPosition(elevatedPosition);
        
        // Ensure minimum visible size (50 units minimum for terrain that spans 8192 units)
        float renderSize = Math.max(brush.getSize(), 50.0f);
        System.out.println("   📏 Rendering with enhanced size: " + renderSize + " (original: " + brush.getSize() + ")");
        shader.loadBrushSize(renderSize);
        
        shader.loadBrushFalloff(brush.getFalloff());
        shader.loadBrushShape(brush.getShape().ordinal());
        shader.loadTime(currentTime);
        
        // Load color based on brush type
        OLVector3f brushColor = getBrushColor(brush.getBrushType());
        shader.loadBrushColor(brushColor);
        
        // Override alpha for better visibility
        shader.loadBrushAlpha(0.9f);
        System.out.println("   🎨 Using color: " + getBrushColorName(brush.getBrushType()) + " with alpha 0.9");
        
        // Create model matrix (identity since we position via uniform)
        OLMatrix4f modelMatrix = new OLMatrix4f();
        modelMatrix.identity();
        shader.loadModelMatrix(modelMatrix);
        
        // Bind geometry and render
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        
        shader.stop();
        
        // Restore OpenGL state
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        
        System.out.println("✅ BrushRenderer: Brush rendered successfully");
    }
    
    public void cleanUp() {
        if (initialized) {
            System.out.println("🧹 BrushRenderer: Cleaning up resources");
            
            if (vao != 0) {
                glDeleteVertexArrays(vao);
            }
            if (vbo != 0) {
                glDeleteBuffers(vbo);
            }
            if (ebo != 0) {
                glDeleteBuffers(ebo);
            }
            if (shader != null) {
                shader.cleanUp();
            }
            
            initialized = false;
            System.out.println("✅ BrushRenderer: Cleanup completed");
        }
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    private OLVector3f getBrushColor(BrushType brushType) {
        return switch (brushType) {
            case RAISE -> new OLVector3f(0.2f, 1.0f, 0.2f);     // Green
            case LOWER -> new OLVector3f(1.0f, 0.2f, 0.2f);     // Red
            case SMOOTH -> new OLVector3f(0.2f, 0.6f, 1.0f);    // Blue
            case FLATTEN -> new OLVector3f(1.0f, 1.0f, 0.2f);   // Yellow
            case NOISE -> new OLVector3f(1.0f, 0.6f, 0.2f);     // Orange
        };
    }
    
    private String getBrushColorName(BrushType brushType) {
        return switch (brushType) {
            case RAISE -> "Green";
            case LOWER -> "Red";
            case SMOOTH -> "Blue";
            case FLATTEN -> "Yellow";
            case NOISE -> "Orange";
        };
    }
}
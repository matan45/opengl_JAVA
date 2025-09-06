package app.ecs.systems;

import app.ecs.Entity;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.components.TransformComponent;
import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.OLVector4f;
import app.math.components.Camera;
import app.math.components.RayCast;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.BrushRenderer;
import app.renderer.terrain.sculpting.BrushSettings;
import app.renderer.terrain.sculpting.BrushType;
import app.renderer.terrain.sculpting.TerrainDataManager;
import app.utilities.logger.LogInfo;
import app.utilities.debug.TerrainDebug;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class SculptingSystem {
    private final List<Entity> sculptingEntities;
    private final Camera camera;
    private BrushRenderer brushRenderer;
    private boolean leftMousePressed = false;
    private boolean rightMousePressed = false;
    private boolean shiftPressed = false;
    private boolean ctrlPressed = false;
    private boolean altPressed = false;
    private float mouseWheelDelta = 0.0f;
    private float mouseX = 0.0f;
    private float mouseY = 0.0f;

    public SculptingSystem(Camera camera) {
        this.sculptingEntities = new ArrayList<>();
        this.camera = camera;
        this.brushRenderer = new BrushRenderer();
        this.brushRenderer.initialize();
        TerrainDebug.println("SculptingSystem initialized with 3D brush renderer");
    }

    public void registerEntity(Entity entity) {
        if (entity.hasComponent(TerrainSculptingComponent.class) && 
            entity.hasComponent(TerrainComponent.class)) {
            if (!sculptingEntities.contains(entity)) {
                sculptingEntities.add(entity);
                TerrainDebug.printf("Registered entity for sculpting: %s", entity.getName());
            }
        }
    }

    public void unregisterEntity(Entity entity) {
        sculptingEntities.remove(entity);
    }

    public void update(float deltaTime, float viewportWidth, float viewportHeight) {
        if (sculptingEntities.isEmpty()) {
            // Only print this occasionally to avoid spam
            if (leftMousePressed) {
                TerrainDebug.println("❌ SculptingSystem: No sculpting entities registered");
            }
            return;
        }

        updateInputState();
        processMouseWheel();

        int activeEntities = 0;
        if (leftMousePressed) {
            TerrainDebug.println("🔍 SculptingSystem: Checking " + sculptingEntities.size() + " entities, leftMousePressed: " + leftMousePressed);
        }
        
        for (Entity entity : sculptingEntities) {
            TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
            if (sculptingComponent == null || !sculptingComponent.isActive()) {
                if (leftMousePressed) {
                    TerrainDebug.println("⚠️ Entity " + entity.getName() + " - sculptingComponent: " + (sculptingComponent != null ? "exists but inactive" : "null"));
                }
                continue;
            }
            
            activeEntities++;
            sculptingComponent.update(deltaTime);
            
            if (leftMousePressed) {
                TerrainDebug.println("🎯 Processing sculpting for entity: " + entity.getName());
                processSculpting(entity, sculptingComponent, deltaTime, viewportWidth, viewportHeight);
            } else {
                sculptingComponent.setCurrentlySculpting(false);
            }
        }
        
        if (activeEntities > 0 && leftMousePressed) {
            TerrainDebug.println("📊 Sculpting update - Active entities: " + activeEntities + ", Left mouse: " + leftMousePressed);
        }
    }

    private void updateInputState() {
        shiftPressed = glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS ||
                      glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
        ctrlPressed = glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS ||
                     glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS;
        altPressed = glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_LEFT_ALT) == GLFW_PRESS ||
                    glfwGetKey(glfwGetCurrentContext(), GLFW_KEY_RIGHT_ALT) == GLFW_PRESS;
    }

    private void processMouseWheel() {
        if (mouseWheelDelta != 0 && !sculptingEntities.isEmpty()) {
            for (Entity entity : sculptingEntities) {
                TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
                if (sculptingComponent != null && sculptingComponent.isActive()) {
                    BrushSettings brush = sculptingComponent.getBrushSettings();
                    
                    if (ctrlPressed) {
                        float newStrength = brush.getStrength() + mouseWheelDelta * 0.05f;
                        brush.setStrength(Math.max(0.01f, Math.min(2.0f, newStrength)));
                    } else if (altPressed) {
                        float newFalloff = brush.getFalloff() + mouseWheelDelta * 0.05f;
                        brush.setFalloff(Math.max(0.1f, Math.min(1.0f, newFalloff)));
                    } else {
                        float newSize = brush.getSize() + mouseWheelDelta * 5.0f;
                        brush.setSize(Math.max(1.0f, Math.min(200.0f, newSize)));
                    }
                }
            }
            mouseWheelDelta = 0;
        }
    }

    private void processSculpting(Entity entity, TerrainSculptingComponent sculptingComponent, float deltaTime, float viewportWidth, float viewportHeight) {
        TerrainDebug.println("🔧 Starting sculpting process for: " + entity.getName());
        
        TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        
        if (terrainComponent == null) {
            TerrainDebug.println("❌ No TerrainComponent found on entity: " + entity.getName());
            return;
        }
        if (transformComponent == null) {
            TerrainDebug.println("❌ No TransformComponent found on entity: " + entity.getName());
            return;
        }
        
        TerrainDebug.println("✅ Components found - proceeding with terrain intersection");

        TerrainIntersection intersection = calculateTerrainIntersection(entity, viewportWidth, viewportHeight);
        if (intersection == null) {
            TerrainDebug.println("❌ No terrain intersection found - mouse not over terrain");
            sculptingComponent.setCurrentlySculpting(false);
            return;
        }
        
        TerrainDebug.println("🎯 Terrain intersection at: " + intersection.worldPosition.x + ", " + intersection.worldPosition.z);

        BrushSettings brush = sculptingComponent.getBrushSettings();
        BrushType operation = determineOperation(brush.getBrushType());
        TerrainDebug.println("🖌️ Using brush: " + operation + " with size: " + brush.getSize() + ", strength: " + brush.getStrength());

        TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
        if (dataManager != null) {
            TerrainDebug.println("💾 Applying brush modification to terrain data");
            dataManager.applyBrushModification(
                intersection.worldPosition.x, 
                intersection.worldPosition.z, 
                brush, 
                operation, 
                deltaTime
            );
            
            sculptingComponent.setLastSculptPosition(intersection.worldPosition);
            sculptingComponent.setCurrentlySculpting(true);
            sculptingComponent.incrementModifications();
            TerrainDebug.println("✅ Sculpting operation completed - modifications count: " + sculptingComponent.getModificationsCount());
        } else {
            TerrainDebug.println("❌ TerrainDataManager is null - cannot apply modifications");
        }
    }

    private TerrainIntersection calculateTerrainIntersection(Entity terrainEntity, float viewportWidth, float viewportHeight) {
        TransformComponent transform = terrainEntity.getComponent(TransformComponent.class);
        TerrainComponent terrainComponent = terrainEntity.getComponent(TerrainComponent.class);
        if (transform == null || terrainComponent == null) {
            TerrainDebug.println("❌ Missing required components for terrain intersection");
            return null;
        }

        // Check for invalid mouse coordinates (set when mouse is outside viewport)
        if (mouseX < 0 || mouseY < 0) {
            TerrainDebug.printf("⚠️ Skipping sculpting - mouse outside viewport: (%.1f, %.1f)", mouseX, mouseY);
            return null;
        }
        
        // Calculate ray from camera through mouse cursor position
        OLVector3f rayOrigin = camera.getPosition();
        OLVector3f rayDirection = calculateMouseRay(viewportWidth, viewportHeight);
        
        if (rayDirection == null) {
            TerrainDebug.println("❌ Failed to calculate mouse ray");
            return null;
        }
        
        TerrainDebug.printf("Mouse position: (%.1f, %.1f) in viewport (%.1f x %.1f)", mouseX, mouseY, viewportWidth, viewportHeight);
        TerrainDebug.printf("Ray origin: %s", rayOrigin.toString());
        TerrainDebug.printf("Ray direction: %s", rayDirection.toString());

        OLVector3f terrainPosition = transform.getOlTransform().getPosition();
        
        // TerrainDebug.printf("SculptingSystem Terrain Position: %s", terrainPosition.toString());
        
        if (Math.abs(rayDirection.y) < 0.0001f) {
            TerrainDebug.println("❌ Ray direction too horizontal for intersection");
            return null;
        }
        
        // Check if camera direction is pointing toward terrain (removed overly restrictive validation)
        float cameraY = rayOrigin.y;
        float terrainY = terrainPosition.y;
        
        TerrainDebug.printf("Camera Y: %.1f, Terrain Y: %.1f, Camera Direction Y: %.6f", cameraY, terrainY, rayDirection.y);
        
        // Let the ray marching algorithm handle intersection calculation regardless of camera position
        // This allows for more flexible camera positioning and terrain interaction
        
        // Use iterative ray-terrain intersection with heightmap sampling
        TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
        if (dataManager == null) {
            // Fallback to flat plane intersection
            return calculateFlatPlaneIntersection(rayOrigin, rayDirection, terrainPosition);
        }
        
        // Ray marching approach - step along the ray and sample terrain height
        float stepSize = 5.0f; // Smaller step size for better accuracy
        float maxDistance = 5000.0f; // Increased maximum ray distance
        
        // TerrainDebug.printf("Starting ray march - stepSize: %.1f, maxDistance: %.1f", stepSize, maxDistance);
        
        int samplesInBounds = 0;
        int totalSamples = 0;
        
        for (float distance = 1.0f; distance < maxDistance; distance += stepSize) {
            totalSamples++;
            
            // Calculate current ray position
            OLVector3f rayPos = new OLVector3f(
                rayOrigin.x + rayDirection.x * distance,
                rayOrigin.y + rayDirection.y * distance,
                rayOrigin.z + rayDirection.z * distance
            );
            
            // Transform to local terrain coordinates (matching quadtree center-based system)
            float terrainSize = 2048.0f;
            
            // Convert from world space to texture coordinates
            // Quadtree spans (-1024 to +1024) in world space, map to (0 to 2048) in texture space
            // Try inverting Z-axis to fix opposite position issue
            float localX = terrainSize - ((rayPos.x - terrainPosition.x) + (terrainSize * 0.5f));
            float localZ = terrainSize - ((rayPos.z - terrainPosition.z) + (terrainSize * 0.5f));
            
            TerrainDebug.printf("🎯 Coordinate Mapping: world(%.1f, %.1f) -> local(%.1f, %.1f)", 
                rayPos.x - terrainPosition.x, rayPos.z - terrainPosition.z, localX, localZ);
            
            // Check if we're within terrain bounds
            if (localX < 0 || localX > terrainSize || localZ < 0 || localZ > terrainSize) {
                continue;
            }
            
            samplesInBounds++;
            
            // Sample terrain height at this position (including displacement factor)
            float baseHeight = dataManager.getCurrentHeightAtPosition(localX, localZ);
            float terrainHeight = baseHeight * 200.0f; // Apply displacement factor
            
            // Check if ray has intersected the terrain
            // For flat terrain, use a more generous tolerance based on step size
            float tolerance = Math.max(20.0f, stepSize * 2.0f);
            
            if (rayPos.y <= terrainHeight + tolerance) {
                TerrainDebug.printf("✅ Ray intersection found - Local: (%.1f, %.1f), Height: %.1f (base: %.4f)", 
                    localX, localZ, terrainHeight, baseHeight);
                
                // Return the local coordinates for terrain modification
                OLVector3f localHitPoint = new OLVector3f(localX, terrainHeight, localZ);
                return new TerrainIntersection(localHitPoint);
            }
        }
        
        TerrainDebug.printf("❌ No intersection - Samples: %d/%d in bounds", samplesInBounds, totalSamples);
        
        TerrainDebug.println("❌ SculptingSystem: No terrain intersection found within range");
        return null;
    }
    
    private OLVector3f calculateMouseRay(float viewportWidth, float viewportHeight) {
        // Convert viewport-relative mouse coordinates to normalized device coordinates (-1 to 1)
        float normalizedX = (2.0f * mouseX) / viewportWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / viewportHeight; // Flip Y for OpenGL
        
        TerrainDebug.printf("Viewport mouse: (%.1f, %.1f) in (%.1f x %.1f)", mouseX, mouseY, viewportWidth, viewportHeight);
        TerrainDebug.printf("Mouse NDC: (%.3f, %.3f)", normalizedX, normalizedY);
        
        // Create clip coordinates (NDC with z = -1 for near plane)
        OLVector4f clipCoords = new OLVector4f(normalizedX, normalizedY, -1.0f, 1.0f);
        
        // Transform to eye coordinates by inverting projection matrix
        OLMatrix4f projectionMatrix = camera.getProjectionMatrix();
        OLMatrix4f invProjection = projectionMatrix.invert();
        OLVector4f eyeCoords = invProjection.transform(clipCoords);
        eyeCoords.z = -1.0f; // Point forward
        eyeCoords.w = 0.0f;  // Direction vector (not position)
        
        // Transform to world coordinates by inverting view matrix
        OLMatrix4f viewMatrix = camera.getViewMatrix();
        OLMatrix4f invView = viewMatrix.invert();
        OLVector4f worldCoords = invView.transform(eyeCoords);
        
        // Extract and normalize direction vector
        OLVector3f rayDirection = new OLVector3f(worldCoords.x, worldCoords.y, worldCoords.z);
        rayDirection.normalize();
        
        return rayDirection;
    }
    
    private TerrainIntersection calculateFlatPlaneIntersection(OLVector3f rayOrigin, OLVector3f rayDirection, OLVector3f terrainPosition) {
        float terrainY = terrainPosition.y;
        float t = (terrainY - rayOrigin.y) / rayDirection.y;
        
        TerrainDebug.printf("Fallback to flat plane - Ray parameter t: %.2f", t);
        if (t < 0) {
            TerrainDebug.println("❌ Ray intersection behind camera (t < 0)");
            return null;
        }

        OLVector3f hitPoint = new OLVector3f(
            rayOrigin.x + rayDirection.x * t,
            terrainY,
            rayOrigin.z + rayDirection.z * t
        );
        
        // Transform to local terrain coordinates
        float terrainSize = 8192.0f;
        float terrainCenterX = 4096.0f;
        float terrainCenterZ = 4096.0f;
        
        float localX = hitPoint.x - terrainPosition.x + terrainCenterX;
        float localZ = hitPoint.z - terrainPosition.z + terrainCenterZ;
        
        if (localX >= 0 && localX <= terrainSize && localZ >= 0 && localZ <= terrainSize) {
            TerrainDebug.println("✅ SculptingSystem: Flat plane intersection within bounds");
            OLVector3f localHitPoint = new OLVector3f(localX, terrainY, localZ);
            return new TerrainIntersection(localHitPoint);
        } else {
            TerrainDebug.println("❌ SculptingSystem: Flat plane intersection outside bounds");
            return null;
        }
    }

    private BrushType determineOperation(BrushType baseBrushType) {
        if (ctrlPressed) {
            return BrushType.SMOOTH;
        }
        if (shiftPressed) {
            return baseBrushType.getInverse();
        }
        return baseBrushType;
    }

    private TerrainDataManager getTerrainDataManager(TerrainComponent terrainComponent) {
        // CRITICAL FIX: Use the same data manager as the terrain renderer!
        TerrainQuadtreeRenderer terrain = terrainComponent.getTerrain();
        
        // Enable sculpting on the renderer to ensure it has a data manager
        terrain.enableSculpting();
        
        // Get the renderer's data manager (the one that actually gets bound to the shader)
        TerrainDataManager rendererDataManager = terrain.getTerrainDataManager();
        
        // Set it on the component so they're synchronized
        terrainComponent.setDataManager(rendererDataManager);
        
        TerrainDebug.println("🔗 Using renderer's TerrainDataManager - both component and renderer now share the same instance");
        return rendererDataManager;
    }

    public void onMouseButton(int button, int action, int mods) {
        TerrainDebug.println("🖱️ Mouse button event - Button: " + button + ", Action: " + action);
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            leftMousePressed = (action == GLFW_PRESS || action == GLFW_REPEAT);
            TerrainDebug.println("🖱️ Left mouse " + (leftMousePressed ? "PRESSED" : "RELEASED"));
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            rightMousePressed = (action == GLFW_PRESS || action == GLFW_REPEAT);
            TerrainDebug.println("🖱️ Right mouse " + (rightMousePressed ? "PRESSED" : "RELEASED"));
        }
    }

    public void onMouseScroll(double xOffset, double yOffset) {
        mouseWheelDelta = (float) yOffset;
    }
    
    public void onMouseMove(double xpos, double ypos) {
        // Temporarily disabled - using ViewPort coordinates instead
        // this.mouseX = (float) xpos;
        // this.mouseY = (float) ypos;
        TerrainDebug.printf("🔄 GLFW mouse move ignored: (%.1f, %.1f) - using ViewPort coordinates instead", xpos, ypos);
    }
    
    public void setViewportRelativeMousePosition(float viewportX, float viewportY) {
        this.mouseX = viewportX;
        this.mouseY = viewportY;
        TerrainDebug.printf("🎯 SculptingSystem received coordinates: (%.1f, %.1f)", viewportX, viewportY);
    }

    public void onKeyboard(int key, int scancode, int action, int mods) {
        if (!sculptingEntities.isEmpty()) {
            for (Entity entity : sculptingEntities) {
                TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
                if (sculptingComponent != null && sculptingComponent.isActive()) {
                    handleKeyboardShortcuts(key, action, sculptingComponent);
                }
            }
        }
    }

    private void handleKeyboardShortcuts(int key, int action, TerrainSculptingComponent sculptingComponent) {
        if (action == GLFW_PRESS) {
            BrushSettings brush = sculptingComponent.getBrushSettings();
            
            switch (key) {
                case GLFW_KEY_1 -> brush.setBrushType(BrushType.RAISE);
                case GLFW_KEY_2 -> brush.setBrushType(BrushType.LOWER);
                case GLFW_KEY_3 -> brush.setBrushType(BrushType.SMOOTH);
                case GLFW_KEY_4 -> brush.setBrushType(BrushType.FLATTEN);
                case GLFW_KEY_5 -> brush.setBrushType(BrushType.NOISE);
                case GLFW_KEY_Z -> {
                    if (ctrlPressed) {
                        undoLastAction();
                    }
                }
                case GLFW_KEY_Y -> {
                    if (ctrlPressed) {
                        redoLastAction();
                    }
                }
            }
        }
    }

    private void undoLastAction() {
        for (Entity entity : sculptingEntities) {
            TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
            if (terrainComponent != null) {
                TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
                if (dataManager != null && dataManager.canUndo()) {
                    dataManager.undo();
                    TerrainDebug.println("Undid terrain modification");
                }
            }
        }
    }

    private void redoLastAction() {
        for (Entity entity : sculptingEntities) {
            TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
            if (terrainComponent != null) {
                TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
                if (dataManager != null && dataManager.canRedo()) {
                    dataManager.redo();
                    TerrainDebug.println("Redid terrain modification");
                }
            }
        }
    }

    public void renderBrushPreview(float viewportWidth, float viewportHeight) {
        if (sculptingEntities.isEmpty()) return;

        for (Entity entity : sculptingEntities) {
            TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
            if (sculptingComponent == null || !sculptingComponent.isActive()) {
                continue;
            }

            BrushSettings brush = sculptingComponent.getBrushSettings();
            if (!brush.isShowPreview()) {
                continue;
            }

            TerrainIntersection intersection = calculateTerrainIntersection(entity, viewportWidth, viewportHeight);
            if (intersection != null) {
                renderBrushCircle(intersection.worldPosition, brush);
            }
        }
    }

    private void renderBrushCircle(OLVector3f position, BrushSettings brush) {
        if (brushRenderer != null && brushRenderer.isInitialized()) {
            // Get camera matrices for 3D rendering
            OLMatrix4f viewMatrix = camera.createViewMatrix();
            OLMatrix4f projectionMatrix = camera.getProjectionMatrix();
            
            // Render the 3D brush visualization
            brushRenderer.render(position, brush, viewMatrix, projectionMatrix);
        } else {
            // Fallback to console logging if renderer not available
            String colorName = getBrushColorName(brush.getBrushType());
            TerrainDebug.println("🖌️ BRUSH PREVIEW: " + colorName + " " + brush.getShape().getDisplayName() + " at (" + 
                              String.format("%.2f", position.x) + ", " + String.format("%.2f", position.z) + 
                              ") - Size: " + brush.getSize() + ", Strength: " + brush.getStrength());
        }
    }
    
    private String getBrushColorName(BrushType brushType) {
        return switch (brushType) {
            case RAISE -> "🟢 Green";
            case LOWER -> "🔴 Red";
            case SMOOTH -> "🔵 Blue";
            case FLATTEN -> "🟡 Yellow";
            case NOISE -> "🟣 Magenta";
        };
    }

    public List<Entity> getSculptingEntities() {
        return new ArrayList<>(sculptingEntities);
    }

    public void cleanUp() {
        sculptingEntities.clear();
        if (brushRenderer != null) {
            brushRenderer.cleanUp();
        }
        TerrainDebug.println("SculptingSystem cleaned up");
    }

    public static class TerrainIntersection {
        public final OLVector3f worldPosition;

        public TerrainIntersection(OLVector3f worldPosition) {
            this.worldPosition = worldPosition;
        }
    }
}
package app.ecs.systems;

import app.ecs.Entity;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.components.TransformComponent;
import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.math.components.RayCast;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.BrushRenderer;
import app.renderer.terrain.sculpting.BrushSettings;
import app.renderer.terrain.sculpting.BrushType;
import app.renderer.terrain.sculpting.TerrainDataManager;
import app.utilities.logger.LogInfo;

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

    public SculptingSystem(Camera camera) {
        this.sculptingEntities = new ArrayList<>();
        this.camera = camera;
        this.brushRenderer = new BrushRenderer();
        this.brushRenderer.initialize();
        System.out.println("SculptingSystem initialized with 3D brush renderer");
    }

    public void registerEntity(Entity entity) {
        if (entity.hasComponent(TerrainSculptingComponent.class) && 
            entity.hasComponent(TerrainComponent.class)) {
            if (!sculptingEntities.contains(entity)) {
                sculptingEntities.add(entity);
                System.out.println("Registered entity for sculpting: " + entity.getName());
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
                System.out.println("❌ SculptingSystem: No sculpting entities registered");
            }
            return;
        }

        updateInputState();
        processMouseWheel();

        int activeEntities = 0;
        if (leftMousePressed) {
            System.out.println("🔍 SculptingSystem: Checking " + sculptingEntities.size() + " entities, leftMousePressed: " + leftMousePressed);
        }
        
        for (Entity entity : sculptingEntities) {
            TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
            if (sculptingComponent == null || !sculptingComponent.isActive()) {
                if (leftMousePressed) {
                    System.out.println("⚠️ Entity " + entity.getName() + " - sculptingComponent: " + (sculptingComponent != null ? "exists but inactive" : "null"));
                }
                continue;
            }
            
            activeEntities++;
            sculptingComponent.update(deltaTime);
            
            if (leftMousePressed) {
                System.out.println("🎯 Processing sculpting for entity: " + entity.getName());
                processSculpting(entity, sculptingComponent, deltaTime, viewportWidth, viewportHeight);
            } else {
                sculptingComponent.setCurrentlySculpting(false);
            }
        }
        
        if (activeEntities > 0 && leftMousePressed) {
            System.out.println("📊 Sculpting update - Active entities: " + activeEntities + ", Left mouse: " + leftMousePressed);
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
        System.out.println("🔧 Starting sculpting process for: " + entity.getName());
        
        TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        
        if (terrainComponent == null) {
            System.out.println("❌ No TerrainComponent found on entity: " + entity.getName());
            return;
        }
        if (transformComponent == null) {
            System.out.println("❌ No TransformComponent found on entity: " + entity.getName());
            return;
        }
        
        System.out.println("✅ Components found - proceeding with terrain intersection");

        TerrainIntersection intersection = calculateTerrainIntersection(entity, viewportWidth, viewportHeight);
        if (intersection == null) {
            System.out.println("❌ No terrain intersection found - mouse not over terrain");
            sculptingComponent.setCurrentlySculpting(false);
            return;
        }
        
        System.out.println("🎯 Terrain intersection at: " + intersection.worldPosition.x + ", " + intersection.worldPosition.z);

        BrushSettings brush = sculptingComponent.getBrushSettings();
        BrushType operation = determineOperation(brush.getBrushType());
        System.out.println("🖌️ Using brush: " + operation + " with size: " + brush.getSize() + ", strength: " + brush.getStrength());

        TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
        if (dataManager != null) {
            System.out.println("💾 Applying brush modification to terrain data");
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
            System.out.println("✅ Sculpting operation completed - modifications count: " + sculptingComponent.getModificationsCount());
        } else {
            System.out.println("❌ TerrainDataManager is null - cannot apply modifications");
        }
    }

    private TerrainIntersection calculateTerrainIntersection(Entity terrainEntity, float viewportWidth, float viewportHeight) {
        TransformComponent transform = terrainEntity.getComponent(TransformComponent.class);
        if (transform == null) return null;

        OLVector3f rayDirection = RayCast.calculateMouseRay(viewportWidth, viewportHeight);
        OLVector3f rayOrigin = camera.getPosition();

        float terrainY = transform.getOlTransform().getPosition().y;
        
        if (Math.abs(rayDirection.y) < 0.001f) {
            return null;
        }
        
        float t = (terrainY - rayOrigin.y) / rayDirection.y;
        if (t < 0) return null;

        OLVector3f hitPoint = new OLVector3f(
            rayOrigin.x + rayDirection.x * t,
            terrainY,
            rayOrigin.z + rayDirection.z * t
        );

        float terrainSize = 8192.0f;
        if (hitPoint.x >= 0 && hitPoint.x <= terrainSize && hitPoint.z >= 0 && hitPoint.z <= terrainSize) {
            return new TerrainIntersection(hitPoint);
        }

        return null;
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
        
        System.out.println("🔗 Using renderer's TerrainDataManager - both component and renderer now share the same instance");
        return rendererDataManager;
    }

    public void onMouseButton(int button, int action, int mods) {
        System.out.println("🖱️ Mouse button event - Button: " + button + ", Action: " + action);
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            leftMousePressed = (action == GLFW_PRESS || action == GLFW_REPEAT);
            System.out.println("🖱️ Left mouse " + (leftMousePressed ? "PRESSED" : "RELEASED"));
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            rightMousePressed = (action == GLFW_PRESS || action == GLFW_REPEAT);
            System.out.println("🖱️ Right mouse " + (rightMousePressed ? "PRESSED" : "RELEASED"));
        }
    }

    public void onMouseScroll(double xOffset, double yOffset) {
        mouseWheelDelta = (float) yOffset;
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
                    System.out.println("Undid terrain modification");
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
                    System.out.println("Redid terrain modification");
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
            System.out.println("🖌️ BRUSH PREVIEW: " + colorName + " " + brush.getShape().getDisplayName() + " at (" + 
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
        System.out.println("SculptingSystem cleaned up");
    }

    public static class TerrainIntersection {
        public final OLVector3f worldPosition;

        public TerrainIntersection(OLVector3f worldPosition) {
            this.worldPosition = worldPosition;
        }
    }
}
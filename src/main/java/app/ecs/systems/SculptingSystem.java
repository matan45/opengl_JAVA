package app.ecs.systems;

import app.ecs.Entity;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.components.TransformComponent;
import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.math.OLVector4f;
import app.math.components.Camera;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.BrushRenderer;
import app.renderer.terrain.sculpting.BrushSettings;
import app.renderer.terrain.sculpting.BrushType;
import app.renderer.terrain.sculpting.TerrainDataManager;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class SculptingSystem {
    private final List<Entity> sculptingEntities;
    private final Camera camera;
    private final BrushRenderer brushRenderer;
    private boolean leftMousePressed = false;
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
    }

    public void registerEntity(Entity entity) {
        if (entity.hasComponent(TerrainSculptingComponent.class) &&
                entity.hasComponent(TerrainComponent.class)) {
            if (!sculptingEntities.contains(entity)) {
                sculptingEntities.add(entity);
            }
        }
    }

    public void update(float deltaTime, float viewportWidth, float viewportHeight) {
        if (sculptingEntities.isEmpty()) {
            return;
        }

        updateInputState();
        processMouseWheel();

        for (Entity entity : sculptingEntities) {
            TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
            if (sculptingComponent == null || !sculptingComponent.isActive()) {
                continue;
            }

            sculptingComponent.update(deltaTime);

            if (leftMousePressed) {
                processSculpting(entity, sculptingComponent, deltaTime, viewportWidth, viewportHeight);
            } else {
                sculptingComponent.setCurrentlySculpting(false);
            }
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
                        float newStrength = brush.getStrength() + mouseWheelDelta * 0.02f;
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

        TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

        if (terrainComponent == null) {
            return;
        }
        if (transformComponent == null) {
            return;
        }

        TerrainIntersection intersection = calculateTerrainIntersection(entity, viewportWidth, viewportHeight);
        if (intersection == null) {
            sculptingComponent.setCurrentlySculpting(false);
            return;
        }

        BrushSettings brush = sculptingComponent.getBrushSettings();
        BrushType operation = determineOperation(brush.getBrushType());

        TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
        if (dataManager != null) {
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
        }
    }

    private TerrainIntersection calculateTerrainIntersection(Entity terrainEntity, float viewportWidth, float viewportHeight) {
        TransformComponent transform = terrainEntity.getComponent(TransformComponent.class);
        TerrainComponent terrainComponent = terrainEntity.getComponent(TerrainComponent.class);
        if (transform == null || terrainComponent == null) {
            return null;
        }

        if (mouseX < 0 || mouseY < 0) {
            return null;
        }

        // Calculate ray from camera through mouse cursor position
        OLVector3f rayOrigin = camera.getPosition();
        OLVector3f rayDirection = calculateMouseRay(viewportWidth, viewportHeight);

        OLVector3f terrainPosition = transform.getOlTransform().getPosition();

        if (Math.abs(rayDirection.y) < 0.0001f) {
            return null;
        }

        // Use iterative ray-terrain intersection with heightmap sampling
        TerrainDataManager dataManager = getTerrainDataManager(terrainComponent);
        if (dataManager == null) {
            return null;
        }

        // Ray marching approach - step along the ray and sample terrain height
        float stepSize = 0.1f; // Smaller step size for better accuracy
        float maxDistance = 1024.0f; // Increased maximum ray distance


        for (float distance = 1.0f; distance < maxDistance; distance += stepSize) {

            // Calculate current ray position
            OLVector3f rayPos = new OLVector3f(
                    rayOrigin.x + rayDirection.x * distance,
                    rayOrigin.y + rayDirection.y * distance,
                    rayOrigin.z + rayDirection.z * distance
            );

            // Transform to local terrain coordinates (matching quadtree center-based system)
            float terrainSize = 2048.0f;

            float localX = terrainSize - ((rayPos.x - terrainPosition.x) + (terrainSize * 0.5f));
            float localZ = terrainSize - ((rayPos.z - terrainPosition.z) + (terrainSize * 0.5f));


            // Check if we're within terrain bounds
            if (localX < 0 || localX > terrainSize || localZ < 0 || localZ > terrainSize) {
                continue;
            }

            // Sample terrain height at this position (including displacement factor)
            float baseHeight = dataManager.getCurrentHeightAtPosition(localX, localZ);

            if (rayPos.y <= baseHeight) {
                // Return the local coordinates for terrain modification
                OLVector3f localHitPoint = new OLVector3f(localX, baseHeight, localZ);
                return new TerrainIntersection(localHitPoint);
            }
        }

        return null;
    }

    private OLVector3f calculateMouseRay(float viewportWidth, float viewportHeight) {
        // Convert viewport-relative mouse coordinates to normalized device coordinates (-1 to 1)
        float normalizedX = (2.0f * mouseX) / viewportWidth - 1.0f;
        float normalizedY = 1.0f - (2.0f * mouseY) / viewportHeight; // Flip Y for OpenGL


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

        if (t < 0) {
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
            OLVector3f localHitPoint = new OLVector3f(localX, terrainY, localZ);
            return new TerrainIntersection(localHitPoint);
        } else {
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

        return terrain.getTerrainDataManager();
    }

    public void onMouseButton(int button, int action, int mods) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            leftMousePressed = (action == GLFW_PRESS || action == GLFW_REPEAT);
        }
    }

    public void onMouseScroll(double xOffset, double yOffset) {
        mouseWheelDelta = (float) yOffset;
    }

    public void setViewportRelativeMousePosition(float viewportX, float viewportY) {
        this.mouseX = viewportX;
        this.mouseY = viewportY;
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
            // Render the 3D brush visualization
            brushRenderer.render(position, brush);
        }
    }

    public List<Entity> getSculptingEntities() {
        return new ArrayList<>(sculptingEntities);
    }

    public void cleanUp() {
        sculptingEntities.clear();
        if (brushRenderer != null) {
            brushRenderer.cleanUp();
        }
    }

    public record TerrainIntersection(OLVector3f worldPosition) {
    }
}
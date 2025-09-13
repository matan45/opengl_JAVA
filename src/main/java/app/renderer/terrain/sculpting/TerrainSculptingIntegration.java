package app.renderer.terrain.sculpting;

import app.ecs.Entity;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.systems.SculptingSystem;
import app.editor.imgui.ImguiLayerHandler;
import app.editor.imgui.TerrainSculptingWindow;
import app.math.components.Camera;
import app.utilities.logger.LogInfo;

public class TerrainSculptingIntegration {
    private SculptingSystem sculptingSystem;
    private TerrainSculptingWindow sculptingWindow;
    private boolean isInitialized = false;

    public void initialize(Camera camera) {
        if (isInitialized) {
            return;
        }

        sculptingSystem = new SculptingSystem(camera);
        sculptingWindow = new TerrainSculptingWindow(sculptingSystem, null); // EntitySystem not needed in window

        isInitialized = true;
    }

    public void enableSculptingForTerrain(Entity terrainEntity) {
        if (!isInitialized) {
            LogInfo.println("ERROR: TerrainSculptingIntegration not initialized!");
            return;
        }

        if (!terrainEntity.hasComponent(TerrainComponent.class)) {
            LogInfo.println("ERROR: Entity does not have a TerrainComponent!");
            return;
        }

        // Add sculpting component if not already present
        if (!terrainEntity.hasComponent(TerrainSculptingComponent.class)) {
            TerrainSculptingComponent sculptingComponent = new TerrainSculptingComponent(terrainEntity);
            terrainEntity.addComponent(sculptingComponent);
            LogInfo.println("Added TerrainSculptingComponent to entity: " + terrainEntity.getName());
        }

        // Register entity with sculpting system
        sculptingSystem.registerEntity(terrainEntity);

        // Enable sculpting UI
        if (sculptingWindow != null && !sculptingWindow.isOpen()) {
            sculptingWindow.setOpen(true);
            ImguiLayerHandler.addLayer(sculptingWindow);
        }

        LogInfo.println("Enabled sculpting for terrain entity: " + terrainEntity.getName());
    }


    public void update(float deltaTime, float viewportWidth, float viewportHeight) {
        if (!isInitialized || sculptingSystem == null) return;

        sculptingSystem.update(deltaTime, viewportWidth, viewportHeight);
    }

    public void renderBrushPreview(float viewportWidth, float viewportHeight) {
        if (!isInitialized || sculptingSystem == null) return;

        sculptingSystem.renderBrushPreview(viewportWidth, viewportHeight);
    }

    public void handleMouseButton(int button, int action, int mods) {
        if (!isInitialized || sculptingSystem == null) return;

        sculptingSystem.onMouseButton(button, action, mods);
    }

    public void handleMouseScroll(double xOffset, double yOffset) {
        if (!isInitialized || sculptingSystem == null) return;

        sculptingSystem.onMouseScroll(xOffset, yOffset);
    }

    public void handleKeyboard(int key, int scancode, int action, int mods) {
        if (!isInitialized || sculptingSystem == null) return;

        sculptingSystem.onKeyboard(key, scancode, action, mods);
    }

    public SculptingSystem getSculptingSystem() {
        return sculptingSystem;
    }


    public boolean isInitialized() {
        return isInitialized;
    }

    public void cleanUp() {
        if (sculptingSystem != null) {
            sculptingSystem.cleanUp();
        }

        if (sculptingWindow != null) {
            sculptingWindow.setOpen(false);
        }

        isInitialized = false;
    }
}
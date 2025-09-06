package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.components.TransformComponent;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.math.components.RayCast;
import app.renderer.Textures;
import app.renderer.terrain.sculpting.BrushSettings;
import app.renderer.terrain.sculpting.BrushType;
import app.renderer.terrain.sculpting.TerrainDataManager;
import app.renderer.draw.EditorRenderer;
import app.utilities.logger.LogInfo;
import app.utilities.debug.TerrainDebug;
import app.utilities.serialize.Serializable;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.*;

public class ViewPort implements ImguiLayer {
    private final Inspector inspector;
    private Entity preEntity;
    private TransformComponent component;
    private int currentGizmoOperation;
    private final float[] inputVectorTranslation;
    private final float[] inputVectorScale;
    private final float[] inputVectorRotation;
    private final float[] inputSapValue;
    private boolean snap;
    private float snapValue;
    private float preWindowWidth;
    private float preWindowHeight;
    private float aspect;
    private Camera editorCamera;
    private boolean firstFrame = true;
    private final int scaleIcon;
    private final int rotateIcon;
    private final int translateIcon;
    private final int cancelIcon;
    private final int playIcon;
    private final int stopIcon;
    private final int gridIcon;
    private boolean isGrid;
    private float[] objectMatrices;
    private float xLastPos;
    private float yLastPos;
    private boolean isFirst = false;

    public ViewPort() {
        preEntity = new Entity();
        inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);

        currentGizmoOperation = -1;

        inputVectorTranslation = new float[3];
        inputVectorScale = new float[3];
        inputVectorRotation = new float[3];

        inputSapValue = new float[3];
        snap = false;

        preWindowWidth = 0;
        preWindowHeight = 0;
        aspect = 0;

        isGrid = true;
        EditorRenderer.getGrid().setRender(true);

        Textures textures = EditorRenderer.getTextures();
        scaleIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\scale.png"));
        rotateIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\rotate.png"));
        translateIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\translate.png"));
        playIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\play.png"));
        stopIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\stop.png"));
        cancelIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\cancel.png"));
        gridIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\viewPort\\grid.png"));

    }

    @Override
    public void render(float dt) {
        if (ImGui.begin("Scene View")) {

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.image(EditorRenderer.getTexturesID(), windowSize.x, windowSize.y - 50, 0, 1, 1, 0);

            // Get image bounds
            ImVec2 imagePos = ImGui.getItemRectMin();

            // Create a child window for overlay toolbar
            ImGui.setCursorScreenPos(imagePos.x, imagePos.y);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 5, 5);
            ImGui.pushStyleColor(ImGuiCol.ChildBg, 0, 0, 0, 0f);

            if (ImGui.beginChild("OverlayToolbar", 300, 33, false,
                    ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse)) {

                // Make buttons transparent
                ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.1f, 0.1f, 0.5f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.3f, 0.3f, 0.8f);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.4f, 0.4f, 0.4f, 0.9f);

                // Render buttons horizontally
                if (ImGui.imageButton(playIcon, 25, 25))
                    LogInfo.println("not implement");
                ImGui.sameLine();

                if (ImGui.imageButton(stopIcon, 25, 25))
                    LogInfo.println("not implement");
                ImGui.sameLine();

                ImGui.separator();
                ImGui.sameLine();

                if (ImGui.imageButton(translateIcon, 25, 25)) {
                    currentGizmoOperation = Operation.TRANSLATE;
                    snapValue = 0.5f;
                }
                ImGui.sameLine();

                if (ImGui.imageButton(rotateIcon, 25, 25)) {
                    currentGizmoOperation = Operation.ROTATE;
                    snapValue = 45.0f;
                }
                ImGui.sameLine();

                if (ImGui.imageButton(scaleIcon, 25, 25)) {
                    currentGizmoOperation = Operation.SCALE;
                    snapValue = 0.5f;
                }
                ImGui.sameLine();

                if (ImGui.imageButton(cancelIcon, 25, 25)) {
                    currentGizmoOperation = -1;
                    snapValue = 0f;
                }
                ImGui.sameLine();

                if (ImGui.imageButton(gridIcon, 25, 25)) {
                    isGrid = !isGrid;
                    EditorRenderer.getGrid().setRender(isGrid);
                }

                ImGui.popStyleColor(3);
            }
            ImGui.endChild();

            ImGui.popStyleColor();
            ImGui.popStyleVar();

            dragAndDropTargetEntity();
            //Gizmos
            //TODO for mouse picking need to fined for select entity
            Entity entity = inspector.getEntity();

            if (ImGui.isWindowFocused()) {
                keyInputImGuizo();
                cameraInput(dt);
                
                // Update sculpting system with viewport-relative mouse coordinates
                updateSculptingMouseCoordinates();

                if (80 < ImGui.getMousePos().y && ImGui.getMousePos().y < windowSize.y) {
                    if (ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_1)) {
                        // Debug viewport and ray information
                        TerrainDebug.printSeparator("VIEWPORT DEBUG INFO");
                        TerrainDebug.printf("Image Size: %.1f x %.1f", imagePos.x, imagePos.y);
                        TerrainDebug.printf("Viewport Size (corrected): %.1f x %.1f", getViewportWidth(), getViewportHeight());
                        TerrainDebug.printf("Mouse Position: %.1f, %.1f", ImGui.getMousePos().x, ImGui.getMousePos().y);
                        TerrainDebug.printf("Camera Position: %s", editorCamera.getPosition().toString());
                        
                        // Debug matrices
                        float[] viewMatrix = editorCamera.createViewMatrix().getAsArray();
                        float[] projMatrix = editorCamera.getProjectionMatrix().getAsArray();
                        TerrainDebug.println("View Matrix:");
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", viewMatrix[0], viewMatrix[4], viewMatrix[8], viewMatrix[12]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", viewMatrix[1], viewMatrix[5], viewMatrix[9], viewMatrix[13]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", viewMatrix[2], viewMatrix[6], viewMatrix[10], viewMatrix[14]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", viewMatrix[3], viewMatrix[7], viewMatrix[11], viewMatrix[15]);
                        
                        TerrainDebug.println("Projection Matrix:");
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", projMatrix[0], projMatrix[4], projMatrix[8], projMatrix[12]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", projMatrix[1], projMatrix[5], projMatrix[9], projMatrix[13]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", projMatrix[2], projMatrix[6], projMatrix[10], projMatrix[14]);
                        TerrainDebug.printf("[%.3f %.3f %.3f %.3f]", projMatrix[3], projMatrix[7], projMatrix[11], projMatrix[15]);
                        
                        // Ray cast with both viewport sizes for comparison
                        OLVector3f rayDir1 = RayCast.calculateMouseRay(windowSize.x, windowSize.y);
                        OLVector3f rayDir2 = RayCast.calculateMouseRay(getViewportWidth(), getViewportHeight());
                        
                        TerrainDebug.println("Ray Cast Results:");
                        TerrainDebug.printf("Ray Direction (full window): %s", rayDir1.toString());
                        TerrainDebug.printf("Ray Direction (corrected viewport): %s", rayDir2.toString());
                    }
                    
                    // Handle terrain sculpting in ViewPort (DISABLED - using SculptingSystem instead)
                    // handleTerrainSculpting(windowSize);
                }
            }

            if (firstFrame) {
                editorCamera = EditorRenderer.getEditorCamera();
                firstFrame = false;
            }

            if (preWindowHeight != ImGui.getWindowHeight() || preWindowWidth != ImGui.getWindowWidth()) {
                aspect = ImGui.getWindowWidth() / ImGui.getWindowHeight();
                editorCamera.setAspect(aspect);
                editorCamera.createPerspectiveMatrix(70);
                preWindowWidth = ImGui.getWindowWidth();
                preWindowHeight = ImGui.getWindowHeight();
                editorCamera.setViewPort(new OLVector2f(preWindowWidth, preWindowHeight));
            }

            ImGuizmo.setOrthographic(false);
            ImGuizmo.setAllowAxisFlip(false);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

            float[] inputViewMatrix = editorCamera.createViewMatrix().getAsArray();

            if (entity != null && currentGizmoOperation != -1) {

                if (!entity.equals(preEntity)) {
                    component = entity.getComponent(TransformComponent.class);
                    objectMatrices = component.getOlTransform().getModelMatrix().getAsArray();
                    preEntity = entity;
                }

                inputSapValue[0] = snapValue;
                inputSapValue[1] = snapValue;
                inputSapValue[2] = snapValue;

                float[] cameraProjection = editorCamera.getProjectionMatrix().getAsArray();
                if (snap)
                    ImGuizmo.manipulate(inputViewMatrix, cameraProjection, objectMatrices, currentGizmoOperation, Mode.LOCAL, inputSapValue);
                else
                    ImGuizmo.manipulate(inputViewMatrix, cameraProjection, objectMatrices, currentGizmoOperation, Mode.LOCAL);

                if (ImGuizmo.isUsing()) {
                    //from model matrix need to set scale translate rotation
                    ImGuizmo.decomposeMatrixToComponents(objectMatrices, inputVectorTranslation, inputVectorRotation, inputVectorScale);

                    OLVector3f position = component.getOlTransform().getPosition();
                    OLVector3f rotation = component.getOlTransform().getRotation();
                    OLVector3f scale = component.getOlTransform().getScale();

                    position.setOLVector3f(inputVectorTranslation[0], inputVectorTranslation[1], inputVectorTranslation[2]);
                    rotation.setOLVector3f(inputVectorRotation[0], inputVectorRotation[1], inputVectorRotation[2]);
                    scale.setOLVector3f(inputVectorScale[0], inputVectorScale[1], inputVectorScale[2]);

                } else
                    objectMatrices = component.getOlTransform().getModelMatrix().getAsArray();
            }

        }
        ImGui.end();
    }

    private void keyInputImGuizo() {
        if (ImGui.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            snap = true;
        else if (ImGui.isKeyReleased(GLFW_KEY_LEFT_CONTROL))
            snap = false;
            
        // Toggle terrain debug mode with F9 key
        if (ImGui.isKeyPressed(GLFW_KEY_F9)) {
            TerrainDebug.toggleDebug();
        }
    }

    private void dragAndDropTargetEntity() {
        if (ImGui.beginDragDropTarget()) {
            String payload = ImGui.acceptDragDropPayload(DragAndDrop.LOAD_ENTITY.getType());
            if (payload != null) {
                Entity entity = Serializable.loadEntity(Path.of(payload));
                assert entity != null;
                if (entity.getFather() != null)
                    EntitySystem.addEntity(entity.getFather());
                else
                    EntitySystem.addEntity(entity);
            }
            ImGui.endDragDropTarget();
        }
    }

    private void cameraInput(float dt) {
        if (editorCamera != null) {
            OLVector3f position = editorCamera.getPosition();
            OLVector3f rotation = editorCamera.getRotation();
            cameraMovement(position, rotation, dt, editorCamera.getSpeed());

            if (ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_2))
                isFirst = true;
            if (ImGui.isMouseDown(GLFW_MOUSE_BUTTON_2)) {
                ImGui.setMouseCursor(ImGuiMouseCursor.None);
                ImVec2 mousePos = ImGui.getMousePos();
                if (isFirst) {
                    xLastPos = mousePos.x;
                    yLastPos = mousePos.y;
                    isFirst = false;
                }
                float xOffset = mousePos.x - xLastPos;
                float yOffset = mousePos.y - yLastPos;
                rotation.y += xOffset * 0.1;
                rotation.x += yOffset * 0.1;

                if (rotation.y >= 360.0f || rotation.y <= -360.0f)
                    rotation.y = 0;
                xLastPos = mousePos.x;
                yLastPos = mousePos.y;
            }
        }
    }

    private void cameraMovement(OLVector3f position, OLVector3f rotation, float dt, float speed) {
        if (ImGui.isKeyDown(GLFW_KEY_W)) {
            position.x += (Math.sin(rotation.y / 180 * Math.PI)) * speed * dt;
            position.z -= (Math.cos(rotation.y / 180 * Math.PI)) * speed * dt;
        } else if (ImGui.isKeyDown(GLFW_KEY_A)) {
            position.x -= (Math.cos(rotation.y / 180 * Math.PI)) * speed * dt;
            position.z -= (Math.sin(rotation.y / 180 * Math.PI)) * speed * dt;
        } else if (ImGui.isKeyDown(GLFW_KEY_D)) {
            position.x += (Math.cos(rotation.y / 180 * Math.PI)) * speed * dt;
            position.z += (Math.sin(rotation.y / 180 * Math.PI)) * speed * dt;
        } else if (ImGui.isKeyDown(GLFW_KEY_S)) {
            position.x -= (Math.sin(rotation.y / 180 * Math.PI)) * speed * dt;
            position.z += (Math.cos(rotation.y / 180 * Math.PI)) * speed * dt;
        } else if (ImGui.isKeyDown(GLFW_KEY_E)) {
            position.y += -1 * speed * dt;
        } else if (ImGui.isKeyDown(GLFW_KEY_Q)) {
            position.y += 1 * speed * dt;
        }
    }

    public void setCurrentGizmoOperation(int currentGizmoOperation) {
        this.currentGizmoOperation = currentGizmoOperation;
        snapValue = 0;
    }
    
    public float getViewportWidth() {
        return preWindowWidth;
    }
    
    public float getViewportHeight() {
        return preWindowHeight - 50; // Subtract toolbar height
    }
    
    private void handleTerrainSculpting(ImVec2 windowSize) {
        if (!ImGui.isMouseDown(GLFW_MOUSE_BUTTON_LEFT)) {
            return;
        }
        
        // Find terrain entities with sculpting components
        for (Entity entity : EntitySystem.getEntitiesFather()) {
            if (!entity.hasComponent(TerrainSculptingComponent.class) || !entity.hasComponent(TerrainComponent.class)) {
                continue;
            }
            
            TerrainSculptingComponent sculptingComponent = entity.getComponent(TerrainSculptingComponent.class);
            if (!sculptingComponent.isActive()) {
                continue;
            }
            
            TerrainDebug.printSeparator("TERRAIN SCULPTING DEBUG");
            
            // Calculate terrain intersection using correct viewport coordinates (subtract toolbar height)
            float viewportWidth = getViewportWidth();
            float viewportHeight = getViewportHeight();
            TerrainDebug.printf("Using viewport dimensions: %.1f x %.1f", viewportWidth, viewportHeight);
            
            OLVector3f rayDirection = RayCast.calculateMouseRay(viewportWidth, viewportHeight);
            OLVector3f rayOrigin = editorCamera.getPosition();
            
            TerrainDebug.printf("Ray Origin: %s", rayOrigin.toString());
            TerrainDebug.printf("Ray Direction: %s", rayDirection.toString());
            
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            if (transform == null) {
                TerrainDebug.println("❌ No TransformComponent found on terrain entity");
                continue;
            }
            
            OLVector3f terrainPosition = transform.getOlTransform().getPosition();
            float terrainY = terrainPosition.y;
            
            TerrainDebug.printf("Terrain Transform Position: %s", terrainPosition.toString());
            TerrainDebug.printf("Terrain Y-plane: %.3f", terrainY);
            
            if (Math.abs(rayDirection.y) < 0.001f) {
                TerrainDebug.println("❌ Ray direction is nearly horizontal, no intersection possible");
                continue;
            }
            
            float t = (terrainY - rayOrigin.y) / rayDirection.y;
            TerrainDebug.printf("Ray parameter t: %.3f", t);
            
            if (t < 0) {
                TerrainDebug.println("❌ Ray intersection behind camera (t < 0)");
                continue;
            }
            
            OLVector3f hitPoint = new OLVector3f(
                rayOrigin.x + rayDirection.x * t,
                terrainY,
                rayOrigin.z + rayDirection.z * t
            );
            
            TerrainDebug.printf("Ray Hit Point: %s", hitPoint.toString());
            
            // Check if hit point is within terrain bounds (updated for 2048x2048 terrain)
            float terrainSize = 2048.0f;
            float terrainCenterX = 1024.0f; // Terrain center (WIDTH/2)
            float terrainCenterZ = 1024.0f; // Terrain center (LENGTH/2)
            
            // Transform hit point to terrain local coordinates
            float localX = hitPoint.x - terrainPosition.x + terrainCenterX;
            float localZ = hitPoint.z - terrainPosition.z + terrainCenterZ;
            
            TerrainDebug.printf("Terrain bounds: 0 to %.0f", terrainSize);
            TerrainDebug.printf("Hit point in terrain local space: (%.1f, %.1f)", localX, localZ);
            
            if (localX < 0 || localX > terrainSize || localZ < 0 || localZ > terrainSize) {
                TerrainDebug.println("❌ Hit point outside terrain bounds");
                continue;
            }
            
            // Apply terrain modification using local coordinates
            BrushSettings brush = sculptingComponent.getBrushSettings();
            TerrainComponent terrainComponent = entity.getComponent(TerrainComponent.class);
            
            // Enable sculpting and get data manager
            terrainComponent.getTerrain().enableSculpting();
            TerrainDataManager dataManager = terrainComponent.getTerrain().getTerrainDataManager();
            
            if (dataManager != null) {
                TerrainDebug.printf("✅ Applying sculpting at local coordinates: (%.1f, %.1f)", localX, localZ);
                TerrainDebug.printf("Brush: %s, Size: %.1f, Strength: %.3f", 
                    brush.getBrushType(), brush.getSize(), brush.getStrength());
                
                dataManager.applyBrushModification(
                    localX, 
                    localZ, 
                    brush, 
                    brush.getBrushType(), 
                    0.016f
                );
                
                sculptingComponent.setCurrentlySculpting(true);
                sculptingComponent.incrementModifications();
            } else {
                TerrainDebug.println("❌ TerrainDataManager is null");
            }
        }
    }
    
    private void updateSculptingMouseCoordinates() {
        if (EditorRenderer.getSculptingIntegration() == null) {
            System.out.println("❌ SculptingIntegration is null");
            return;
        }
        if (!EditorRenderer.getSculptingIntegration().isInitialized()) {
            System.out.println("❌ SculptingIntegration not initialized");
            return;
        }
        System.out.println("✅ SculptingIntegration available and initialized");
        
        // Get current mouse position (ImGui.getMousePos() gives window-relative coordinates)
        ImVec2 mousePos = ImGui.getMousePos();
        ImVec2 windowSize = ImGui.getWindowSize();
        
        // The image rendering uses: ImGui.image(EditorRenderer.getTexturesID(), windowSize.x, windowSize.y - 50, 0, 1, 1, 0);
        // So the image occupies the full window width and (windowSize.y - 50) height, starting 50px from the top
        
        // Convert to viewport-relative coordinates (same as existing ViewPort logic)
        float viewportMouseX = mousePos.x;  // Full window width
        float viewportMouseY = mousePos.y - 50;  // Subtract toolbar height (50px)
        
        // Image dimensions
        float imageWidth = windowSize.x;
        float imageHeight = windowSize.y - 50;
        
        // Check if mouse is actually inside the viewport bounds
        if (viewportMouseX < 0 || viewportMouseX >= imageWidth || 
            viewportMouseY < 0 || viewportMouseY >= imageHeight) {
            TerrainDebug.printf("⚠️ Mouse outside viewport bounds: (%.1f, %.1f) not in (0,0)-(%.1f,%.1f)", 
                viewportMouseX, viewportMouseY, imageWidth, imageHeight);
            // Set invalid coordinates to prevent sculpting when mouse is outside viewport
            EditorRenderer.getSculptingIntegration().getSculptingSystem().setViewportRelativeMousePosition(-1, -1);
            return; 
        }
        
        // Mouse is inside viewport, no need to clamp
        
        // Update the sculpting system with viewport-relative coordinates
        EditorRenderer.getSculptingIntegration().getSculptingSystem().setViewportRelativeMousePosition(viewportMouseX, viewportMouseY);
        
        // Debug output to verify coordinate calculation
        TerrainDebug.printf("🖱️ ViewPort mouse: raw(%.1f,%.1f) -> viewport(%.1f,%.1f) in (%.1f x %.1f)", 
            mousePos.x, mousePos.y, viewportMouseX, viewportMouseY, imageWidth, imageHeight);
    }
}
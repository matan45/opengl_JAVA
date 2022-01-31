package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.components.TransformComponent;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.*;

public class ViewPort implements ImguiLayer {
    Inspector inspector;
    Entity preEntity;
    TransformComponent component;
    int currentGizmoOperation;

    final float[] inputVectorTranslation;
    final float[] inputVectorScale;
    final float[] inputVectorRotation;

    float[] inputSapValue;
    boolean snap;
    float snapValue;

    float preWindowWidth;
    float preWindowHeight;
    float[] cameraProjection;
    float aspect;

    Camera editorCamera;
    float speed = 0.2f;
    boolean isViewChange = false;

    boolean firstFrame = true;

    int scaleIcon;
    int rotateIcon;
    int translateIcon;
    int cancelIcon;
    int playIcon;
    int stopIcon;

    float[] objectMatrices = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    final float[] gridMatrix = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    float[] inputViewMatrix = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    Textures textures;

    float xLastPos;
    float yLastPos;
    boolean isFirst = false;

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

        textures = EditorRenderer.getTextures();
        scaleIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\scale.png");
        rotateIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\rotate.png");
        translateIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\translate.png");
        playIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\play.png");
        stopIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\stop.png");
        cancelIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\viewPort\\cancel.png");

    }

    @Override
    public void render() {
        if (ImGui.begin("Scene View", ImGuiWindowFlags.MenuBar)) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 255);
            if (ImGui.beginMenuBar()) {
                if (ImGui.imageButton(playIcon, 30, 20))
                    LogInfo.println("not implement");
                else if (ImGui.imageButton(stopIcon, 30, 20))
                    LogInfo.println("not implement");
                else if (ImGui.imageButton(translateIcon, 30, 20)) {
                    currentGizmoOperation = Operation.TRANSLATE;
                    snapValue = 0.5f;
                } else if (ImGui.imageButton(rotateIcon, 30, 20)) {
                    currentGizmoOperation = Operation.ROTATE;
                    snapValue = 45.0f;
                } else if (ImGui.imageButton(scaleIcon, 30, 20)) {
                    currentGizmoOperation = Operation.SCALE;
                    snapValue = 0.5f;
                } else if (ImGui.imageButton(cancelIcon, 30, 20)) {
                    currentGizmoOperation = -1;
                    snapValue = 0f;
                }
            }
            ImGui.endMenuBar();
            ImGui.popStyleColor();

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.image(EditorRenderer.getTexturesID(), windowSize.x, windowSize.y - 80, 0, 1, 1, 0);
            //Gizmos
            //TODO for mouse picking need to fined for select entity
            Entity entity = inspector.getEntity();

            if (ImGui.isWindowFocused()) {
                keyInputImGuizo();
                cameraInput();
            }

            if (firstFrame) {
                editorCamera = EditorRenderer.getEditorCamera();
                inputViewMatrix = editorCamera.createViewMatrix().getAsArray();
                firstFrame = false;
            }

            if (preWindowHeight != ImGui.getWindowHeight() || preWindowWidth != ImGui.getWindowWidth()) {
                aspect = ImGui.getWindowWidth() / ImGui.getWindowHeight();
                cameraProjection = editorCamera.createPerspectiveMatrix(30, aspect, 0.1f, 100f).getAsArray();
                preWindowWidth = ImGui.getWindowWidth();
                preWindowHeight = ImGui.getWindowHeight();
            }

            ImGuizmo.setOrthographic(false);
            ImGuizmo.setAllowAxisFlip(false);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

            ImGuizmo.drawGrid(inputViewMatrix, cameraProjection, gridMatrix, 10);

            if (entity != null && currentGizmoOperation != -1) {

                if (!entity.equals(preEntity)) {
                    component = entity.getComponent(TransformComponent.class);
                    objectMatrices = component.getOlTransform().getModelMatrix().getAsArray();
                    preEntity = entity;
                }

                inputSapValue[0] = snapValue;
                inputSapValue[1] = snapValue;
                inputSapValue[2] = snapValue;

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

    public float getAspect() {
        return aspect;
    }

    private void keyInputImGuizo() {
        if (ImGui.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            snap = true;
        else if (ImGui.isKeyReleased(GLFW_KEY_LEFT_CONTROL))
            snap = false;
    }

    private void cameraInput() {
        if (editorCamera != null) {
            OLVector3f position = editorCamera.getPosition();
            OLVector3f rotation = editorCamera.getRotation();
            cameraMovement(position, rotation);

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

                if (rotation.y > 89.0f)
                    rotation.y = 89.0f;
                if (rotation.y < -89.0f)
                    rotation.y = -89.0f;
                xLastPos = mousePos.x;
                yLastPos = mousePos.y;

                isViewChange = true;
            }

            if (isViewChange) {
                inputViewMatrix = editorCamera.createViewMatrix().getAsArray();
                isViewChange = false;
            }
        }
    }

    private void cameraMovement(OLVector3f position, OLVector3f rotation) {
        if (ImGui.isKeyDown(GLFW_KEY_W)) {
            position.x += (Math.sin(rotation.y / 180 * Math.PI)) * speed;
            position.z -= (Math.cos(rotation.y / 180 * Math.PI)) * speed;
            isViewChange = true;
        } else if (ImGui.isKeyDown(GLFW_KEY_A)) {
            position.x -= (Math.cos(rotation.y / 180 * Math.PI)) * speed;
            position.z -= (Math.sin(rotation.y / 180 * Math.PI)) * speed;
            isViewChange = true;
        } else if (ImGui.isKeyDown(GLFW_KEY_D)) {
            position.x += (Math.cos(rotation.y / 180 * Math.PI)) * speed;
            position.z += (Math.sin(rotation.y / 180 * Math.PI)) * speed;
            isViewChange = true;
        } else if (ImGui.isKeyDown(GLFW_KEY_S)) {
            position.x -= (Math.sin(rotation.y / 180 * Math.PI)) * speed;
            position.z += (Math.cos(rotation.y / 180 * Math.PI)) * speed;
            isViewChange = true;
        } else if (ImGui.isKeyDown(GLFW_KEY_E)) {
            position.y += -1 * speed;
            isViewChange = true;
            if (position.y < -360)
                position.y = 0;
        } else if (ImGui.isKeyDown(GLFW_KEY_Q)) {
            position.y += 1 * speed;
            isViewChange = true;
            if (position.y > 360)
                position.y = 0;
        }
    }

}

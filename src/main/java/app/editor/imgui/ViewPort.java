package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.components.TransformComponent;
import app.math.OLVector3f;
import app.math.components.Camera;
import app.renderer.draw.EditorRenderer;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
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

    float[] objectMatrices =
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    0.f, 0.f, 0.f, 1.f
            };
    static final float[] gridMatrix = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    float speed = 0.2f;
    boolean isChange = false;
    //TODO swap it for camera view
    private float[] INPUT_CAMERA_VIEW = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };

    private static boolean firstFrame = true;


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
    }

    @Override
    public void render() {
        if (ImGui.begin("Scene View", ImGuiWindowFlags.MenuBar)) {

            if (ImGui.beginMenuBar()) {
                if (ImGui.menuItem("Play"))
                    LogInfo.println("not implement");
                if (ImGui.menuItem("Stop"))
                    LogInfo.println("not implement");
            }
            ImGui.endMenuBar();

            ImVec2 windowSize = ImGui.getWindowSize();
            ImGui.image(EditorRenderer.getFramebuffer().getTextureId(), windowSize.x, windowSize.y - 80, 0, 1, 1, 0);
            //Gizmos
            //TODO for mouse picking need to fined for select entity
            Entity entity = inspector.getEntity();

            if (ImGui.isWindowFocused()) {
                keyInputImGuizo();
                cameraInput();
            }

            if (firstFrame) {
                editorCamera = EditorRenderer.getEditorCamera();
                INPUT_CAMERA_VIEW = editorCamera.createViewMatrix().getAsArray();
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
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight() - 80);

            ImGuizmo.drawGrid(INPUT_CAMERA_VIEW, cameraProjection, gridMatrix, 10);

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
                    ImGuizmo.manipulate(INPUT_CAMERA_VIEW, cameraProjection, objectMatrices, currentGizmoOperation, Mode.LOCAL, inputSapValue);
                else
                    ImGuizmo.manipulate(INPUT_CAMERA_VIEW, cameraProjection, objectMatrices, currentGizmoOperation, Mode.LOCAL);

                if (ImGuizmo.isUsing()) {
                    //from model matrix need to set scale translate rotation
                    ImGuizmo.decomposeMatrixToComponents(objectMatrices, inputVectorTranslation, inputVectorRotation, inputVectorScale);

                    OLVector3f position = component.getOlTransform().getPosition();
                    OLVector3f rotation = component.getOlTransform().getRotation();
                    OLVector3f scale = component.getOlTransform().getScale();

                    position.x = inputVectorTranslation[0];
                    position.y = inputVectorTranslation[1];
                    position.z = inputVectorTranslation[2];

                    scale.x = inputVectorScale[0];
                    scale.y = inputVectorScale[1];
                    scale.z = inputVectorScale[2];

                    rotation.x = inputVectorRotation[0];
                    rotation.y = inputVectorRotation[1];
                    rotation.z = inputVectorRotation[2];
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
        if (ImGui.isKeyPressed(GLFW_KEY_T)) {
            currentGizmoOperation = Operation.TRANSLATE;
            snapValue = 0.5f;
        } else if (ImGui.isKeyPressed(GLFW_KEY_R)) {
            currentGizmoOperation = Operation.ROTATE;
            snapValue = 45.0f;
        } else if (ImGui.isKeyPressed(GLFW_KEY_S)) {
            currentGizmoOperation = Operation.SCALE;
            snapValue = 0.5f;
        } else if (ImGui.isKeyPressed(GLFW_KEY_Q)) {
            currentGizmoOperation = -1;
            snapValue = 0f;
        } else if (ImGui.isKeyPressed(GLFW_KEY_LEFT_CONTROL))
            snap = true;
        else if (ImGui.isKeyReleased(GLFW_KEY_LEFT_CONTROL))
            snap = false;
    }

    private void cameraInput() {
        if (editorCamera != null) {
            OLVector3f position = editorCamera.getPosition();
            OLVector3f rotation = editorCamera.getRotation();
            if (ImGui.isKeyPressed(GLFW_KEY_KP_8)) {
                position.x += (Math.sin(rotation.y / 180 * Math.PI)) * speed;
                position.z -= (Math.cos(rotation.y / 180 * Math.PI)) * speed;
                isChange = true;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_4)) {
                position.x -= (Math.cos(rotation.y / 180 * Math.PI)) * speed;
                position.z -= (Math.sin(rotation.y / 180 * Math.PI)) * speed;
                isChange = true;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_6)) {
                position.x += (Math.cos(rotation.y / 180 * Math.PI)) * speed;
                position.z += (Math.sin(rotation.y / 180 * Math.PI)) * speed;
                isChange = true;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_5)) {
                position.x -= (Math.sin(rotation.y / 180 * Math.PI)) * speed;
                position.z += (Math.cos(rotation.y / 180 * Math.PI)) * speed;
                isChange = true;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_7)) {
                position.y += -1 * speed;
                isChange = true;
                if (position.y < -360)
                    position.y = 0;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_9)) {
                position.y += 1 * speed;
                isChange = true;
                if (position.y > 360)
                    position.y = 0;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_1)) {
                rotation.y += 1;
                isChange = true;
                if (rotation.y > 360)
                    rotation.y = 0;
            } else if (ImGui.isKeyPressed(GLFW_KEY_KP_3)) {
                rotation.y -= 1;
                isChange = true;
                if (rotation.y < -360)
                    rotation.y = 0;
            }
            float wheel = ImGui.getIO().getMouseWheel();
            if (wheel > 0) {
                isChange = true;
                rotation.x += 10.0f;
            } else if (wheel < 0) {
                isChange = true;
                rotation.x -= 10.0f;
            }
            if (isChange) {
                INPUT_CAMERA_VIEW = editorCamera.createViewMatrix().getAsArray();
                isChange = false;
            }
        }
    }

    @Override
    public void cleanUp() {

    }
}

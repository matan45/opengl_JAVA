package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.components.TransformComponent;
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
    //TODO swap it for camera view
    private static final float[] INPUT_CAMERA_VIEW = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    private static final float[][] OBJECT_MATRICES = {
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    0.f, 0.f, 0.f, 1.f
            }};


    public ViewPort() {
        this.inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);
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
            Entity entity = inspector.getEntity();
            if (entity != null) {
                //move to camera class
                float aspect = ImGui.getWindowWidth() / ImGui.getWindowHeight();
                float[] cameraProjection = perspective(27, aspect, 0.1f, 100f);

                LogInfo.println(String.valueOf(inputKey()));
                ImGuizmo.setOrthographic(false);
                ImGuizmo.setDrawList();
                ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());
                ImGuizmo.manipulate(INPUT_CAMERA_VIEW, cameraProjection, OBJECT_MATRICES[0], inputKey(), Mode.LOCAL);

                if (ImGuizmo.isUsing()) {
                    LogInfo.println("hit2");
                    //from model matrix need to set scale translate rotation
                    TransformComponent component = entity.getComponent(TransformComponent.class);
                    /*component.getOlTransform().setPosition();
                    component.getOlTransform().setScale();
                    component.getOlTransform().setRotation();*/
                }
            }
        }
        ImGui.end();
    }

    private int inputKey() {
        //need to set key callback
        if (ImGui.isKeyPressed(GLFW_KEY_T)) {
            return Operation.TRANSLATE;
        } else if (ImGui.isKeyPressed(GLFW_KEY_R)) {
            return Operation.ROTATE;
        } else if (ImGui.isKeyPressed(GLFW_KEY_S)) {
            return Operation.SCALE;
        }
        return -1;
    }

    @Override
    public void cleanUp() {

    }

    //TODO move to camera calss
    private static float[] perspective(float fovY, float aspect, float near, float far) {
        float ymax, xmax;
        ymax = (float) (near * Math.tan(fovY * Math.PI / 180.0f));
        xmax = ymax * aspect;
        return frustum(-xmax, xmax, -ymax, ymax, near, far);
    }

    private static float[] frustum(float left, float right, float bottom, float top, float near, float far) {
        float[] r = new float[16];
        float temp = 2.0f * near;
        float temp2 = right - left;
        float temp3 = top - bottom;
        float temp4 = far - near;
        r[0] = temp / temp2;
        r[1] = 0.0f;
        r[2] = 0.0f;
        r[3] = 0.0f;
        r[4] = 0.0f;
        r[5] = temp / temp3;
        r[6] = 0.0f;
        r[7] = 0.0f;
        r[8] = (right + left) / temp2;
        r[9] = (top + bottom) / temp3;
        r[10] = (-far - near) / temp4;
        r[11] = -1.0f;
        r[12] = 0.0f;
        r[13] = 0.0f;
        r[14] = (-temp * far) / temp4;
        r[15] = 0.0f;
        return r;
    }

}

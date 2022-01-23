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
    Entity preEntity = new Entity("temp");
    TransformComponent component;
    int currentGizmoOperation = -1;
    final float[] inputMatrixTranslation = new float[3];
    final float[] inputMatrixScale = new float[3];
    final float[] inputMatrixRotation = new float[3];
    float[] objectMatrices =
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    0.f, 0.f, 0.f, 1.f
            };

    //TODO swap it for camera view
    private static final float[] INPUT_CAMERA_VIEW = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    private static final float FLT_EPSILON = 1.19209290E-07f;
    private static final int CAM_DISTANCE = 8;
    private static final float CAM_Y_ANGLE = 360.f / 180.f * (float) Math.PI;
    private static final float CAM_X_ANGLE = 30.f / 180.f * (float) Math.PI;
    private static final float[] IDENTITY_MATRIX = {
            1.f, 0.f, 0.f, 0.f,
            0.f, 1.f, 0.f, 0.f,
            0.f, 0.f, 1.f, 0.f,
            0.f, 0.f, 0.f, 1.f
    };
    private static boolean firstFrame = true;


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

            if (ImGui.isWindowFocused()) {
                if (ImGui.isKeyPressed(GLFW_KEY_T))
                    currentGizmoOperation = Operation.TRANSLATE;
                else if (ImGui.isKeyPressed(GLFW_KEY_R))
                    currentGizmoOperation = Operation.ROTATE;
                else if (ImGui.isKeyPressed(GLFW_KEY_S))
                    currentGizmoOperation = Operation.SCALE;
                else if (ImGui.isKeyPressed(GLFW_KEY_Q))
                    currentGizmoOperation = -1;
            }
            //TODO move to camera class
            if (firstFrame) {
                float[] eye = new float[]{
                        (float) (Math.cos(CAM_Y_ANGLE) * Math.cos(CAM_X_ANGLE) * CAM_DISTANCE),
                        (float) (Math.sin(CAM_X_ANGLE) * CAM_DISTANCE),
                        (float) (Math.sin(CAM_Y_ANGLE) * Math.cos(CAM_X_ANGLE) * CAM_DISTANCE)
                };
                float[] at = new float[]{0.f, 0.f, 0.f};
                float[] up = new float[]{0.f, 1.f, 0.f};
                lookAt(eye, at, up, INPUT_CAMERA_VIEW);
                firstFrame = false;
            }

            float aspect = ImGui.getWindowWidth() / ImGui.getWindowHeight();
            float[] cameraProjection = perspective(30, aspect, 0.1f, 100f);
            //
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setAllowAxisFlip(false);
            ImGuizmo.setDrawList();
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight());

            ImGuizmo.drawGrid(INPUT_CAMERA_VIEW, cameraProjection, IDENTITY_MATRIX, 10);

            if (entity != null && currentGizmoOperation != -1) {

                if (!entity.equals(preEntity)) {
                    component = entity.getComponent(TransformComponent.class);
                    objectMatrices = component.getOlTransform().getModelMatrix().getAsArray();
                    preEntity = entity;
                }

                ImGuizmo.manipulate(INPUT_CAMERA_VIEW, cameraProjection, objectMatrices, currentGizmoOperation, Mode.LOCAL);

                if (ImGuizmo.isUsing()) {
                    //from model matrix need to set scale translate rotation
                    ImGuizmo.decomposeMatrixToComponents(objectMatrices, inputMatrixTranslation, inputMatrixRotation, inputMatrixScale);
                    component.getOlTransform().getPosition().x = inputMatrixTranslation[0];
                    component.getOlTransform().getPosition().y = inputMatrixTranslation[1];
                    component.getOlTransform().getPosition().z = inputMatrixTranslation[2];

                    component.getOlTransform().getScale().x = inputMatrixScale[0];
                    component.getOlTransform().getScale().y = inputMatrixScale[1];
                    component.getOlTransform().getScale().z = inputMatrixScale[2];

                    component.getOlTransform().getRotation().x = inputMatrixRotation[0];
                    component.getOlTransform().getRotation().y = inputMatrixRotation[1];
                    component.getOlTransform().getRotation().z = inputMatrixRotation[2];
                } else
                    objectMatrices = component.getOlTransform().getModelMatrix().getAsArray();
            }

        }
        ImGui.end();
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

    private static float dot(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static float[] normalize(float[] a) {
        float[] r = new float[3];
        float il = (float) (1.f / (Math.sqrt(dot(a, a)) + FLT_EPSILON));
        r[0] = a[0] * il;
        r[1] = a[1] * il;
        r[2] = a[2] * il;
        return r;
    }

    private static float[] cross(float[] a, float[] b) {
        float[] r = new float[3];
        r[0] = a[1] * b[2] - a[2] * b[1];
        r[1] = a[2] * b[0] - a[0] * b[2];
        r[2] = a[0] * b[1] - a[1] * b[0];
        return r;
    }

    private static void lookAt(float[] eye, float[] at, float[] up, float[] m16) {
        float[] x;
        float[] y;
        float[] z;
        float[] tmp = new float[3];

        tmp[0] = eye[0] - at[0];
        tmp[1] = eye[1] - at[1];
        tmp[2] = eye[2] - at[2];
        z = normalize(tmp);
        y = normalize(up);

        tmp = cross(y, z);
        x = normalize(tmp);

        tmp = cross(z, x);
        y = normalize(tmp);

        m16[0] = x[0];
        m16[1] = y[0];
        m16[2] = z[0];
        m16[3] = 0.0f;
        m16[4] = x[1];
        m16[5] = y[1];
        m16[6] = z[1];
        m16[7] = 0.0f;
        m16[8] = x[2];
        m16[9] = y[2];
        m16[10] = z[2];
        m16[11] = 0.0f;
        m16[12] = -dot(x, eye);
        m16[13] = -dot(y, eye);
        m16[14] = -dot(z, eye);
        m16[15] = 1.0f;
    }

}

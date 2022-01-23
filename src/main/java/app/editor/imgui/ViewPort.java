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
    Entity preEntity;
    TransformComponent component;
    int currentGizmoOperation = -1;

    final float[] inputVectorTranslation = new float[3];
    final float[] inputVectorScale = new float[3];
    final float[] inputVectorRotation = new float[3];

    float[] inputSapValue = new float[3];
    boolean snap = false;
    float snapValue;

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
    private static final int CAM_DISTANCE = 20;
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
        preEntity = new Entity("temp");
        inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);
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
            //TODO for mouse picking need to find for select entity
            Entity entity = inspector.getEntity();

            if (ImGui.isWindowFocused()) {
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
            ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), ImGui.getWindowWidth(), ImGui.getWindowHeight() - 80);

            ImGuizmo.drawGrid(INPUT_CAMERA_VIEW, cameraProjection, IDENTITY_MATRIX, 10);

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

                    component.getOlTransform().getPosition().x = inputVectorTranslation[0];
                    component.getOlTransform().getPosition().y = inputVectorTranslation[1];
                    component.getOlTransform().getPosition().z = inputVectorTranslation[2];

                    component.getOlTransform().getScale().x = inputVectorScale[0];
                    component.getOlTransform().getScale().y = inputVectorScale[1];
                    component.getOlTransform().getScale().z = inputVectorScale[2];

                    component.getOlTransform().getRotation().x = inputVectorRotation[0];
                    component.getOlTransform().getRotation().y = inputVectorRotation[1];
                    component.getOlTransform().getRotation().z = inputVectorRotation[2];
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

package app.editor.component;

import app.math.components.Camera;
import imgui.ImGui;
import imgui.type.ImBoolean;

import static app.utilities.ImguiUtil.drawVector3;

public class CameraWindow {
    private Camera camera;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void cameraEditor(ImBoolean cameraWindow) {
        if (ImGui.begin("Camera Editor", cameraWindow)) {
            ImGui.pushID("Camera Speed");
            if (ImGui.button("speed"))
                camera.setSpeed(20f);
            ImGui.sameLine();
            float[] cameraValue = {camera.getSpeed()};
            ImGui.dragFloat("##Y", cameraValue, 0.5f, 0.5f, 100f);
            camera.setSpeed(cameraValue[0]);
            ImGui.popID();

            drawVector3("Position", camera.getPosition(), 0.0f, 25.0f, 51.0f);
            drawVector3("Rotation", camera.getRotation(), 34.0f, 0.0f, 0.0f);

            ImGui.textWrapped("Camera Perspective");
            ImGui.separator();
            ImGui.pushID("near");
            if (ImGui.button("Near"))
                camera.setNear(0.1f);
            ImGui.sameLine();
            float[] nearValue = {camera.getNear()};
            ImGui.dragFloat("##Y", nearValue, 0.1f, 0.1f, 10f);
            camera.setNear(nearValue[0]);
            ImGui.popID();

            ImGui.pushID("far");
            if (ImGui.button("Far"))
                camera.setFar(2048);
            ImGui.sameLine();
            float[] farValue = {camera.getFar()};
            ImGui.dragFloat("##Y", farValue, 1f, 0.0f, 4096f);
            camera.setFar(farValue[0]);
            ImGui.popID();

        }
        ImGui.end();
    }
}

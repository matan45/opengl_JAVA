package app.ecs.components;

import app.ecs.Component;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

public class TransformComponent implements Component {

    float[] X = new float[1];
    float[] Y = new float[1];
    float[] Z = new float[1];

    @Override
    public void update(float dt) {

    }

    @Override
    public void imguiDraw() {
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.button("X");
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        ImGui.dragFloat("##X", X, 0.1f);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.8f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.9f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.8f, 0.15f, 1.0f);
        ImGui.button("Y");
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        ImGui.dragFloat("##Y", Y, 0.1f);

        ImGui.pushStyleColor(ImGuiCol.Button, 0.15f, 0.1f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.2f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.15f, 0.8f, 1.0f);
        ImGui.button("Z");
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        ImGui.dragFloat("##Z", Z, 0.1f);

    }

    @Override
    public String getName() {
        return "Transform Component";
    }
}

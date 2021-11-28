package app.editor.imgui;

import imgui.ImGui;

public class Inspector implements ImguiLayer {
    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Inspector")) {
            ImGui.text("show ECS details");
        }
        ImGui.end();
    }
}

package app.editor.imgui;

import imgui.ImGui;

public class SceneGraph implements ImguiLayer {
    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Scene Graph")) {
            ImGui.button("test");
            ImGui.text("show the scene hierarchy");
        }
        ImGui.end();
    }
}

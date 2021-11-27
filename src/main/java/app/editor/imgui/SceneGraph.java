package app.editor.imgui;

import imgui.ImGui;

public class SceneGraph implements ImguiLayer {
    @Override
    public void render() {
        ImGui.begin("Scene Graph");
        ImGui.setWindowSize(200, 200);
        ImGui.text("tttt");
        ImGui.end();
    }
}

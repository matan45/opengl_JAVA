package app.editor.imgui;

import imgui.ImGui;

public class ContentWindow implements ImguiLayer {
    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Content Folder")) {
            ImGui.text("show Content Folder");
        }
        ImGui.end();
    }
}

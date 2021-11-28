package app.editor.imgui;

import imgui.ImGui;

public class LogWindow implements ImguiLayer {
    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Log")) {
            ImGui.textUnformatted("not implemented");
        }
        ImGui.end();
    }
}

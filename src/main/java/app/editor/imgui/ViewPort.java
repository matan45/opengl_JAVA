package app.editor.imgui;

import imgui.ImGui;

public class ViewPort implements ImguiLayer {
    @Override
    public void render() {
        if (ImGui.begin("Scene View")) {
            ImGui.text("here render opengl view port TODO");
            //ImGui.image();
        }
        ImGui.end();
    }
}

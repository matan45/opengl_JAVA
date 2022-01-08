package app.editor.imgui;

import app.renderer.Renderer;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class ViewPort implements ImguiLayer {
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
            ImGui.image(Renderer.getFramebuffer().getTextureId(), windowSize.x, windowSize.y, 0, 1, 1, 0);
        }
        ImGui.end();
    }
}

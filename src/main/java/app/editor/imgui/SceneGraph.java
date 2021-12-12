package app.editor.imgui;

import app.utilities.logger.LogInfo;
import imgui.ImGui;

public class SceneGraph implements ImguiLayer {
    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Scene Graph")) {
            ImGui.text("show the scene hierarchy");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Game Object")) {
                   LogInfo.println("TO DO add game object");
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }
}

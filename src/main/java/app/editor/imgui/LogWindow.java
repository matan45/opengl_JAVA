package app.editor.imgui;

import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class LogWindow implements ImguiLayer {
    double previousTime = 0;
    int frameCount = 0;
    int fps = 0;

    @Override
    public void render() {
        frameCount++;
        double currentTime = glfwGetTime();
        if (currentTime - previousTime >= 1.0) {
            fps = frameCount;
            frameCount = 0;
            previousTime = currentTime;
        }
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Debug")) {
            ImGui.text("FPS: " + fps);
            if (ImGui.beginChild("Log", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar)) {
                ImGui.pushStyleColor(ImGuiCol.Text, ImColor.intToColor(0, 255, 0));
                ImGui.textUnformatted("TODO implemented looger");
                ImGui.popStyleColor();
            }
            ImGui.endChild();
        }
        ImGui.end();
    }
}

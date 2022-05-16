package app.editor.imgui;

import app.utilities.logger.Logger;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class LogWindow implements ImguiLayer {
    private double previousTime = 0;
    private int frameCount = 0;
    private int fps = 0;

    @Override
    public void render(float dt) {
        frameCount++;
        double currentTime = glfwGetTime();
        if (currentTime - previousTime >= 1.0) {
            fps = frameCount;
            frameCount = 0;
            previousTime = currentTime;
        }

        if (ImGui.begin("Debug")) {
            if (ImGui.button("clear"))
                Logger.clear();
            ImGui.sameLine();
            ImGui.text("FPS: " + fps);
            ImGui.sameLine();
            ImGui.text("Delta Time: " + dt);
            ImGui.separator();
            ImGui.pushStyleColor(ImGuiCol.ChildBg, ImColor.intToColor(0, 0, 0));
            if (ImGui.beginChild("Log", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar)) {
                ImGui.pushStyleColor(ImGuiCol.Text, ImColor.intToColor(0, 255, 0));
                ImGui.textUnformatted(Logger.outputLog());
                ImGui.popStyleColor();
            }
            ImGui.popStyleColor();
            ImGui.endChild();
        }
        ImGui.end();
    }

}

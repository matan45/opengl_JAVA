package app.editor.imgui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.*;

public class MainImgui implements ImguiLayer {
    private boolean closeWindow = false;
    final int width;
    final int height;
    final String title;

    public MainImgui(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    @Override
    public void render() {
        ImGui.begin(title, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse);
        ImGui.setWindowSize(width, height);
        if (ImGui.button("close"))
            closeWindow = true;
        if (closeWindow) {
            final long backupWindowPtr = glfwGetCurrentContext();
            glfwSetWindowShouldClose(backupWindowPtr, true);
        }

        ImGui.end();
    }
}

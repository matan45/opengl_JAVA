package app.editor.imgui;

import app.editor.GlfwWindow;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainImgui implements ImguiLayer {
    private boolean closeWindow = false;
    final String title;
    int windowFlags;

    public MainImgui(String title) {
        this.title = title;
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.MenuBar;
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(GlfwWindow.WIDTH, GlfwWindow.HEIGHT);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin(title, windowFlags);
        ImGui.popStyleVar();

        ImGui.dockSpace(ImGui.getID("Dockspace"), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu(FontAwesomeIcons.File + " File")) {
                if (ImGui.menuItem("Exit", null, false)) {
                    closeWindow = true;
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        if (closeWindow) {
            final long backupWindowPtr = glfwGetCurrentContext();
            glfwSetWindowShouldClose(backupWindowPtr, true);
        }

        ImGui.end();
    }
}

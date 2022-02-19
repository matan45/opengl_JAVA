package app.editor.imgui;

import app.utilities.OpenFileDialog;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainImgui implements ImguiLayer {
    private final ImBoolean closeWindow;
    private final String title;
    private int windowFlags;
    private int width;
    private int height;

    static final String DOCK_SPACE = "Dockspace";

    public MainImgui(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        closeWindow = new ImBoolean(true);
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.MenuBar;
    }

    @Override
    public void render(float dt) {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(width, height);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        if (ImGui.begin(title, closeWindow, windowFlags)) {
            ImGui.popStyleVar();

            ImGui.dockSpace(ImGui.getID(DOCK_SPACE), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

            menuBar();

            if (!closeWindow.get()) {
                final long backupWindowPtr = glfwGetCurrentContext();
                glfwSetWindowShouldClose(backupWindowPtr, true);
            }
        }
        ImGui.end();
    }

    private void menuBar() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu(FontAwesomeIcons.File + " File")) {
                if (ImGui.menuItem(FontAwesomeIcons.Plus + " New", null, false)) {
                    OpenFileDialog.openFolder().ifPresent(LogInfo::println);
                } else if (ImGui.menuItem(FontAwesomeIcons.FolderOpen + " Open", null, false)) {
                    OpenFileDialog.openFile("png,jpg;pdf").ifPresent(LogInfo::println);
                } else if (ImGui.menuItem(FontAwesomeIcons.Save + " Save", null, false)) {
                    OpenFileDialog.save("png,jpg;pdf").ifPresent(LogInfo::println);
                } else if (ImGui.menuItem(FontAwesomeIcons.Outdent + " Exit", null, false)) {
                    closeWindow.set(false);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.Wrench + " Settings")) {
                if (ImGui.menuItem(FontAwesomeIcons.Camera + " Editor Camera", null, false)) {
                    LogInfo.println("not implemented");
                } else if (ImGui.menuItem(FontAwesomeIcons.LayerGroup + " Layout Style", null, false)) {
                    LogInfo.println("not implement");
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}

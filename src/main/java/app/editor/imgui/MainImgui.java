package app.editor.imgui;

import app.utilities.logger.LogError;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.*;

public class MainImgui implements ImguiLayer {
    private final ImBoolean closeWindow;
    final String title;
    int windowFlags;
    int width;
    int height;

    public MainImgui(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        closeWindow = new ImBoolean(true);
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.MenuBar;
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(width, height);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        if (ImGui.begin(title, closeWindow, windowFlags)) {
            ImGui.popStyleVar();

            ImGui.dockSpace(ImGui.getID("Dockspace"), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

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
                    openFolder();
                } else if (ImGui.menuItem(FontAwesomeIcons.FolderOpen + " Open", null, false)) {
                    openFile();
                } else if (ImGui.menuItem(FontAwesomeIcons.Save + " Save", null, false)) {
                    save();
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

    private void openFolder() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            checkResult(NFD_PickFolder((ByteBuffer) null, outPath), outPath);
        } finally {
            memFree(outPath);
        }
    }

    private void openFile() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            checkResult(NFD_OpenDialog("png,jpg;pdf", null, outPath), outPath);
        } finally {
            memFree(outPath);
        }
    }

    private void save() {
        PointerBuffer savePath = memAllocPointer(1);

        try {
            checkResult(NFD_SaveDialog("png,jpg;pdf", null, savePath), savePath);
        } finally {
            memFree(savePath);
        }
    }

    private void checkResult(int result, PointerBuffer path) {
        switch (result) {
            case NFD_OKAY -> {
                LogInfo.println("Success!");
                LogInfo.println(path.getStringUTF8(0));
                nNFD_Free(path.get(0));
            }
            case NFD_CANCEL -> LogInfo.println("User pressed cancel.");
            default -> // NFD_ERROR
                    LogError.println("Error: " + NFD_GetError());
        }
    }

}

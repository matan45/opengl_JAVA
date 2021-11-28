package app.editor.imgui;

import app.editor.GlfwWindow;
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

    public MainImgui(String title) {
        this.title = title;
        closeWindow = new ImBoolean(true);
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.MenuBar;
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(GlfwWindow.WIDTH, GlfwWindow.HEIGHT);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        if (ImGui.begin(title, closeWindow, windowFlags)) {
            ImGui.popStyleVar();

            ImGui.dockSpace(ImGui.getID("Dockspace"), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu(FontAwesomeIcons.File + " File")) {
                    if (ImGui.menuItem(FontAwesomeIcons.Plus + " New", null, false)) {
                        System.out.print("not implemented");
                    } else if (ImGui.menuItem(FontAwesomeIcons.FolderOpen + " Open", null, false)) {
                        openFolder();
                    } else if (ImGui.menuItem(FontAwesomeIcons.Save + " Save", null, false)) {
                        save();
                    } else if (ImGui.menuItem(FontAwesomeIcons.Outdent + " Exit", null, false)) {
                        closeWindow.set(false);
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }

            if (!closeWindow.get()) {
                final long backupWindowPtr = glfwGetCurrentContext();
                glfwSetWindowShouldClose(backupWindowPtr, true);
            }
        }
        ImGui.end();
    }

    private void openFolder() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            checkResult(NFD_PickFolder((ByteBuffer) null, outPath), outPath);
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
            case NFD_OKAY:
                System.out.println("Success!");
                System.out.println(path.getStringUTF8(0));
                nNFD_Free(path.get(0));
                break;
            case NFD_CANCEL:
                System.out.println("User pressed cancel.");
                break;
            default: // NFD_ERROR
                System.err.format("Error: %s\n", NFD_GetError());
        }
    }
}

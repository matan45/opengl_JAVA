package app.editor.imgui;

import app.renderer.pbr.Mesh;
import app.utilities.OpenFileDialog;
import app.utilities.logger.LogInfo;
import app.utilities.resource.ResourceManager;
import app.utilities.serialize.FileExtension;
import app.utilities.serialize.Serializable;
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

    private static final String DOCK_SPACE = "Dockspace";

    private final ImBoolean cameraWindow;

    public MainImgui(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        closeWindow = new ImBoolean(true);
        cameraWindow = new ImBoolean(false);
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
            if (cameraWindow.get())
                cameraEditor();
        }
        ImGui.end();
    }

    private void menuBar() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu(FontAwesomeIcons.File + " File")) {
                if (ImGui.menuItem(FontAwesomeIcons.Plus + " New Scene", "CTRL+N", false)) {
                    OpenFileDialog.openFolder().ifPresent(Serializable::saveEmptyScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.FolderOpen + " Open Scene", "CTRL+O", false)) {
                    OpenFileDialog.openFile(FileExtension.SCENE_EXTENSION.getFileName()).ifPresent(Serializable::loadScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.Save + " Save Scene", "CTRL+S", false)) {
                    OpenFileDialog.save(FileExtension.SCENE_EXTENSION.getFileName()).ifPresent(Serializable::saveScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.Outdent + " Exit", "EXIT", false)) {
                    closeWindow.set(false);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.Wrench + " Settings")) {
                if (ImGui.menuItem(FontAwesomeIcons.Camera + " Editor Camera", null, false)) {
                    cameraWindow.set(!cameraWindow.get());
                } else if (ImGui.menuItem(FontAwesomeIcons.LayerGroup + " Layout Style", null, false)) {
                    LogInfo.println("not implement");
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.Tools + " Import")) {
                if (ImGui.menuItem(FontAwesomeIcons.FileImport + " Meshes", null, false)) {
                    //TODO thread pool
                    OpenFileDialog.openMulti("obj,fbx,dae,gltf").parallelStream().forEach(path -> {
                        Mesh[] meshes = ResourceManager.loadMeshesFromFile(path);
                        for (Mesh mesh : meshes)
                            Serializable.saveMesh(mesh, path);
                    });
                } else if (ImGui.menuItem(FontAwesomeIcons.FileImage + " Textures", null, false)) {
                    LogInfo.println("not implement");
                } else if (ImGui.menuItem(FontAwesomeIcons.FileAudio + " Audio", null, false)) {
                    LogInfo.println("not implement");
                } else if (ImGui.menuItem(FontAwesomeIcons.Walking + " Animation", null, false)) {
                    LogInfo.println("not implement");
                }
                ImGui.endMenu();

            }
            ImGui.endMenuBar();
        }
    }

    private void cameraEditor() {
        if (ImGui.begin("Camera Editor", cameraWindow)) {

        }
        ImGui.end();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}

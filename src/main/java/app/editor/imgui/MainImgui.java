package app.editor.imgui;

import app.editor.component.CameraWindow;
import app.editor.component.StyleWindow;
import app.renderer.draw.EditorRenderer;
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
    private final ImBoolean cameraWindowBoolean;
    private final CameraWindow cameraEditor;
    private final StyleWindow styleWindow;
    private final ImBoolean styleWindowBoolean;

    public MainImgui(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        closeWindow = new ImBoolean(true);
        cameraWindowBoolean = new ImBoolean(false);
        styleWindowBoolean = new ImBoolean(false);
        cameraEditor = new CameraWindow();
        styleWindow = new StyleWindow();
        windowFlags |= ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.MenuBar;
        styleWindow.theme();
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
            if (cameraWindowBoolean.get())
                cameraEditor.cameraEditor(cameraWindowBoolean);
            if (styleWindowBoolean.get())
                styleWindow.showStyleWindow(styleWindowBoolean);
        }
        ImGui.end();
    }

    private void menuBar() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu(FontAwesomeIcons.FILE + " File")) {
                if (ImGui.menuItem(FontAwesomeIcons.PLUS + " New Scene", "CTRL+N", false)) {
                    OpenFileDialog.openFolder().ifPresent(Serializable::saveEmptyScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.FOLDER_OPEN + " Open Scene", "CTRL+O", false)) {
                    OpenFileDialog.openFile(FileExtension.SCENE_EXTENSION.getFileName(), "Scene").ifPresent(Serializable::loadScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.SAVE + " Save Scene", "CTRL+S", false)) {
                    OpenFileDialog.save(FileExtension.SCENE_EXTENSION.getFileName(), "Scene").ifPresent(Serializable::saveScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.OUT_DENT + " Exit", "EXIT", false)) {
                    closeWindow.set(false);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.WRENCH + " Settings")) {
                if (ImGui.menuItem(FontAwesomeIcons.CAMERA + " Editor Camera", null, false)) {
                    cameraEditor.setCamera(EditorRenderer.getEditorCamera());
                    cameraWindowBoolean.set(!cameraWindowBoolean.get());
                } else if (ImGui.menuItem(FontAwesomeIcons.LAYER_GROUP + " Layout Style", null, false)) {
                    styleWindowBoolean.set(!styleWindowBoolean.get());
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.TOOLS + " Import")) {
                if (ImGui.menuItem(FontAwesomeIcons.FILE_IMPORT + " Meshes", null, false)) {
                    //TODO thread pool
                    OpenFileDialog.openMulti("obj,fbx,dae,gltf", "Meshes").parallelStream().forEach(path -> {
                        Mesh[] meshes = ResourceManager.loadMeshesFromFile(path);
                        for (Mesh mesh : meshes)
                            Serializable.saveMesh(mesh, path);
                    });
                } else if (ImGui.menuItem(FontAwesomeIcons.FILE_IMAGE + " Textures", null, false)) {
                    LogInfo.println("not implement");
                } else if (ImGui.menuItem(FontAwesomeIcons.FILE_AUDIO + " Audio", null, false)) {
                    LogInfo.println("not implement");
                } else if (ImGui.menuItem(FontAwesomeIcons.WALKING + " Animation", null, false)) {
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

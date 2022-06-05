package app.editor.imgui;

import app.math.components.Camera;
import app.renderer.draw.EditorRenderer;
import app.renderer.texture.Image;
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

import java.util.Arrays;

import static app.utilities.ImguiUtil.drawVector3;
import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class MainImgui implements ImguiLayer {
    private final ImBoolean closeWindow;
    private final String title;
    private int windowFlags;
    private int width;
    private int height;

    private Camera camera;

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
            if (ImGui.beginMenu(FontAwesomeIcons.FILE + " File")) {
                if (ImGui.menuItem(FontAwesomeIcons.PLUS + " New Scene", "CTRL+N", false)) {
                    OpenFileDialog.openFolder().ifPresent(Serializable::saveEmptyScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.FOLDER_OPEN + " Open Scene", "CTRL+O", false)) {
                    OpenFileDialog.openFile(FileExtension.SCENE_EXTENSION.getFileName()).ifPresent(Serializable::loadScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.SAVE + " Save Scene", "CTRL+S", false)) {
                    OpenFileDialog.save(FileExtension.SCENE_EXTENSION.getFileName()).ifPresent(Serializable::saveScene);
                } else if (ImGui.menuItem(FontAwesomeIcons.OUT_DENT + " Exit", "EXIT", false)) {
                    closeWindow.set(false);
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.WRENCH + " Settings")) {
                if (ImGui.menuItem(FontAwesomeIcons.CAMERA + " Editor Camera", null, false)) {
                    camera = EditorRenderer.getEditorCamera();
                    cameraWindow.set(!cameraWindow.get());
                } else if (ImGui.menuItem(FontAwesomeIcons.LAYER_GROUP + " Layout Style", null, false)) {
                    LogInfo.println("not implement");
                }
                ImGui.endMenu();
            }
            if (ImGui.beginMenu(FontAwesomeIcons.TOOLS + " Import")) {
                if (ImGui.menuItem(FontAwesomeIcons.FILE_IMPORT + " Meshes", null, false)) {
                    //TODO thread pool
                    OpenFileDialog.openMulti("obj,fbx,dae,gltf").parallelStream().forEach(path ->
                            Arrays.stream(ResourceManager.loadMeshesFromFile(path)).forEach(mesh -> Serializable.saveMesh(mesh, path)));

                } else if (ImGui.menuItem(FontAwesomeIcons.FILE_IMAGE + " Textures", null, false)) {
                    OpenFileDialog.openMulti("hdr,png,tga,jpg").parallelStream().forEach(path -> {
                        Image image = ResourceManager.loadTextureFromFile(path);
                        Serializable.saveTexture(image, path);
                    });
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

    private void cameraEditor() {
        if (ImGui.begin("Camera Editor", cameraWindow)) {
            ImGui.pushID("Camera Speed");
            if (ImGui.button("speed"))
                camera.setSpeed(20f);
            ImGui.sameLine();
            float[] cameraValue = {camera.getSpeed()};
            ImGui.dragFloat("##Y", cameraValue, 0.5f, 0.5f, 100f);
            camera.setSpeed(cameraValue[0]);
            ImGui.popID();

            drawVector3("Position", camera.getPosition(), 0.0f, 25.0f, 51.0f);
            drawVector3("Rotation", camera.getRotation(), 34.0f, 0.0f, 0.0f);

            ImGui.textWrapped("Camera Perspective");
            ImGui.separator();
            ImGui.pushID("near");
            if (ImGui.button("Near"))
                camera.setNear(0.1f);
            ImGui.sameLine();
            float[] nearValue = {camera.getNear()};
            ImGui.dragFloat("##Y", nearValue, 0.1f, 0.1f, 10f);
            camera.setNear(nearValue[0]);
            ImGui.popID();

            ImGui.pushID("far");
            if (ImGui.button("Far"))
                camera.setFar(2048);
            ImGui.sameLine();
            float[] farValue = {camera.getFar()};
            ImGui.dragFloat("##Y", farValue, 1f, 0.0f, 4096f);
            camera.setFar(farValue[0]);
            ImGui.popID();

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

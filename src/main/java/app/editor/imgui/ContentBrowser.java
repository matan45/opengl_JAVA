package app.editor.imgui;

import app.ecs.Entity;
import app.editor.component.SceneHandler;
import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import app.utilities.serialize.FileExtension;
import app.utilities.serialize.Serializable;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class ContentBrowser implements ImguiLayer {
    private Path absolutePath;

    private final int folderIcon;
    private final int fileIcon;

    private static final String FOLDER_SPLITTER = "\\";

    public ContentBrowser() {
        Textures textures = EditorRenderer.getTextures();
        //TODO: icons for mesh prefabe and scene
        folderIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-folder.png");
        fileIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-file.png");
        absolutePath = SceneHandler.getActiveScene().getPath();
    }

    @Override
    public void render(float dt) {
        if (ImGui.begin("Content Folder")) {
            dragAndDropTargetEntity();
            if (ImGui.button("<--") && absolutePath.getParent() != null) {
                absolutePath = absolutePath.getParent();
                SceneHandler.getActiveScene().setPath(absolutePath);
            }
            ImGui.sameLine();
            ImGui.labelText("Current Path", absolutePath.toString());
            ImGui.separator();

            File folder = absolutePath.toFile();
            File[] listOfFiles = folder.listFiles();

            float panelWidth = ImGui.getContentRegionAvailX();
            float thumbnailSize = 128.0f;
            float padding = 16.0f;
            float cellSize = padding + thumbnailSize;
            int columnCount = (int) (panelWidth / cellSize);
            ImGui.columns(columnCount, "", false);
            assert listOfFiles != null;

            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            for (File file : listOfFiles) {
                fileType(thumbnailSize, file);
            }
            ImGui.popStyleColor();
        }
        ImGui.columns(1);
        ImGui.end();
    }

    private void fileType(float thumbnailSize, File file) {
        if (file.isFile()) {
            ImGui.pushID(file.getName());
            ImGui.imageButton(fileIcon, thumbnailSize, thumbnailSize);
            if (ImGui.isMouseDragging(GLFW_MOUSE_BUTTON_1))
                dragAndDropSourceEntity(file.toPath().toAbsolutePath().toString());
            ImGui.textWrapped(file.getName());
            ImGui.popID();
            ImGui.nextColumn();
        } else if (file.isDirectory()) {
            ImGui.pushID(file.getName());
            if (ImGui.imageButton(folderIcon, thumbnailSize, thumbnailSize))
                absolutePath = Paths.get(absolutePath + FOLDER_SPLITTER + file.getName());
            ImGui.textWrapped(file.getName());
            ImGui.popID();
            ImGui.nextColumn();
        }
    }

    private void dragAndDropTargetEntity() {
        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload(DragAndDrop.SAVE_ENTITY.getType());
            if (payload != null && payload.getClass().isAssignableFrom(Entity.class)) {
                Entity entity = (Entity) payload;
                Serializable.saveEntity(entity, absolutePath.toString());
            }
            ImGui.endDragDropTarget();
        }
    }

    private void dragAndDropSourceEntity(String path) {
        Optional.ofNullable(path)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.lastIndexOf(".") + 1)).ifPresent(extension -> {
                    if (extension.equals(FileExtension.PREFAB_EXTENSION.getFileName()) && ImGui.beginDragDropSource()) {
                        ImGui.setDragDropPayload(DragAndDrop.LOAD_ENTITY.getType(), path, ImGuiCond.Once);
                        ImGui.text(path);
                        ImGui.endDragDropSource();
                    }
                });
    }

    public void setAbsolutePath(Path absolutePath) {
        this.absolutePath = absolutePath;
    }
}

package app.editor.imgui;

import app.ecs.Entity;
import app.editor.component.SceneHandler;
import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import app.utilities.FileUtil;
import app.utilities.serialize.FileExtension;
import app.utilities.serialize.Serializable;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class ContentBrowser implements ImguiLayer {
    private Path absolutePath;
    private final int folderIcon;
    private final int fileIcon;
    private final int meshIcon;
    private final int prefabIcon;
    private final int javaIcon;
    private final int sceneIcon;
    private final int shaderIcon;

    private static final float THUMBNAIL_SIZE = 96.0f;
    private static final float PADDING = 16.0f;

    public static final String FOLDER_SPLITTER = "\\";

    public ContentBrowser() {
        Textures textures = EditorRenderer.getTextures();
        //TODO: icons for texture and audio
        folderIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-folder.png"));
        fileIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-file.png"));
        meshIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-mesh.png"));
        prefabIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-prefab.png"));
        javaIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-java.png"));
        sceneIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-scene.png"));
        shaderIcon = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-shader.png"));
        absolutePath = SceneHandler.getActiveScene().getPath();
    }

    @Override
    public void render(float dt) {
        if (ImGui.begin("Content Folder")) {

            if (ImGui.button("<--") && absolutePath.getParent() != null) {
                absolutePath = absolutePath.getParent();
                SceneHandler.getActiveScene().setPath(absolutePath);
            }
            ImGui.sameLine();
            ImGui.labelText("Current Path", absolutePath.toString());
            dragAndDropTargetEntity();
            ImGui.separator();

            File folder = absolutePath.toFile();
            File[] listOfFiles = folder.listFiles();

            float panelWidth = ImGui.getContentRegionAvailX();
            float cellSize = PADDING + THUMBNAIL_SIZE;
            int columnCount = (int) (panelWidth / cellSize);
            ImGui.columns(columnCount, "", false);
            assert listOfFiles != null;

            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            for (File file : listOfFiles) {
                fileType(file);
            }
            ImGui.popStyleColor();
        }
        ImGui.columns(1);
        ImGui.end();
    }

    private void fileType(File file) {
        if (file.isFile()) {
            ImGui.pushID(file.getName());
            int icon = getFileIcon(file);
            ImGui.imageButton(icon, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            if (ImGui.isMouseDragging(GLFW_MOUSE_BUTTON_1))
                dragAndDropSourceEntity(file);
            ImGui.textWrapped(FileUtil.getFileNameWithoutExtension(file));
            ImGui.popID();
            ImGui.nextColumn();
        } else if (file.isDirectory()) {
            ImGui.pushID(file.getName());
            if (ImGui.imageButton(folderIcon, THUMBNAIL_SIZE, THUMBNAIL_SIZE))
                absolutePath = Paths.get(absolutePath + FOLDER_SPLITTER + file.getName());
            ImGui.textWrapped(file.getName());
            ImGui.popID();
            ImGui.nextColumn();
        }
    }

    private int getFileIcon(File file) {
        String extension = FileUtil.getFileExtension(file).orElse("");
        return switch (extension) {
            case "prefab" -> prefabIcon;
            case "java" -> javaIcon;
            case "mesh" -> meshIcon;
            case "scene" -> sceneIcon;
            case "glsl" -> shaderIcon;
            default -> fileIcon;
        };
    }

    private void dragAndDropTargetEntity() {
        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload(DragAndDrop.SAVE_ENTITY.getType());
            if (payload != null && payload.getClass().isAssignableFrom(Entity.class)) {
                Entity entity = (Entity) payload;
                Serializable.saveEntity(entity, Path.of(absolutePath.toString()));
            }
            ImGui.endDragDropTarget();
        }
    }

    private void dragAndDropSourceEntity(File file) {
        FileUtil.getFileExtension(file).ifPresent(extension -> {
            if (extension.equals(FileExtension.PREFAB_EXTENSION.getFileName()) && ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload(DragAndDrop.LOAD_ENTITY.getType(), file.getAbsolutePath(), ImGuiCond.Once);
                ImGui.text(file.toString());
                ImGui.endDragDropSource();
            }
        });
    }

    public void setAbsolutePath(Path absolutePath) {
        this.absolutePath = absolutePath;
    }
}

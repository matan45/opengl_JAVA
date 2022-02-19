package app.editor.imgui;

import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentBrowser implements ImguiLayer {
    private Path absolutePath = Paths.get("C:\\matan\\java\\src\\main");

    private final int folderIcon;
    private final int fileIcon;

    private static final String FOLDER_SPLITTER= "\\";

    public ContentBrowser() {
        Textures textures = EditorRenderer.getTextures();
        folderIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-folder.png");
        fileIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-file.png");
    }

    @Override
    public void render(float dt) {
        if (ImGui.begin("Content Folder")) {
            if (ImGui.button("<--") && absolutePath.getParent() != null)
                absolutePath = absolutePath.getParent();
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
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    ImGui.imageButton(fileIcon, thumbnailSize, thumbnailSize);
                    ImGui.textWrapped(listOfFile.getName());
                    ImGui.nextColumn();
                } else if (listOfFile.isDirectory()) {
                    ImGui.pushID(listOfFile.getName());
                    if (ImGui.imageButton(folderIcon, thumbnailSize, thumbnailSize))
                        absolutePath = Paths.get(absolutePath + FOLDER_SPLITTER + listOfFile.getName());
                    ImGui.textWrapped(listOfFile.getName());
                    ImGui.popID();
                    ImGui.nextColumn();
                }
            }
            ImGui.popStyleColor();
        }
        ImGui.columns(1);
        ImGui.end();
    }

}

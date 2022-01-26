package app.editor.imgui;

import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentBrowser implements ImguiLayer {
    Path absolutePath = Paths.get("C:\\matan\\java\\src\\main");
    File folder;
    File[] listOfFiles;
    int folderIcon;
    int fileIcon;
    Textures textures;
    float padding = 16.0f;
    float thumbnailSize = 128.0f;
    float cellSize = padding + thumbnailSize;

    public ContentBrowser() {
        textures = EditorRenderer.getTextures();
        this.folderIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-folder.png");
        this.fileIcon = textures.loadTextureHdr("src\\main\\resources\\editor\\icons\\contentBrowser\\icon-file.png");
    }

    @Override
    public void render() {
        if (ImGui.begin("Content Folder")) {
            if (ImGui.button("<--") && absolutePath.getParent() != null)
                absolutePath = absolutePath.getParent();
            ImGui.sameLine();
            ImGui.labelText("Current Path", absolutePath.toString());
            ImGui.separator();

            folder = absolutePath.toFile();
            listOfFiles = folder.listFiles();

            float panelWidth = ImGui.getContentRegionAvailX();
            int columnCount = (int) (panelWidth / cellSize);
            ImGui.columns(columnCount, "", false);
            assert listOfFiles != null;
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    ImGui.imageButton(fileIcon, thumbnailSize, thumbnailSize);
                    ImGui.textWrapped(listOfFile.getName());
                    ImGui.nextColumn();
                } else if (listOfFile.isDirectory()) {
                    ImGui.pushID(listOfFile.getName());
                    if (ImGui.imageButton(folderIcon, thumbnailSize, thumbnailSize))
                        absolutePath = Paths.get(absolutePath + "\\" + listOfFile.getName());
                    ImGui.textWrapped(listOfFile.getName());
                    ImGui.popID();
                    ImGui.nextColumn();
                }
            }
        }
        ImGui.columns(1);
        ImGui.end();
    }

}

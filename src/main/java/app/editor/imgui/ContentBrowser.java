package app.editor.imgui;

import imgui.ImGui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentBrowser implements ImguiLayer {
    Path absolutePath = Paths.get("C:\\matan\\java\\src\\main");
    File folder;
    File[] listOfFiles;

    @Override
    public void render() {
        if (ImGui.begin("Content Folder")) {
            if (ImGui.button("<--"))
                absolutePath = absolutePath.getParent();
            ImGui.sameLine();
            ImGui.labelText("Current Path", absolutePath.toString());
            ImGui.separator();

            folder = absolutePath.toFile();
            listOfFiles = folder.listFiles();

            assert listOfFiles != null;
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile())
                    ImGui.labelText("File ", listOfFile.getName());
                else if (listOfFile.isDirectory()) {
                    ImGui.pushID(listOfFile.getName());
                    if (ImGui.button("Directory " + listOfFile.getName()))
                        absolutePath = Paths.get(absolutePath + "\\" + listOfFile.getName());
                    ImGui.popID();
                }
            }
        }
        ImGui.end();
    }
}

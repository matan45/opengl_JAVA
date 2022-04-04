package app.ecs.components;

import app.ecs.Entity;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;

public class MusicComponent extends CommonComponent {
    private String path = "";
    private String prePath = "";
    private File file;


    public MusicComponent(Entity ownerEntity) {
        super(ownerEntity);
        file = new File("");
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Mesh"))
            path = OpenFileDialog.openFile("wav").orElse(prePath);

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

    }
}

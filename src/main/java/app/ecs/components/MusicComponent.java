package app.ecs.components;

import app.ecs.Entity;
import app.utilities.OpenFileDialog;
import app.utilities.logger.LogInfo;
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
            LogInfo.println(file.getName());
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

    }
}

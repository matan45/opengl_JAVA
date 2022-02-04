package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.ibl.SkyBox;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;
import java.util.Optional;

public class SkyBoxComponent extends CommonComponent {
    SkyBox skyBox;
    ImBoolean showLightMap;
    String result;

    public SkyBoxComponent(Entity ownerEntity) {
        super(ownerEntity);
        skyBox = EditorRenderer.getSkyBox();
        showLightMap = new ImBoolean(false);
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("HDR")) {
            result = OpenFileDialog.openFile("hdr");
            if (result != null)
                skyBox.init(result);

        } else if (result != null) {
            ImGui.sameLine();
            File file = new File(result);
            ImGui.textWrapped(file.getName());
        }

        if (ImGui.checkbox("Light Map", showLightMap))
            skyBox.setShowLightMap(showLightMap.get());

    }

    @Override
    public void cleanUp() {
        skyBox.setActive(false);
    }
}

package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.ibl.SkyBox;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.io.File;

public class SkyBoxComponent extends CommonComponent {
    SkyBox skyBox;
    ImBoolean showLightMap;
    ImBoolean showPreFilterMap;
    String result;
    ImInt select;

    public SkyBoxComponent(Entity ownerEntity) {
        super(ownerEntity);
        skyBox = EditorRenderer.getSkyBox();
        showLightMap = new ImBoolean(false);
        showPreFilterMap = new ImBoolean(false);
        select = new ImInt(0);
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

        if (ImGui.radioButton("Cube Map", select, 0)) {
            select.set(0);
            skyBox.setShowLightMap(false);
            skyBox.setShowPreFilterMap(false);
        } else if (ImGui.radioButton("Light Map", select, 1)) {
            select.set(1);
            skyBox.setShowLightMap(true);
            skyBox.setShowPreFilterMap(false);
        } else if (ImGui.radioButton("PreFilter Map", select, 2)) {
            select.set(2);
            skyBox.setShowLightMap(false);
            skyBox.setShowPreFilterMap(true);
        }

    }

    @Override
    public void cleanUp() {
        skyBox.setActive(false);
    }
}

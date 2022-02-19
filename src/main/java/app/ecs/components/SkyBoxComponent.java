package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.ibl.SkyBox;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImInt;

import java.io.File;

public class SkyBoxComponent extends CommonComponent {
    private final SkyBox skyBox;
    private final ImInt select;
    private float exposure;

    public SkyBoxComponent(Entity ownerEntity) {
        super(ownerEntity);
        skyBox = EditorRenderer.getSkyBox();
        select = new ImInt(0);
        exposure = 2.2f;
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("HDR")) {
            OpenFileDialog.openFile("hdr").ifPresent(skyBox::init);

        } else if (skyBox.getPath() != null && !skyBox.getPath().isBlank()) {
            ImGui.sameLine();
            File file = new File(skyBox.getPath());
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

        if (ImGui.button("Exposure"))
            exposure = 2.2f;
        ImGui.sameLine();
        float[] yValue = {exposure};
        ImGui.dragFloat("##Y", yValue, 0.1f);
        exposure = yValue[0];
        skyBox.setExposure(exposure);

    }

    @Override
    public void cleanUp() {
        skyBox.setActive(false);
    }
}

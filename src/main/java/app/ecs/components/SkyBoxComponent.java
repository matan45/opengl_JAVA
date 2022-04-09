package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.ibl.SkyBox;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImInt;

import java.io.File;

public class SkyBoxComponent extends CommonComponent {
    private final transient SkyBox skyBox;
    private final ImInt select;

    private String path = "";
    private String prePath = "";
    private File file;

    public SkyBoxComponent(Entity ownerEntity) {
        super(ownerEntity);
        skyBox = EditorRenderer.getSkyBox();
        select = new ImInt(0);
        file = new File("");
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("HDR"))
            path = OpenFileDialog.openFile("hdr").orElse(prePath);

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            skyBox.init(path);
            file = new File(path);
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

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
            skyBox.setExposure(2.2f);
        ImGui.sameLine();
        float[] exposureValue = {skyBox.getExposure()};
        ImGui.dragFloat("##Y", exposureValue, 0.1f);
        skyBox.setExposure(exposureValue[0]);

    }

    @Override
    public void cleanUp() {
        skyBox.setActive(false);
    }
}

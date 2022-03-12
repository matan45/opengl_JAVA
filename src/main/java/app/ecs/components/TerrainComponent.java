package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;

public class TerrainComponent extends CommonComponent {
    private final TerrainRenderer terrain;
    private final ImBoolean wireframe;
    private final OLTransform olTransform;

    public TerrainComponent(Entity ownerEntity) {
        super(ownerEntity);
        terrain = EditorRenderer.getTerrain();
        wireframe = new ImBoolean();
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
    }


    @Override
    public void imguiDraw() {
        if (ImGui.button("Height Map")) {
            OpenFileDialog.openFile("png,jpg").ifPresent(p -> terrain.init(p, olTransform));

        } else if (terrain.getPath() != null && !terrain.getPath().isBlank()) {
            ImGui.sameLine();
            File file = new File(terrain.getPath());
            ImGui.textWrapped(file.getName());
            terrain.setActive(true);
        }

        if (ImGui.button("Displacement"))
            terrain.setFactor(2f);
        ImGui.sameLine();
        float[] displacementValue = {terrain.getFactor()};
        ImGui.dragFloat("##Y", displacementValue, 0.1f);
        terrain.setFactor(displacementValue[0]);

        ImGui.checkbox("Wireframe", wireframe);
        terrain.setWireframe(wireframe.get());

    }

    @Override
    public void cleanUp() {
        terrain.setActive(false);
    }
}

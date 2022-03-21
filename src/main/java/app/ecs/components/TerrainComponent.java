package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainRenderer;
import app.renderer.terrain.quadtree.TerrainQuadtreeRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;

public class TerrainComponent extends CommonComponent {
    private final TerrainQuadtreeRenderer terrain;
    private final ImBoolean wireframe;

    public TerrainComponent(Entity ownerEntity) {
        super(ownerEntity);
        terrain = EditorRenderer.getTerrainQuadtreeRenderer();
        wireframe = new ImBoolean();
    }


    @Override
    public void imguiDraw() {
        if (ImGui.button("Height Map")) {
            OpenFileDialog.openFile("png,jpg").ifPresent(terrain::init);

        } else if (terrain.getPath() != null && !terrain.getPath().isBlank()) {
            ImGui.sameLine();
            File file = new File(terrain.getPath());
            ImGui.textWrapped(file.getName());
            terrain.setActive(true);
        }

        ImGui.pushID("Displacement");
        if (ImGui.button("Displacement"))
            terrain.setgDispFactor(40f);
        ImGui.sameLine();
        float[] displacementValue = {terrain.getgDispFactor()};
        ImGui.dragFloat("##Y", displacementValue, 0.1f);
        terrain.setgDispFactor(displacementValue[0]);
        ImGui.popID();

        ImGui.textWrapped("RenderDepth: " + terrain.getRenderDepth());

        ImGui.checkbox("Wireframe", wireframe);
        terrain.setWireframe(wireframe.get());

    }

    @Override
    public void cleanUp() {
        terrain.setActive(false);
    }
}

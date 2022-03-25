package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;

import java.io.File;

public class TerrainComponent extends CommonComponent {
    private final TerrainQuadtreeRenderer terrain;
    private final ImBoolean wireframe;

    private float R;
    private float G;
    private float B;

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
            terrain.setDisplacementFactor(40f);
        ImGui.sameLine();
        float[] displacementValue = {terrain.getDisplacementFactor()};
        ImGui.dragFloat("##Y", displacementValue, 0.1f);
        terrain.setDisplacementFactor(displacementValue[0]);
        ImGui.popID();

        ImGui.pushID("SightRange");
        if (ImGui.button("SightRange"))
            terrain.setSightRange(0.06f);
        ImGui.sameLine();
        float[] sightRangeValue = {terrain.getSightRange()};
        ImGui.dragFloat("##Y", sightRangeValue, 0.01f);
        terrain.setSightRange(sightRangeValue[0]);
        ImGui.popID();

        ImGui.pushID("DirectionalLight");
        float[] color = {R, G, B};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);
        R = color[0];
        G = color[1];
        B = color[2];

        terrain.setFogColor(R, G, B);
        ImGui.popID();

        ImGui.textWrapped("RenderDepth: " + terrain.getRenderDepth());
        ImGui.textWrapped("Number of Terrain Nodes: " + terrain.getNumTerrainNodes());

        ImGui.checkbox("Wireframe", wireframe);
        terrain.setWireframe(wireframe.get());

    }

    @Override
    public void cleanUp() {
        terrain.setActive(false);
    }
}

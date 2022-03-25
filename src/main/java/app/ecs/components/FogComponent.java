package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.fog.Fog;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class FogComponent extends CommonComponent {
    private final Fog fog;

    private float R;
    private float G;
    private float B;

    public FogComponent(Entity ownerEntity) {
        super(ownerEntity);
        fog = new Fog();
        EditorRenderer.getTerrainQuadtreeRenderer().setFog(fog);
    }

    @Override
    public void imguiDraw() {
        ImGui.pushID("SightRange");
        if (ImGui.button("SightRange"))
            fog.setSightRange(0.06f);
        ImGui.sameLine();
        float[] sightRangeValue = {fog.getSightRange()};
        ImGui.dragFloat("##Y", sightRangeValue, 0.01f);
        fog.setSightRange(sightRangeValue[0]);
        ImGui.popID();

        ImGui.pushID("DirectionalLight");
        float[] color = {fog.getFogColor().x, fog.getFogColor().y, fog.getFogColor().z};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);
        R = color[0];
        G = color[1];
        B = color[2];

        fog.setFogColor(R, G, B);
        ImGui.popID();
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getTerrainQuadtreeRenderer().setFog(null);
    }
}

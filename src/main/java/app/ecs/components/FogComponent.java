package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.fog.Fog;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class FogComponent extends Component {
    private final Fog fog;

    public FogComponent(Entity ownerEntity) {
        super(ownerEntity);
        fog = new Fog();
        EditorRenderer.getTerrainQuadtreeRenderer().setFog(fog);
        EditorRenderer.getMeshRenderer().setFog(fog);
    }


    @Override
    public void imguiDraw() {
        ImGui.pushID("SightRange");
        if (ImGui.button("SightRange"))
            fog.setSightRange(0.3f);
        ImGui.sameLine();
        float[] sightRangeValue = {fog.getSightRange()};
        ImGui.dragFloat("##Y", sightRangeValue, 0.01f, 0.0f, 10.0f);
        fog.setSightRange(sightRangeValue[0]);
        ImGui.popID();

        ImGui.pushID("FogColor");
        float[] color = {fog.getFogColor().x, fog.getFogColor().y, fog.getFogColor().z};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);

        fog.setFogColor(color[0], color[1], color[2]);
        ImGui.popID();
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getTerrainQuadtreeRenderer().setFog(null);
        EditorRenderer.getMeshRenderer().setFog(null);
    }

    public Fog getFog() {
        return fog;
    }
}

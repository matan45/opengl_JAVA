package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.DirectionalLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class DirectionalLightComponent extends CommonComponent {
    private final DirectionalLight directionalLight;
    private final OLTransform olTransform;

    private float R;
    private float G;
    private float B;

    public DirectionalLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        directionalLight = new DirectionalLight();
        EditorRenderer.getLightHandler().setDirectionalLight(directionalLight);
    }

    @Override
    public void update(float dt) {
        directionalLight.setDirection(olTransform.getRotation());
    }

    @Override
    public void imguiDraw() {
        ImGui.pushID("DirectionalLight");
        float[] color = {R, G, B};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);
        R = color[0];
        G = color[1];
        B = color[2];

        directionalLight.setColor(R, G, B);
        ImGui.popID();
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().setDirectionalLight(null);
    }

}

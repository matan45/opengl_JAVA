package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.PointLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class PointLightComponent extends CommonComponent {
    private final OLTransform olTransform;
    private final PointLight pointLight;

    private float R;
    private float G;
    private float B;

    public PointLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        pointLight = new PointLight();
        EditorRenderer.getLightHandler().addPointLight(pointLight);
    }

    @Override
    public void update(float dt) {
        pointLight.setPosition(olTransform.getPosition());
    }

    @Override
    public void imguiDraw() {
        ImGui.pushID("PointLightConstant");
        if (ImGui.button("Constant"))
            pointLight.setConstant(0.1f);
        ImGui.sameLine();
        float[] constantValue = {pointLight.getConstant()};
        ImGui.dragFloat("##Y", constantValue, 0.01f);
        pointLight.setConstant(constantValue[0]);
        ImGui.popID();

        ImGui.pushID("PointLightLinear");
        if (ImGui.button("Linear"))
            pointLight.setLinear(0.1f);
        ImGui.sameLine();
        float[] linearValue = {pointLight.getLinear()};
        ImGui.dragFloat("##Y", linearValue, 0.01f);
        pointLight.setLinear(linearValue[0]);
        ImGui.popID();

        ImGui.pushID("PointLightQuadratic");
        if (ImGui.button("Quadratic"))
            pointLight.setQuadratic(0.1f);
        ImGui.sameLine();
        float[] quadraticValue = {pointLight.getQuadratic()};
        ImGui.dragFloat("##Y", quadraticValue, 0.01f);
        pointLight.setQuadratic(quadraticValue[0]);
        ImGui.popID();

        float[] color = {R, G, B};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);
        R = color[0];
        G = color[1];
        B = color[2];

        pointLight.setColor(R, G, B);
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().removePointLight(pointLight);
    }

}

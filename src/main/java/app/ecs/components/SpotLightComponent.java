package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.SpotLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class SpotLightComponent extends CommonComponent {
    private final OLTransform olTransform;
    private final SpotLight spotLight;

    private float R;
    private float G;
    private float B;

    public SpotLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        spotLight = new SpotLight();
        EditorRenderer.getLightHandler().addSpotLight(spotLight);

    }

    @Override
    public void update(float dt) {
        spotLight.setPosition(olTransform.getPosition());
        spotLight.setDirection(olTransform.getRotation());
    }

    @Override
    public void imguiDraw() {
        ImGui.pushID("SpotLightConstant");
        if (ImGui.button("Constant"))
            spotLight.setConstant(0.1f);
        ImGui.sameLine();
        float[] constantValue = {spotLight.getConstant()};
        ImGui.dragFloat("##Y", constantValue, 0.01f);
        spotLight.setConstant(constantValue[0]);
        ImGui.popID();

        ImGui.pushID("SpotLightLinear");
        if (ImGui.button("Linear"))
            spotLight.setLinear(0.1f);
        ImGui.sameLine();
        float[] linearValue = {spotLight.getLinear()};
        ImGui.dragFloat("##Y", linearValue, 0.01f);
        spotLight.setLinear(linearValue[0]);
        ImGui.popID();

        ImGui.pushID("SpotLightQuadratic");
        if (ImGui.button("Quadratic"))
            spotLight.setQuadratic(0.1f);
        ImGui.sameLine();
        float[] quadraticValue = {spotLight.getQuadratic()};
        ImGui.dragFloat("##Y", quadraticValue, 0.01f);
        spotLight.setQuadratic(quadraticValue[0]);
        ImGui.popID();

        ImGui.pushID("SpotLightCutOff");
        if (ImGui.button("CutOff"))
            spotLight.setCutOff(0f);
        ImGui.sameLine();
        float[] cutOffValue = {spotLight.getCutOff()};
        ImGui.dragFloat("##Y", cutOffValue, 0.01f);
        spotLight.setCutOff(cutOffValue[0]);
        ImGui.popID();

        ImGui.pushID("SpotLightOuterCutOff");
        if (ImGui.button("OuterCutOff"))
            spotLight.setOuterCutOff(0f);
        ImGui.sameLine();
        float[] outerCutOffValue = {spotLight.getOuterCutOff()};
        ImGui.dragFloat("##Y", outerCutOffValue, 0.01f);
        spotLight.setOuterCutOff(outerCutOffValue[0]);
        ImGui.popID();

        float[] color = {R, G, B};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);
        R = color[0];
        G = color[1];
        B = color[2];

        spotLight.setColor(R, G, B);
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().removeSpotLight(spotLight);
    }
}
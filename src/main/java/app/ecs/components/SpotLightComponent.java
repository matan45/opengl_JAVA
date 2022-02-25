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

    private float constant;
    private float linear;
    private float quadratic;

    private float cutOff;
    private float outerCutOff;

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
            constant = 0f;
        ImGui.sameLine();
        float[] constantValue = {constant};
        ImGui.dragFloat("##Y", constantValue, 0.01f);
        constant = constantValue[0];
        spotLight.setConstant(constant);
        ImGui.popID();

        ImGui.pushID("SpotLightLinear");
        if (ImGui.button("Linear"))
            linear = 0f;
        ImGui.sameLine();
        float[] linearValue = {linear};
        ImGui.dragFloat("##Y", linearValue, 0.01f);
        linear = linearValue[0];
        spotLight.setLinear(linear);
        ImGui.popID();

        ImGui.pushID("SpotLightQuadratic");
        if (ImGui.button("Quadratic"))
            quadratic = 0f;
        ImGui.sameLine();
        float[] quadraticValue = {quadratic};
        ImGui.dragFloat("##Y", quadraticValue, 0.01f);
        quadratic = quadraticValue[0];
        spotLight.setQuadratic(quadratic);
        ImGui.popID();

        ImGui.pushID("SpotLightCutOff");
        if (ImGui.button("CutOff"))
            cutOff = 0f;
        ImGui.sameLine();
        float[] cutOffValue = {cutOff};
        ImGui.dragFloat("##Y", cutOffValue, 0.01f);
        cutOff = cutOffValue[0];
        spotLight.setCutOff(cutOff);
        ImGui.popID();

        ImGui.pushID("SpotLightOuterCutOff");
        if (ImGui.button("OuterCutOff"))
            outerCutOff = 0f;
        ImGui.sameLine();
        float[] outerCutOffValue = {outerCutOff};
        ImGui.dragFloat("##Y", outerCutOffValue, 0.01f);
        outerCutOff = outerCutOffValue[0];
        spotLight.setOuterCutOff(outerCutOff);
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

package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.PointLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class PointLightComponent extends CommonComponent {
    private final OLTransform olTransform;
    private PointLight pointLight;

    private float constant;
    private float linear;
    private float quadratic;

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
        ImGui.pushID("Constant");
        if (ImGui.button("Constant"))
            constant = 0f;
        ImGui.sameLine();
        float[] constantValue = {constant};
        ImGui.dragFloat("##Y", constantValue, 0.01f);
        constant = constantValue[0];
        pointLight.setConstant(constant);
        ImGui.popID();

        ImGui.pushID("Linear");
        if (ImGui.button("Linear"))
            linear = 0f;
        ImGui.sameLine();
        float[] linearValue = {linear};
        ImGui.dragFloat("##Y", linearValue, 0.01f);
        linear = linearValue[0];
        pointLight.setLinear(linear);
        ImGui.popID();

        ImGui.pushID("Quadratic");
        if (ImGui.button("Quadratic"))
            quadratic = 0f;
        ImGui.sameLine();
        float[] quadraticValue = {quadratic};
        ImGui.dragFloat("##Y", quadraticValue, 0.01f);
        quadratic = quadraticValue[0];
        pointLight.setQuadratic(quadratic);
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

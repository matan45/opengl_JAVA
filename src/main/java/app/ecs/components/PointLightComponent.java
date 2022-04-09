package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.Textures;
import app.renderer.debug.billboards.Billboards;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.PointLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class PointLightComponent extends Component {
    private final OLTransform olTransform;
    private final PointLight pointLight;

    public PointLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        Textures textures = EditorRenderer.getTextures();
        Billboards billboards = new Billboards(EditorRenderer.getOpenGLObjects(), textures.loadTexture("src\\main\\resources\\editor\\icons\\lights\\pointLight.png"));
        pointLight = new PointLight(billboards);
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

        float[] color = {pointLight.getColor().x, pointLight.getColor().y, pointLight.getColor().z};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);

        pointLight.setColor(color[0], color[1], color[2]);
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().removePointLight(pointLight);
    }

    @Override
    public int getComponentType() {
        return 0;
    }

}

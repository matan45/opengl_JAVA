package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.texture.Textures;
import app.renderer.debug.billboards.Billboards;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.SpotLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

import java.nio.file.Path;

public class SpotLightComponent extends Component {
    private final OLTransform olTransform;
    private final SpotLight spotLight;

    public SpotLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        Textures textures = EditorRenderer.getTextures();
        Billboards billboards = new Billboards(EditorRenderer.getOpenGLObjects(), textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\lights\\spotLight.png")));
        spotLight = new SpotLight(billboards);
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

        float[] color = {spotLight.getColor().x, spotLight.getColor().y, spotLight.getColor().z};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);

        spotLight.setColor(color[0], color[1], color[2]);
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().removeSpotLight(spotLight);
    }

    public SpotLight getSpotLight() {
        return spotLight;
    }
}

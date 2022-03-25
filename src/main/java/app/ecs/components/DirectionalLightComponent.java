package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.Textures;
import app.renderer.debug.billboards.Billboards;
import app.renderer.draw.EditorRenderer;
import app.renderer.lights.DirectionalLight;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

public class DirectionalLightComponent extends CommonComponent {
    private final DirectionalLight directionalLight;
    private final OLTransform olTransform;


    public DirectionalLightComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        Textures textures = EditorRenderer.getTextures();
        Billboards billboards = new Billboards(EditorRenderer.getOpenGLObjects(), textures.loadTexture("src\\main\\resources\\editor\\icons\\lights\\directionalLight.png"));
        directionalLight = new DirectionalLight(billboards);
        directionalLight.setDirLightIntensity(1f);
        EditorRenderer.getLightHandler().setDirectionalLight(directionalLight);
    }

    @Override
    public void update(float dt) {
        directionalLight.setDirection(olTransform.getRotation());
    }

    @Override
    public void imguiDraw() {
        ImGui.pushID("DirectionalLightIntensity");
        if (ImGui.button("Intensity"))
            directionalLight.setDirLightIntensity(1f);
        ImGui.sameLine();
        float[] constantValue = {directionalLight.getDirLightIntensity()};
        ImGui.dragFloat("##Y", constantValue, 0.1f);
        directionalLight.setDirLightIntensity(constantValue[0]);
        ImGui.popID();

        ImGui.pushID("DirectionalLight");
        float[] color = {directionalLight.getColor().x, directionalLight.getColor().y, directionalLight.getColor().z};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop);

        directionalLight.setColor(color[0], color[1], color[2]);
        ImGui.popID();
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getLightHandler().setDirectionalLight(null);
    }

}

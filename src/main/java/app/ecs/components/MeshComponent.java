package app.ecs.components;

import app.ecs.Entity;
import app.math.OLVector3f;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.pbr.Material;
import app.renderer.pbr.MeshRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

import java.io.File;
import java.util.Optional;

public class MeshComponent extends CommonComponent {
    private final MeshRenderer meshRenderer;
    private final OLTransform olTransform;
    private final Material material;

    float tt1;
    float tt2;
    float tt3;

    float aa1;
    float aa2;
    float aa3;

    public MeshComponent(Entity ownerEntity) {
        super(ownerEntity);
        meshRenderer = EditorRenderer.getMeshRenderer().createNewInstant();
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        material = meshRenderer.getMaterial();
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Mesh")) {
            OpenFileDialog.openFile("obj,fbx,stl,dae,gltf").ifPresent(s -> {
                if (!EditorRenderer.getMeshRenderer().isContains(meshRenderer))
                    EditorRenderer.getMeshRenderer().addInstant(meshRenderer);
                meshRenderer.init(s, olTransform);
            });

        } else if (meshRenderer.getPath() != null && !meshRenderer.getPath().isBlank()) {
            ImGui.sameLine();
            File file = new File(meshRenderer.getPath());
            ImGui.textWrapped(file.getName());
        }

        ImGui.pushID("tt1");
        if (ImGui.button("tt1"))
            tt1 = 2.2f;
        ImGui.sameLine();
        float[] xValue = {tt1};
        ImGui.dragFloat("##Y", xValue, 0.1f);
        tt1 = xValue[0];
        ImGui.popID();

        ImGui.pushID("tt2");
        if (ImGui.button("tt2"))
            tt2 = 2.2f;
        ImGui.sameLine();
        float[] yValue = {tt2};
        ImGui.dragFloat("##Y", yValue, 0.1f);
        tt2 = yValue[0];
        ImGui.popID();

        ImGui.pushID("tt3");
        if (ImGui.button("tt3"))
            tt3 = 2.2f;
        ImGui.sameLine();
        float[] zValue = {tt3};
        ImGui.dragFloat("##Y", zValue, 0.1f);
        tt3 = zValue[0];
        ImGui.popID();

        meshRenderer.getTest().setDirection(new OLVector3f(tt1, tt2, tt3));

        float[] color = {aa1, aa2, aa3};
        ImGui.colorEdit3("color", color, ImGuiColorEditFlags.DisplayRGB|ImGuiColorEditFlags.NoDragDrop);
        aa1 = color[0];
        aa2 = color[1];
        aa3 = color[2];

        meshRenderer.getTest().setColor(new OLVector3f(aa1, aa2, aa3));

        ImGui.textWrapped("Material");
        ImGui.separator();
        ImGui.columns(3, "", true);

        material.setAlbedoMap(materialPath("Albedo"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getAlbedoMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Albedo");
        if (ImGui.button("X"))
            material.albedoMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setNormalMap(materialPath("Normal"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getNormalMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Normal");
        if (ImGui.button("X"))
            material.normalMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setRoughnessMap(materialPath("Roughness"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getRoughnessMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Roughness");
        if (ImGui.button("X"))
            material.roughnessMapRemove();
        ImGui.popID();


        ImGui.nextColumn();
        material.setMetallicMap(materialPath("Metallic"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getMetallicMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Metallic");
        if (ImGui.button("X"))
            material.metallicMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setAoMap(materialPath("Ambient Occlusion"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getAoMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Ambient Occlusion");
        if (ImGui.button("X"))
            material.aoMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setDisplacementMap(materialPath("Displacement"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getDisplacementMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Displacement");
        if (ImGui.button("X"))
            material.displacementMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setEmissiveMap(materialPath("Emissive"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getEmissiveMapPath());
        ImGui.nextColumn();
        ImGui.pushID("Emissive");
        if (ImGui.button("X"))
            material.emissiveMapRemove();
        ImGui.popID();

        ImGui.columns(1);
    }

    private String materialPath(String buttonName) {
        if (ImGui.button(buttonName)) {
            Optional<String> path = OpenFileDialog.openFile("png,tga,jpg");
            if (path.isPresent())
                return path.get();
        }
        return "";
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getMeshRenderer().removeInstant(meshRenderer);
    }
}

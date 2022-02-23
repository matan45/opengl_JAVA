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

package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.pbr.Material;
import app.renderer.pbr.MeshRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;
import java.util.Optional;

public class MeshComponent extends CommonComponent {
    private final MeshRenderer meshRenderer;
    private final OLTransform olTransform;
    private final Material material;

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
        ImGui.columns(2, "", true);
        material.setAlbedoMap(materialPath("Albedo"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getAlbedoMapPath());

        ImGui.nextColumn();
        material.setNormalMap(materialPath("Normal"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getNormalMapPath());

        ImGui.nextColumn();
        material.setMetallicMap(materialPath("Metallic"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getMetallicMapPath());

        ImGui.nextColumn();
        material.setAoMap(materialPath("Ambient Occlusion"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getAoMapPath());

        ImGui.nextColumn();
        material.setDisplacementMap(materialPath("Displacement"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getDisplacementMapPath());

        ImGui.nextColumn();
        material.setEmissiveMap(materialPath("Emissive"));

        ImGui.nextColumn();
        ImGui.textWrapped(material.getEmissiveMapPath());

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

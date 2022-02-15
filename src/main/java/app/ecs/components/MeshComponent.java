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

        material.setAlbedoMap(materialPath("Albedo"));
        if (material.getAlbedoMapPath() != null && !material.getAlbedoMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getAlbedoMapPath());
        }
        material.setNormalMap(materialPath("Normal"));
        if (material.getNormalMapPath() != null && !material.getNormalMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getNormalMapPath());
        }
        material.setMetallicMap(materialPath("Metallic"));
        if (material.getMetallicMapPath() != null && !material.getMetallicMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getMetallicMapPath());
        }
        material.setAoMap(materialPath("Ambient Occlusion"));
        if (material.getAoMapPath() != null && !material.getAoMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getAoMapPath());
        }
        material.setDisplacementMap(materialPath("Displacement"));
        if (material.getDisplacementMapPath() != null && !material.getDisplacementMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getDisplacementMapPath());
        }
        material.setEmissiveMap(materialPath("Emissive"));
        if (material.getEmissiveMapPath() != null && !material.getEmissiveMapPath().isEmpty()) {
            ImGui.sameLine();
            ImGui.textWrapped(material.getEmissiveMapPath());
        }

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

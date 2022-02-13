package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.pbr.MeshRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;

public class MeshComponent extends CommonComponent {
    private final MeshRenderer meshRenderer;
    private final OLTransform olTransform;

    public MeshComponent(Entity ownerEntity) {
        super(ownerEntity);
        meshRenderer = EditorRenderer.getMeshRenderer().createNewInstant();
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
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
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getMeshRenderer().removeInstant(meshRenderer);
    }
}
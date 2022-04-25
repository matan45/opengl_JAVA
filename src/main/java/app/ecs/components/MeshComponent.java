package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.pbr.Material;
import app.renderer.pbr.Mesh;
import app.renderer.pbr.MeshRenderer;
import app.utilities.OpenFileDialog;
import app.utilities.resource.ResourceManager;
import app.utilities.serialize.FileExtension;
import app.utilities.serialize.SerializableMesh;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class MeshComponent extends Component {
    private final MeshRenderer meshRenderer;
    private final OLTransform olTransform;
    private final Material material;

    private String path = "";
    private String prePath = "";
    private String meshName = "";
    private File file;

    public MeshComponent(Entity ownerEntity) {
        super(ownerEntity);
        meshRenderer = EditorRenderer.getMeshRenderer().createNewInstant();
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        material = meshRenderer.getMaterial();
        file = new File("");
    }


    @Override
    public void imguiDraw() {
        if (ImGui.button("Mesh"))
            path = OpenFileDialog.openFile(FileExtension.MESH_EXTENSION.getFileName()).orElse(prePath);

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
            Mesh mesh = ResourceManager.loadMeshFromFile(Path.of(path));
            assert mesh != null;
            meshName = mesh.name();
            meshRenderer.init(mesh, olTransform);
        }

        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.textWrapped("Mesh Name: " + meshName);

        ImGui.textWrapped("Material");
        ImGui.separator();
        ImGui.columns(3, "", true);

        material.setAlbedoMap(materialPath("Albedo"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getAlbedoFileName());
        ImGui.nextColumn();
        ImGui.pushID("Albedo");
        if (ImGui.button("X"))
            material.albedoMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setNormalMap(materialPath("Normal"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getNormalFileName());
        ImGui.nextColumn();
        ImGui.pushID("Normal");
        if (ImGui.button("X"))
            material.normalMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setRoughnessMap(materialPath("Roughness"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getRoughnessFileName());
        ImGui.nextColumn();
        ImGui.pushID("Roughness");
        if (ImGui.button("X"))
            material.roughnessMapRemove();
        ImGui.popID();


        ImGui.nextColumn();
        material.setMetallicMap(materialPath("Metallic"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getMetallicFileName());
        ImGui.nextColumn();
        ImGui.pushID("Metallic");
        if (ImGui.button("X"))
            material.metallicMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setAoMap(materialPath("Ambient Occlusion"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getAoFileName());
        ImGui.nextColumn();
        ImGui.pushID("Ambient Occlusion");
        if (ImGui.button("X"))
            material.aoMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        material.setEmissiveMap(materialPath("Emissive"));
        ImGui.nextColumn();
        ImGui.textWrapped(material.getEmissiveFileName());
        ImGui.nextColumn();
        ImGui.pushID("Emissive");
        if (ImGui.button("X"))
            material.emissiveMapRemove();
        ImGui.popID();

        ImGui.columns(1);
    }

    private String materialPath(String buttonName) {
        if (ImGui.button(buttonName)) {
            Optional<String> materialPath = OpenFileDialog.openFile("png,tga,jpg");
            if (materialPath.isPresent())
                return materialPath.get();
        }
        return "";
    }

    @Override
    public void cleanUp() {
        EditorRenderer.getMeshRenderer().removeInstant(meshRenderer);
    }

    public Material getMaterial() {
        return material;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OLTransform getOlTransform() {
        return olTransform;
    }

    public MeshRenderer getMeshRenderer() {
        return meshRenderer;
    }
}

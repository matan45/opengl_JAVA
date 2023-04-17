package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.draw.EditorRenderer;
import app.renderer.particle.ParticleMaterial;
import app.renderer.particle.ParticleRenderer;
import app.renderer.pbr.Mesh;
import app.utilities.OpenFileDialog;
import app.utilities.resource.ResourceManager;
import app.utilities.serialize.FileExtension;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class ParticleComponent extends Component {
    private final OLTransform olTransform;

    private final ParticleRenderer particleRenderer;
    private final ParticleMaterial particleMaterial;

    private String path = "";
    private String prePath = "";
    private File file;

    public ParticleComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
        particleRenderer = EditorRenderer.getParticleRenderer().createNewInstant();
        particleMaterial = particleRenderer.getMaterial();
        file = new File("");
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Mesh"))
            path = OpenFileDialog.openFile(FileExtension.MESH_EXTENSION.getFileName()).orElse(Path.of(prePath)).toString();


        if (!path.isEmpty() && !prePath.equals(path))
            init(false);


        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.textWrapped("Material");
        ImGui.separator();
        ImGui.columns(3, "", true);

        particleMaterial.setAlbedoMap(materialPath("Albedo"));
        ImGui.nextColumn();
        ImGui.textWrapped(particleMaterial.getAlbedoFileName());
        ImGui.nextColumn();
        ImGui.pushID("particleAlbedo");
        if (ImGui.button("X"))
            particleMaterial.albedoMapRemove();
        ImGui.popID();

        ImGui.nextColumn();
        particleMaterial.setNormalMap(materialPath("Normal"));
        ImGui.nextColumn();
        ImGui.textWrapped(particleMaterial.getNormalFileName());
        ImGui.nextColumn();
        ImGui.pushID("particleNormal");
        if (ImGui.button("X"))
            particleMaterial.normalMapRemove();
        ImGui.popID();

        ImGui.columns(1);

    }

    private void init(boolean useTransform) {
        prePath = path;
        file = new File(path);
        Mesh mesh = ResourceManager.loadMeshFromFile(Path.of(path));
        if (!useTransform)
            olTransform.setPosition(mesh.center());

        particleRenderer.init(mesh, olTransform);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String materialPath(String buttonName) {
        if (ImGui.button(buttonName)) {
            Optional<Path> materialPath = OpenFileDialog.openFile("png,tga,jpg");
            return materialPath.orElse(Path.of("")).toString();
        }
        return "";
    }


    @Override
    public void cleanUp() {

    }
}

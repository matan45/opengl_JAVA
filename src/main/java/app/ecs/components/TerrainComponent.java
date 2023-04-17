package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainMaterial;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class TerrainComponent extends Component {
    private final TerrainQuadtreeRenderer terrain;
    private final ImBoolean wireframe;
    private final TerrainMaterial material;

    private String path = "";
    private String prePath = "";
    private File file;

    public TerrainComponent(Entity ownerEntity) {
        super(ownerEntity);
        terrain = EditorRenderer.getTerrainQuadtreeRenderer();
        wireframe = new ImBoolean();
        file = new File("");
        material=terrain.getTerrainMaterial();
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Height Map"))
            path = OpenFileDialog.openFile("png,jpg").orElse(Path.of(prePath)).toString();

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
            terrain.init(Path.of(path));
            terrain.setActive(true);
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.pushID("Displacement");
        if (ImGui.button("Displacement"))
            terrain.setDisplacementFactor(200f);
        ImGui.sameLine();
        float[] displacementValue = {terrain.getDisplacementFactor()};
        ImGui.dragFloat("##Y", displacementValue, 0.1f);
        terrain.setDisplacementFactor(displacementValue[0]);
        ImGui.popID();

        ImGui.textWrapped("RenderDepth: " + terrain.getRenderDepth());
        ImGui.textWrapped("Number of Terrain Nodes: " + terrain.getNumTerrainNodes());

        ImGui.checkbox("Wireframe", wireframe);
        terrain.setWireframe(wireframe.get());

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

        ImGui.columns(1);
    }

    @Override
    public void cleanUp() {
        terrain.setActive(false);
    }

    public ImBoolean getWireframe() {
        return wireframe;
    }

    public TerrainQuadtreeRenderer getTerrain() {
        return terrain;
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

}

package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.io.File;
import java.nio.file.Path;

public class TerrainComponent extends Component {
    private final TerrainQuadtreeRenderer terrain;
    private final ImBoolean wireframe;

    private String path = "";
    private String prePath = "";
    private File file;

    public TerrainComponent(Entity ownerEntity) {
        super(ownerEntity);
        terrain = EditorRenderer.getTerrainQuadtreeRenderer();
        wireframe = new ImBoolean();
        file = new File("");
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


}

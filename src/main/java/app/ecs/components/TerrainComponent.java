package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainMaterial;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.TerrainSculptingIntegration;
import app.utilities.OpenFileDialog;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
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
    
    private int paintChannel = 0;
    private boolean paintMode = false;

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
            path = OpenFileDialog.openFile("png,jpg","Texture").orElse(Path.of(prePath)).toString();

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
        
        // --- Texture Splatting & Painting ---
        ImGui.separator();
        ImGui.text("Texture Splatting");
        
        if (ImGui.checkbox("Paint Mode", paintMode)) {
            paintMode = !paintMode;
            // Disable sculpting if painting is enabled
            if (paintMode) {
                if (ownerEntity.hasComponent(TerrainSculptingComponent.class)) {
                    ownerEntity.getComponent(TerrainSculptingComponent.class).setActive(false);
                }
            }
        }

        ImGui.separator();
        
        for (int i = 0; i < 4; i++) {
            ImGui.pushID("Layer" + i);
            
            if (ImGui.radioButton("Select Layer " + (i + 1), paintChannel == i)) {
                paintChannel = i;
            }
            
            ImGui.columns(3, "LayerCols" + i, true);
            
            // Albedo
            if (ImGui.button("Albedo")) {
                Optional<Path> p = OpenFileDialog.openFile("png,tga,jpg", "Texture");
                if (p.isPresent()) material.setAlbedoMap(i, p.get().toString());
            }
            ImGui.nextColumn();
            ImGui.textWrapped(material.getAlbedoFileName(i).isEmpty() ? "Default" : material.getAlbedoFileName(i));
            ImGui.nextColumn();
            if (ImGui.button("X##Alb")) material.removeAlbedoMap(i);
            
            ImGui.nextColumn(); // Next row
            
            // Normal
            if (ImGui.button("Normal")) {
                Optional<Path> p = OpenFileDialog.openFile("png,tga,jpg", "Texture");
                if (p.isPresent()) material.setNormalMap(i, p.get().toString());
            }
            ImGui.nextColumn();
            ImGui.textWrapped(material.getNormalFileName(i).isEmpty() ? "Default" : material.getNormalFileName(i));
            ImGui.nextColumn();
            if (ImGui.button("X##Norm")) material.removeNormalMap(i);
            
            ImGui.columns(1);
            ImGui.separator();
            ImGui.popID();
        }

        // Terrain Sculpting Section
        ImGui.separator();
        ImGui.text("Terrain Sculpting");
        
        boolean hasSculpting = ownerEntity.hasComponent(TerrainSculptingComponent.class);
        TerrainSculptingComponent sculptingComponent = ownerEntity.getComponent(TerrainSculptingComponent.class);
        
        if (!hasSculpting) {
            if (ImGui.button("Enable Sculpting", 200, 30)) {
                enableTerrainSculpting();
            }
            ImGui.textDisabled("Click to enable terrain sculpting tools");
        } else {
            // Sculpting is enabled - show quick controls
            boolean isActive = sculptingComponent.isActive();
            
            if (isActive) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.2f, 0.2f, 1.0f);
                if (ImGui.button("Disable Sculpting", 150, 25)) {
                    sculptingComponent.setActive(false);
                }
                ImGui.popStyleColor();
            } else {
                ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.8f, 0.2f, 1.0f);
                if (ImGui.button("Activate Sculpting", 150, 25)) {
                    sculptingComponent.setActive(true);
                    // Disable paint mode if sculpting is activated
                    paintMode = false; 
                }
                ImGui.popStyleColor();
            }
            
            // Show quick stats
            if (isActive) {
                ImGui.text("Status: Active");
                ImGui.text("Modifications: " + sculptingComponent.getModificationsCount());
                ImGui.text("Tool: " + sculptingComponent.getBrushSettings().getBrushType().getDisplayName());
            } else {
                ImGui.textDisabled("Status: Paused");
            }
        }
    }
    
    private void enableTerrainSculpting() {
        // Add sculpting component to this entity
        TerrainSculptingComponent sculptingComponent = new TerrainSculptingComponent(ownerEntity);
        ownerEntity.addComponent(sculptingComponent);
        
        // Enable sculpting renderer support
        terrain.enableSculpting();
        
        // Get the global sculpting integration and register this entity
        TerrainSculptingIntegration sculptingIntegration = EditorRenderer.getSculptingIntegration();
        if (sculptingIntegration != null) {
            sculptingIntegration.enableSculptingForTerrain(ownerEntity);
        }
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
            Optional<Path> materialPath = OpenFileDialog.openFile("png,tga,jpg","Texture");
            return materialPath.orElse(Path.of("")).toString();
        }
        return "";
    }
    
    public boolean isPaintMode() {
        return paintMode;
    }
    
    public int getPaintChannel() {
        return paintChannel;
    }

}


package app.ecs.components;

import app.ecs.Entity;
import app.editor.imgui.ImguiLayerHandler;
import app.renderer.draw.EditorRenderer;
import app.renderer.terrain.TerrainMaterial;
import app.renderer.terrain.TerrainQuadtreeRenderer;
import app.renderer.terrain.sculpting.TerrainDataManager;
import app.renderer.terrain.sculpting.TerrainSculptingIntegration;
import app.utilities.logger.LogInfo;
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
    private TerrainDataManager dataManager;

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
        
        // Terrain Sculpting Section
        ImGui.separator();
        ImGui.text("🏔️ Terrain Sculpting");
        
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
                }
                ImGui.popStyleColor();
            }
            
            ImGui.sameLine();
            if (ImGui.button("Open Sculpting Window", 150, 25)) {
                openSculptingWindow();
            }
            
            // Show quick stats
            if (isActive) {
                ImGui.text("Status: ✅ Active");
                ImGui.text("Modifications: " + sculptingComponent.getModificationsCount());
                ImGui.text("Tool: " + sculptingComponent.getBrushSettings().getBrushType().getDisplayName());
            } else {
                ImGui.textDisabled("Status: ⏸️ Paused");
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
    
    private void openSculptingWindow() {
        try {
            TerrainSculptingIntegration sculptingIntegration = EditorRenderer.getSculptingIntegration();
            if (sculptingIntegration == null) {
                LogInfo.println("ERROR: SculptingIntegration is null - not initialized properly");
                return;
            }
            
            if (!sculptingIntegration.isInitialized()) {
                LogInfo.println("ERROR: SculptingIntegration is not initialized - attempting to initialize");
                EditorRenderer.initializeSculpting();
                if (!sculptingIntegration.isInitialized()) {
                    LogInfo.println("ERROR: Failed to initialize SculptingIntegration");
                    return;
                }
            }
            
            if (sculptingIntegration.getSculptingWindow() == null) {
                LogInfo.println("ERROR: SculptingWindow is null after initialization");
                return;
            }
            
            // Set window as open
            sculptingIntegration.getSculptingWindow().setOpen(true);
            
            // Add the window to ImguiLayerHandler - now thread-safe with duplicate check
            ImguiLayerHandler.addLayer(sculptingIntegration.getSculptingWindow());
            
            LogInfo.println("Sculpting window opened successfully");
            
        } catch (Exception e) {
            LogInfo.println("ERROR: Exception while opening sculpting window: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ImGui color constants
    private static class ImGuiCol {
        static final int Button = 21;
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

    public TerrainDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(TerrainDataManager dataManager) {
        this.dataManager = dataManager;
    }

    private String materialPath(String buttonName) {
        if (ImGui.button(buttonName)) {
            Optional<Path> materialPath = OpenFileDialog.openFile("png,tga,jpg","Texture");
            return materialPath.orElse(Path.of("")).toString();
        }
        return "";
    }

}

package app.ecs.components;

import app.ecs.Entity;
import app.renderer.terrain.sculpting.BrushSettings;
import app.renderer.terrain.sculpting.BrushType;
import app.renderer.terrain.sculpting.FalloffType;
import app.renderer.terrain.sculpting.BrushShape;
import app.math.OLVector3f;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class TerrainSculptingComponent extends Component {
    private boolean isActive = false;
    private final BrushSettings brushSettings;
    private final OLVector3f lastSculptPosition;
    private boolean isCurrentlySculpting = false;
    private float sculptingTimer = 0.0f;
    private int modificationsCount = 0;

    public TerrainSculptingComponent(Entity ownerEntity) {
        super(ownerEntity);
        this.brushSettings = new BrushSettings();
        this.lastSculptPosition = new OLVector3f();
    }

    @Override
    public void update(float dt) {
        if (isCurrentlySculpting) {
            sculptingTimer += dt;
        }
    }

    @Override
    public void imguiDraw() {
        ImGui.text("Terrain Sculpting");
        ImGui.separator();

        ImBoolean activeRef = new ImBoolean(isActive);
        if (ImGui.checkbox("Enable Sculpting", activeRef)) {
            isActive = activeRef.get();
        }

        if (!isActive) {
            ImGui.textDisabled("Sculpting is disabled");
            return;
        }

        ImGui.spacing();
        ImGui.text("Tools");

        for (BrushType type : BrushType.values()) {
            boolean selected = brushSettings.getBrushType() == type;
            if (ImGui.radioButton(type.getIcon() + " " + type.getDisplayName(), selected)) {
                brushSettings.setBrushType(type);
            }
        }

        ImGui.spacing();
        ImGui.text("Brush Settings");

        ImFloat sizeRef = new ImFloat(brushSettings.getSize());
        if (ImGui.sliderFloat("Size", sizeRef.getData(), 1.0f, 200.0f, "%.1f")) {
            brushSettings.setSize(sizeRef.get());
        }

        ImFloat strengthRef = new ImFloat(brushSettings.getStrength());
        if (ImGui.sliderFloat("Strength", strengthRef.getData(), 0.01f, 2.0f, "%.2f")) {
            brushSettings.setStrength(strengthRef.get());
        }

        ImFloat falloffRef = new ImFloat(brushSettings.getFalloff());
        if (ImGui.sliderFloat("FalloffAmount", falloffRef.getData(), 0.1f, 1.0f, "%.2f")) {
            brushSettings.setFalloff(falloffRef.get());
        }

        if (brushSettings.getBrushType() == BrushType.FLATTEN) {
            ImFloat targetHeightRef = new ImFloat(brushSettings.getTargetHeight());
            if (ImGui.sliderFloat("Target Height", targetHeightRef.getData(), -100.0f, 100.0f, "%.1f")) {
                brushSettings.setTargetHeight(targetHeightRef.get());
            }
        }

        ImGui.spacing();
        ImGui.text("Shape");

        ImInt shapeRef = new ImInt(brushSettings.getShape().ordinal());
        String[] shapeDisplayNames = {"Circle"};

        if (ImGui.combo("Shape2", shapeRef, shapeDisplayNames)) {
            brushSettings.setShape(BrushShape.values()[shapeRef.get()]);
        }

        ImGui.spacing();
        ImGui.text("Falloff Type");

        ImInt falloffTypeRef = new ImInt(brushSettings.getFalloffType().ordinal());
        String[] falloffDisplayNames = {"Linear", "Smooth", "Gaussian", "Polynomial"};

        if (ImGui.combo("Falloff", falloffTypeRef, falloffDisplayNames)) {
            brushSettings.setFalloffType(FalloffType.values()[falloffTypeRef.get()]);
        }

        ImBoolean showPreviewRef = new ImBoolean(brushSettings.isShowPreview());
        if (ImGui.checkbox("Show Preview", showPreviewRef)) {
            brushSettings.setShowPreview(showPreviewRef.get());
        }

        ImGui.spacing();
        ImGui.text("Statistics");
        ImGui.textDisabled("Modifications: " + modificationsCount);
        ImGui.textDisabled("Sculpting Time: " + String.format("%.1f", sculptingTimer) + "s");

        if (lastSculptPosition.length() > 0) {
            ImGui.textDisabled(String.format("Last Position: (%.1f, %.1f, %.1f)", 
                lastSculptPosition.x, lastSculptPosition.y, lastSculptPosition.z));
        }
    }

    @Override
    public void cleanUp() {
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public BrushSettings getBrushSettings() { return brushSettings; }

    public OLVector3f getLastSculptPosition() { return lastSculptPosition; }
    public void setLastSculptPosition(OLVector3f position) { 
        this.lastSculptPosition.x = position.x;
        this.lastSculptPosition.y = position.y;
        this.lastSculptPosition.z = position.z;
    }

    public boolean isCurrentlySculpting() { return isCurrentlySculpting; }
    public void setCurrentlySculpting(boolean sculpting) { 
        this.isCurrentlySculpting = sculpting;
        if (!sculpting) {
            sculptingTimer = 0.0f;
        }
    }

    public void incrementModifications() { modificationsCount++; }
    public int getModificationsCount() { return modificationsCount; }
    public void resetModificationsCount() { modificationsCount = 0; }
}
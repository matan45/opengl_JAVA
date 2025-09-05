package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.ecs.components.TerrainComponent;
import app.ecs.components.TerrainSculptingComponent;
import app.ecs.systems.SculptingSystem;
import app.renderer.terrain.sculpting.*;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.util.List;

public class TerrainSculptingWindow implements ImguiLayer {
    private boolean isOpen = true;
    private SculptingSystem sculptingSystem;
    private EntitySystem entitySystem;
    private boolean showAdvancedSettings = false;
    private boolean showPerformanceMetrics = true;
    private boolean showHistory = true;
    private float windowWidth = 350.0f;

    public TerrainSculptingWindow(SculptingSystem sculptingSystem, EntitySystem entitySystem) {
        this.sculptingSystem = sculptingSystem;
        this.entitySystem = entitySystem;
        LogInfo.println("TerrainSculptingWindow initialized");
    }

    @Override
    public void render(float dt) {
        if (!isOpen) return;

        ImGui.setNextWindowSize(windowWidth, 600);
        ImGui.setNextWindowPos(10, 50, 0);

        int windowFlags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize;
        
        if (ImGui.begin("🏔️ Terrain Sculpting", new ImBoolean(isOpen), windowFlags)) {
            renderToolSelection();
            ImGui.separator();
            
            renderBrushSettings();
            ImGui.separator();
            
            if (ImGui.collapsingHeader("🔧 Advanced Settings", showAdvancedSettings ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
                renderAdvancedSettings();
            }
            ImGui.separator();
            
            if (ImGui.collapsingHeader("📊 Performance", showPerformanceMetrics ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
                renderPerformanceMetrics();
            }
            
            if (ImGui.collapsingHeader("📜 History", showHistory ? ImGuiTreeNodeFlags.DefaultOpen : 0)) {
                renderHistoryControls();
            }
            
            ImGui.separator();
            renderKeyboardShortcuts();
            
            ImGui.end();
        }
    }

    private void renderToolSelection() {
        ImGui.text("🛠️ Sculpting Tools");
        ImGui.spacing();

        TerrainSculptingComponent sculptingComponent = getActiveSculptingComponent();
        if (sculptingComponent == null) {
            ImGui.textDisabled("No terrain selected for sculpting");
            return;
        }

        BrushSettings brush = sculptingComponent.getBrushSettings();
        BrushType currentType = brush.getBrushType();

        ImGui.columns(2, "tools", false);
        
        for (BrushType type : BrushType.values()) {
            boolean selected = (currentType == type);
            String buttonText = type.getIcon() + " " + type.getDisplayName();
            
            if (selected) {
                ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
            }
            
            if (ImGui.button(buttonText, 150, 25)) {
                brush.setBrushType(type);
            }
            
            if (selected) {
                ImGui.popStyleColor(2);
            }
            
            if ((type.ordinal() + 1) % 2 == 0) {
                ImGui.nextColumn();
            }
        }
        
        ImGui.columns(1);
    }

    private void renderBrushSettings() {
        ImGui.text("🖌️ Brush Configuration");
        ImGui.spacing();

        TerrainSculptingComponent sculptingComponent = getActiveSculptingComponent();
        if (sculptingComponent == null) return;

        BrushSettings brush = sculptingComponent.getBrushSettings();

        // Brush Size
        ImFloat sizeRef = new ImFloat(brush.getSize());
        if (ImGui.sliderFloat("Size", sizeRef.getData(), 1.0f, 200.0f, "%.1f units")) {
            brush.setSize(sizeRef.get());
        }
        ImGui.sameLine();
        if (ImGui.button("Reset##size")) {
            brush.setSize(50.0f);
        }

        // Brush Strength
        ImFloat strengthRef = new ImFloat(brush.getStrength());
        if (ImGui.sliderFloat("Strength", strengthRef.getData(), 0.01f, 2.0f, "%.2f")) {
            brush.setStrength(strengthRef.get());
        }
        ImGui.sameLine();
        if (ImGui.button("Reset##strength")) {
            brush.setStrength(0.5f);
        }

        // Brush Falloff
        ImFloat falloffRef = new ImFloat(brush.getFalloff());
        if (ImGui.sliderFloat("Falloff", falloffRef.getData(), 0.1f, 1.0f, "%.2f")) {
            brush.setFalloff(falloffRef.get());
        }
        ImGui.sameLine();
        if (ImGui.button("Reset##falloff")) {
            brush.setFalloff(0.8f);
        }

        // Target Height (for flatten tool)
        if (brush.getBrushType() == BrushType.FLATTEN) {
            ImFloat targetHeightRef = new ImFloat(brush.getTargetHeight());
            if (ImGui.sliderFloat("Target Height", targetHeightRef.getData(), -100.0f, 100.0f, "%.1f")) {
                brush.setTargetHeight(targetHeightRef.get());
            }
            ImGui.sameLine();
            if (ImGui.button("Reset##target")) {
                brush.setTargetHeight(0.0f);
            }
        }

        // Preview toggle
        ImBoolean showPreviewRef = new ImBoolean(brush.isShowPreview());
        if (ImGui.checkbox("Show Brush Preview", showPreviewRef)) {
            brush.setShowPreview(showPreviewRef.get());
        }
        
        // Show brush preview info in UI instead of 3D rendering
        if (brush.isShowPreview()) {
            ImGui.spacing();
            ImGui.text("🖌️ Brush Preview (Console Output):");
            ImGui.text("Position tracking in console logs");
            ImGui.text("Brush follows mouse over terrain");
        }
    }

    private void renderAdvancedSettings() {
        TerrainSculptingComponent sculptingComponent = getActiveSculptingComponent();
        if (sculptingComponent == null) return;

        BrushSettings brush = sculptingComponent.getBrushSettings();

        // Brush Shape
        ImInt shapeRef = new ImInt(brush.getShape().ordinal());
        String[] shapeNames = {"Circle", "Square"};
        if (ImGui.combo("Shape", shapeRef, shapeNames)) {
            brush.setShape(BrushShape.values()[shapeRef.get()]);
        }

        // Falloff Type
        ImInt falloffTypeRef = new ImInt(brush.getFalloffType().ordinal());
        String[] falloffNames = {"Linear", "Smooth", "Gaussian", "Polynomial"};
        if (ImGui.combo("Falloff Curve", falloffTypeRef, falloffNames)) {
            brush.setFalloffType(FalloffType.values()[falloffTypeRef.get()]);
        }

        ImGui.spacing();
        if (ImGui.button("Reset All Settings", 200, 25)) {
            brush.setSize(50.0f);
            brush.setStrength(0.5f);
            brush.setFalloff(0.8f);
            brush.setTargetHeight(0.0f);
            brush.setShape(BrushShape.CIRCLE);
            brush.setFalloffType(FalloffType.SMOOTH);
        }
    }

    private void renderPerformanceMetrics() {
        TerrainSculptingComponent sculptingComponent = getActiveSculptingComponent();
        if (sculptingComponent == null) {
            ImGui.textDisabled("No active sculpting component");
            return;
        }

        ImGui.text("Modifications: " + sculptingComponent.getModificationsCount());
        ImGui.text("Currently Sculpting: " + (sculptingComponent.isCurrentlySculpting() ? "Yes" : "No"));
        
        // Get terrain data manager metrics
        TerrainDataManager dataManager = getTerrainDataManager(sculptingComponent);
        if (dataManager != null) {
            ImGui.text("Memory Usage: " + String.format("%.2f MB", getMemoryUsage(dataManager)));
            ImGui.text("Terrain Size: " + dataManager.getTerrainWidth() + "x" + dataManager.getTerrainHeight());
        }

        ImGui.spacing();
        if (ImGui.button("Reset Counters", 150, 20)) {
            sculptingComponent.resetModificationsCount();
        }
    }

    private void renderHistoryControls() {
        TerrainSculptingComponent sculptingComponent = getActiveSculptingComponent();
        TerrainDataManager dataManager = getTerrainDataManager(sculptingComponent);
        
        if (dataManager == null) {
            ImGui.textDisabled("No terrain data manager available");
            return;
        }

        ImGui.columns(3, "history", false);
        
        boolean canUndo = dataManager.canUndo();
        boolean canRedo = dataManager.canRedo();
        
        if (!canUndo) ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        if (ImGui.button("Undo (Ctrl+Z)", 100, 25) && canUndo) {
            dataManager.undo();
        }
        if (!canUndo) ImGui.popStyleVar();
        
        ImGui.nextColumn();
        
        if (!canRedo) ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        if (ImGui.button("Redo (Ctrl+Y)", 100, 25) && canRedo) {
            dataManager.redo();
        }
        if (!canRedo) ImGui.popStyleVar();
        
        ImGui.nextColumn();
        
        if (ImGui.button("Clear All", 100, 25)) {
            dataManager.clearModifications();
            sculptingComponent.resetModificationsCount();
        }
        
        ImGui.columns(1);
    }

    private void renderKeyboardShortcuts() {
        if (ImGui.collapsingHeader("⌨️ Keyboard Shortcuts")) {
            ImGui.text("1-5: Select tool (Raise/Lower/Smooth/Flatten/Noise)");
            ImGui.text("Mouse Wheel: Adjust brush size");
            ImGui.text("Ctrl + Mouse Wheel: Adjust brush strength");
            ImGui.text("Alt + Mouse Wheel: Adjust brush falloff");
            ImGui.text("Shift + Drag: Invert tool operation");
            ImGui.text("Ctrl + Drag: Force smooth operation");
            ImGui.text("Ctrl + Z: Undo");
            ImGui.text("Ctrl + Y: Redo");
        }
    }

    private TerrainSculptingComponent getActiveSculptingComponent() {
        if (sculptingSystem == null) return null;
        
        List<Entity> sculptingEntities = sculptingSystem.getSculptingEntities();
        if (sculptingEntities.isEmpty()) return null;
        
        for (Entity entity : sculptingEntities) {
            TerrainSculptingComponent component = entity.getComponent(TerrainSculptingComponent.class);
            if (component != null && component.isActive()) {
                return component;
            }
        }
        
        return sculptingEntities.get(0).getComponent(TerrainSculptingComponent.class);
    }

    private TerrainDataManager getTerrainDataManager(TerrainSculptingComponent sculptingComponent) {
        return null;
    }

    private float getMemoryUsage(TerrainDataManager dataManager) {
        return (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    private static class ImGuiTreeNodeFlags {
        static final int DefaultOpen = 32;
    }
    
    private static class ImGuiCol {
        static final int Button = 21;
        static final int ButtonHovered = 22;
    }
    
    private static class ImGuiStyleVar {
        static final int Alpha = 2;
    }
}
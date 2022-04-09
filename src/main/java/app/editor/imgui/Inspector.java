package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.components.*;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

public class Inspector implements ImguiLayer {
    private Entity entity = null;
    private final List<Component> removeComponent = new ArrayList<>();
    private static final ImBoolean headerClose = new ImBoolean(true);

    @Override
    public void render(float dt) {
        if (ImGui.begin("Inspector") && entity != null) {
            for (Component component : entity.getComponents()) {
                ImGui.pushID(component.getClass().getSimpleName());

                boolean nodeHeader;
                if (component instanceof TransformComponent) {
                    ImGui.pushID(component.hashCode());
                    nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);
                    ImGui.popID();
                } else
                    nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", headerClose, ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);

                if (!headerClose.get()) {
                    removeComponent.add(component);
                    headerClose.set(true);
                }
                if (nodeHeader)
                    component.imguiDraw();
                ImGui.popID();
            }
            if (!removeComponent.isEmpty()) {
                for (Component component : removeComponent) {
                    component.cleanUp();
                    entity.removeComponent(component.getClass());
                }
                removeComponent.clear();
            }

            if (ImGui.beginPopupContextWindow("Component")) {
                if (ImGui.beginMenu("Light")) {
                    if (ImGui.menuItem("Directional Light"))
                        entity.addComponent(DirectionalLightComponent.class);
                    if (ImGui.menuItem("Point Light"))
                        entity.addComponent(PointLightComponent.class);
                    if (ImGui.menuItem("Spot Light"))
                        entity.addComponent(SpotLightComponent.class);
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Audio")) {
                    if (ImGui.menuItem("Sound Effect"))
                        entity.addComponent(SoundEffectComponent.class);
                    if (ImGui.menuItem("Music"))
                        entity.addComponent(MusicComponent.class);
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Sky Box"))
                    entity.addComponent(SkyBoxComponent.class);
                if (ImGui.menuItem("Mesh"))
                    entity.addComponent(MeshComponent.class);
                if (ImGui.menuItem("Terrain"))
                    entity.addComponent(TerrainComponent.class);
                if (ImGui.menuItem("Fog"))
                    entity.addComponent(FogComponent.class);
                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

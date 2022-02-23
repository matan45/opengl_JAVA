package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.components.*;
import app.utilities.logger.LogInfo;
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
                if (component instanceof TransformComponent)
                    nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);
                else
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
                        entity.addComponent(new DirectionalLightComponent(entity));
                    if (ImGui.menuItem("Point Light"))
                        LogInfo.println("not implement");
                    if (ImGui.menuItem("Spot Light"))
                        LogInfo.println("not implement");
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Sky Box"))
                    entity.addComponent(new SkyBoxComponent(entity));
                if (ImGui.menuItem("Mesh"))
                    entity.addComponent(new MeshComponent(entity));
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

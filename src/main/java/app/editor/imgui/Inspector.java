package app.editor.imgui;

import app.ecs.Component;
import app.ecs.Entity;
import app.ecs.components.TransformComponent;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

public class Inspector implements ImguiLayer {
    Entity entity = null;
    List<Component> removeComponent = new ArrayList<>();
    static final ImBoolean headerClose = new ImBoolean(true);

    @Override
    public void render() {
        if (ImGui.begin("Inspector") && entity != null) {
            for (Component component : entity.getComponents()) {
                ImGui.pushID(component.getClass().getSimpleName());

                boolean nodeHeader;
                if (component instanceof TransformComponent)
                    nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);
                else
                    nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", headerClose, ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);

                if (!headerClose.get())
                    removeComponent.add(component);
                if (nodeHeader)
                    component.imguiDraw();
                ImGui.popID();
            }
            if (!removeComponent.isEmpty()) {
                for (Component component : removeComponent)
                    entity.removeComponent(component.getClass());
                removeComponent.clear();
            }

            if (ImGui.beginPopupContextWindow("Component")) {
                if (ImGui.beginMenu("Light")) {
                    if (ImGui.menuItem("Directional Light"))
                        LogInfo.println("not implement");
                    if (ImGui.menuItem("Point Light"))
                        LogInfo.println("not implement");
                    if (ImGui.menuItem("Spot Light"))
                        LogInfo.println("not implement");
                    ImGui.endMenu();
                }
                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

    @Override
    public void cleanUp() {

    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

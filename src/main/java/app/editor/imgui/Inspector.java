package app.editor.imgui;

import app.ecs.Component;
import app.ecs.Entity;
import app.ecs.components.TransformComponent;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.ArrayList;
import java.util.List;

public class Inspector implements ImguiLayer {
    Entity entity = null;
    List<Component> removeComponent = new ArrayList<>();

    @Override
    public void render() {
        if (ImGui.begin("Inspector") && entity != null) {
            for (Component component : entity.getComponents()) {
                ImGui.pushID(component.getClass().getSimpleName());
                boolean nodeHeader = ImGui.collapsingHeader(component.getClass().getSimpleName() + "\t", ImGuiTreeNodeFlags.AllowItemOverlap | ImGuiTreeNodeFlags.FramePadding);
                if (!(component instanceof TransformComponent)) {
                    ImGui.sameLine();
                    if (ImGui.button("X"))
                        removeComponent.add(component);
                }
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
                if (ImGui.menuItem("Add Component"))
                    LogInfo.println("not implemented");
                ImGui.endPopup();
            }
        }

        ImGui.end();
    }

    @Override
    public void cleanUp() {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

package app.editor.imgui;

import app.ecs.Component;
import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.utilities.logger.LogInfo;
import imgui.ImGui;

public class Inspector implements ImguiLayer {
    Entity entity = null;

    @Override
    public void render() {
        if (ImGui.begin("Inspector")) {
            if (entity != null) {
                entity.getComponents().forEach(Component::imguiDraw);
                if (ImGui.beginPopupContextWindow("Component")) {
                    if (ImGui.menuItem("Add Component")) {
                        LogInfo.println("add custom components");
                    }
                    ImGui.endPopup();
                }
            }
        }
        ImGui.end();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

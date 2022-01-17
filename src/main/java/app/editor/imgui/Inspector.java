package app.editor.imgui;

import app.ecs.Component;
import app.ecs.Entity;
import app.utilities.logger.LogInfo;
import imgui.ImGui;

public class Inspector implements ImguiLayer {
    Entity entity = null;

    @Override
    public void render() {
        if (ImGui.begin("Inspector")) {
            if (entity != null) {
                for (Component component : entity.getComponents()) {
                    if (ImGui.collapsingHeader(component.getName())) {
                        if (ImGui.button("Remove component"))
                            LogInfo.println("not implemented");
                        ImGui.separator();
                        component.imguiDraw();
                    }
                }

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

    @Override
    public void cleanUp() {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}

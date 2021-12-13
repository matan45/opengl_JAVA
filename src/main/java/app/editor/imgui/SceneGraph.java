package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.utilities.logger.LogInfo;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class SceneGraph implements ImguiLayer {

    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Scene Hierarchy")) {
            int index = 0;
            for (Entity entity : EntitySystem.getEntities()) {
                boolean treeNodeOpen = doTreeNode(entity, index);
                if (treeNodeOpen)
                    ImGui.treePop();

                index++;
            }

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Game Object")) {
                    EntitySystem.addEntity(new Entity("Default Name"));
                }
                if (ImGui.menuItem("Remove Game Object")) {
                    //TODO HOW TO REMOVE ENTITY
                    LogInfo.println("not ready");
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }


    private boolean doTreeNode(Entity entity, int index) {
        ImGui.pushID(index);
        boolean treeNodeOpen = ImGui.treeNodeEx(entity.getName(),
                ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
                entity.getName());

        ImGui.popID();

        return treeNodeOpen;
    }
}

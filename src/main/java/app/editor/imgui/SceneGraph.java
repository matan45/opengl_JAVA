package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class SceneGraph implements ImguiLayer {
    boolean isRemove = false;
    int indexRemove = 0;
    Inspector inspector;

    public SceneGraph(Inspector inspector) {
        this.inspector = inspector;
    }

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

            if (ImGui.beginPopupContextWindow("Entity")) {
                if (ImGui.menuItem("Add Game Object")) {
                    EntitySystem.addEntity(new Entity("Default Name"));
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();

        if (isRemove) {
            isRemove = false;
            EntitySystem.removeEntity(indexRemove);
            inspector.setEntity(null);
        }
    }


    private boolean doTreeNode(Entity entity, int index) {
        ImGui.pushID(index);
        if (ImGui.button("-")) {
            isRemove = true;
            indexRemove = index;
        }
        ImGui.sameLine();
        if (ImGui.isMouseClicked(0))
            inspector.setEntity(entity);

        boolean treeNodeOpen = ImGui.treeNodeEx(entity.getName(), ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth);

        ImGui.popID();

        return treeNodeOpen;
    }

}

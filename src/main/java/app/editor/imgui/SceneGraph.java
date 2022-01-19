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

    @Override
    public void cleanUp() {

    }


    private boolean doTreeNode(Entity entity, int index) {
        ImGui.pushID(index);
        if (ImGui.button("-")) {
            isRemove = true;
            indexRemove = index;
        }
        if (ImGui.isItemHovered())
            ImGui.setTooltip("Remove Component");
        ImGui.sameLine();
        boolean treeNodeOpen = ImGui.treeNodeEx(entity.getName(), ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.AllowItemOverlap);

        if (ImGui.isItemClicked(0))
            inspector.setEntity(entity);
        ImGui.popID();

        return treeNodeOpen;
    }

}

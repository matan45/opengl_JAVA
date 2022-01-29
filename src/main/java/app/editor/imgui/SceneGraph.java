package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneGraph implements ImguiLayer {
    int indexRemove = 0;
    Inspector inspector;
    int selectionNode = -1;

    public SceneGraph() {
        this.inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);
    }

    @Override
    public void render() {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Scene Hierarchy")) {
            ImGui.text("Scene Name " + "todo scene class");
            ImGui.separator();
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 159, 100);
            List<Entity> componentList = EntitySystem.getEntities();
            for (int index = 0; index < componentList.size(); index++) {
                boolean treeNodeOpen = doTreeNode(componentList.get(index), index);
                if (treeNodeOpen)
                    ImGui.treePop();
            }
            ImGui.popStyleColor();

            if (ImGui.beginPopupContextWindow("Entity", ImGuiPopupFlags.MouseButtonRight)) {
                if (ImGui.menuItem("Add Game Object")) {
                    EntitySystem.addEntity(new Entity("Default Name"));
                } else if (selectionNode != -1) {
                    if (ImGui.menuItem("Remove Game Object")) {
                        EntitySystem.removeEntity(indexRemove);
                        inspector.setEntity(null);
                        selectionNode = -1;
                    }
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();

    }

    private boolean doTreeNode(Entity entity, int index) {
        boolean treeNodeOpen;
        if (index == selectionNode)
            treeNodeOpen = ImGui.treeNodeEx(index, ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.Selected, entity.getName());
        else
            treeNodeOpen = ImGui.treeNodeEx(index, ImGuiTreeNodeFlags.OpenOnArrow, entity.getName());

        if (ImGui.isItemActive()) {
            inspector.setEntity(entity);
            selectionNode = index;
        }

        return treeNodeOpen;
    }

}

package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.math.components.OLTransform;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class SceneGraph implements ImguiLayer {
    private final Inspector inspector;
    private Entity selectionNode;

    public SceneGraph() {
        this.inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);
    }

    @Override
    public void render(float dt) {
        ImGui.setNextWindowSize(200, 200);
        if (ImGui.begin("Scene Hierarchy")) {
            ImGui.text("Scene Name " + "todo scene class");
            ImGui.separator();
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 159, 100);

            List<Entity> entitiesArray = EntitySystem.getEntitiesFather();
            for (Entity entity : entitiesArray) {
                boolean treeNodeOpen = doTreeNode(entity, ImGuiTreeNodeFlags.OpenOnArrow);
                if (treeNodeOpen) {
                    if (entity.hasChildren())
                        doTreeNodeChildren(entity);
                    ImGui.treePop();
                }
            }
            ImGui.popStyleColor();

            menuPopUp();

        }
        ImGui.end();
    }

    private void dragAndDrop() {
        if (selectionNode != null && ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(DragAndDrop.ENTITY.getType(), selectionNode, ImGuiCond.Once);
            ImGui.text(selectionNode.getName());
            ImGui.endDragDropSource();
        }
    }

    private void menuPopUp() {
        if (ImGui.beginPopupContextWindow("Entity", ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Add Game Object"))
                EntitySystem.addEntity(new Entity("Default Name", new OLTransform()));
            else if (selectionNode != null && ImGui.menuItem("Add Children") && selectionNode.getFather() == null) {
                EntitySystem.addEntityChildren(selectionNode, new Entity("Default Name", new OLTransform()));
            } else if (selectionNode != null && ImGui.menuItem("Remove Game Object")) {
                EntitySystem.removeEntity(selectionNode);
                inspector.setEntity(null);
                selectionNode = null;
            }
            ImGui.endPopup();
        }
    }

    private boolean doTreeNode(Entity entity, int flag) {
        boolean treeNodeOpen;
        if (entity == selectionNode)
            treeNodeOpen = ImGui.treeNodeEx(entity.getUuid(), flag | ImGuiTreeNodeFlags.Selected, entity.getName());
        else
            treeNodeOpen = ImGui.treeNodeEx(entity.getUuid(), flag, entity.getName());

        if (ImGui.isItemActive()) {
            inspector.setEntity(entity);
            selectionNode = entity;
        }

        dragAndDrop();

        return treeNodeOpen;
    }

    private void doTreeNodeChildren(Entity entity) {
        for (Entity entitySon : entity.getChildren())
            if (doTreeNode(entitySon, ImGuiTreeNodeFlags.Leaf))
                ImGui.treePop();
    }

}

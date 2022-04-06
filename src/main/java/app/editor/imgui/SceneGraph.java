package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.math.components.OLTransform;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;
import java.util.Objects;

public class SceneGraph implements ImguiLayer {
    private final Inspector inspector;
    private int selectionNode = -1;

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

            List<Entity> entitiesArray = EntitySystem.getEntitiesArray();
            for (int index = 0; index < entitiesArray.size(); index++) {
                Entity entity = entitiesArray.get(index);
                boolean treeNodeOpen = doTreeNode(entity, index);
                if (treeNodeOpen) {
                    if (entity.hasChildren()) {
                        doTreeNodeChildren(entity);
                        ImGui.treePop();
                    }
                    ImGui.treePop();
                }
            }
            ImGui.popStyleColor();

            if (ImGui.beginPopupContextWindow("Entity", ImGuiPopupFlags.MouseButtonRight)) {
                if (ImGui.menuItem("Add Game Object"))
                    EntitySystem.addEntity(new Entity("Default Name", new OLTransform()));
                else if (selectionNode != -1 && ImGui.menuItem("Add Children")) {
                    EntitySystem.addEntityChildren(selectionNode, new Entity("Default Name", new OLTransform()));
                } else if (selectionNode != -1 && ImGui.menuItem("Remove Game Object")) {
                    EntitySystem.removeEntity(selectionNode);
                    inspector.setEntity(null);
                    selectionNode = -1;
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

    private void doTreeNodeChildren(Entity entity) {
        for (int i = 0; i < entity.getChildren().size(); i++) {
            Entity entitySon = entity.getChildren().get(i);
            ImGui.treeNodeEx(Objects.hashCode(entitySon), ImGuiTreeNodeFlags.Leaf, entitySon.getName());
            if (ImGui.isItemActive()) {
                inspector.setEntity(entitySon);
                //selectionNode = index;
            }
        }


    }

}

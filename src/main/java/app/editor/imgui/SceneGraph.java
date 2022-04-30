package app.editor.imgui;

import app.ecs.Entity;
import app.ecs.EntitySystem;
import app.editor.component.SceneHandler;
import app.math.components.OLTransform;
import app.renderer.Textures;
import app.renderer.draw.EditorRenderer;
import app.utilities.serialize.Serializable;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImString;

import java.nio.file.Path;
import java.util.List;

public class SceneGraph implements ImguiLayer {
    private final Inspector inspector;
    private Entity selectionNode;
    private boolean firstTime;
    private String preSceneName;
    private final int pencil;

    public SceneGraph() {
        Textures textures = EditorRenderer.getTextures();
        pencil = textures.loadTexture(Path.of("src\\main\\resources\\editor\\icons\\sceneGraph\\pencil.png"));
        firstTime = false;
        inspector = ImguiLayerHandler.getImguiLayer(Inspector.class);
    }

    @Override
    public void render(float dt) {
        if (ImGui.begin("Scene Hierarchy")) {
            if (ImGui.imageButton(pencil, 15, 15))
                ImGui.openPopup("Rename Scene Name");

            ImGui.sameLine();
            ImGui.text("Scene: " + SceneHandler.getActiveScene().getName());
            ImGui.separator();
            ImGui.pushStyleColor(ImGuiCol.Header, 255, 255, 159, 100);

            modal();

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

    private void dragAndDropEntity() {
        if (selectionNode != null && ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(DragAndDrop.SAVE_ENTITY.getType(), selectionNode, ImGuiCond.Once);
            ImGui.text(selectionNode.getName());
            ImGui.endDragDropSource();
        }
    }

    private void menuPopUp() {
        if (ImGui.beginPopupContextWindow("Entity", ImGuiPopupFlags.MouseButtonRight)) {
            if (ImGui.menuItem("Add Game Object"))
                EntitySystem.addEntity(new Entity("Default Name", new OLTransform()));

            else if (selectionNode != null) {
                if (selectionNode.getFather() == null && ImGui.menuItem("Add Children"))
                    EntitySystem.addEntityChildren(selectionNode, new Entity("Default Name", new OLTransform()));
                else if (ImGui.menuItem("Remove Game Object")) {
                    EntitySystem.removeEntity(selectionNode);
                    inspector.setEntity(null);
                    selectionNode = null;
                } else if (!selectionNode.getPath().isEmpty() && ImGui.menuItem("Update prefab"))
                    Serializable.saveEntity(selectionNode);
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

        dragAndDropEntity();

        return treeNodeOpen;
    }

    private void doTreeNodeChildren(Entity entity) {
        for (Entity entitySon : entity.getChildren())
            if (doTreeNode(entitySon, ImGuiTreeNodeFlags.Leaf))
                ImGui.treePop();
    }

    private void modal() {
        // Always center this window when appearing
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);
        if (ImGui.beginPopupModal("Rename Scene Name", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("The new name to be set");
            ImGui.separator();

            if (!firstTime) {
                preSceneName = String.copyValueOf(SceneHandler.getActiveScene().getName().toCharArray());
                firstTime = true;
            }
            ImString sceneName = new ImString(SceneHandler.getActiveScene().getName(), 256);
            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 0);
            if (ImGui.inputText("##", sceneName, ImGuiInputTextFlags.CallbackCharFilter))
                SceneHandler.getActiveScene().setName(sceneName.get());

            ImGui.popStyleVar();

            if (ImGui.button("OK", 120, 0)) {
                firstTime = false;
                ImGui.closeCurrentPopup();
            }
            ImGui.setItemDefaultFocus();
            ImGui.sameLine();
            if (ImGui.button("Cancel", 120, 0)) {
                firstTime = false;
                SceneHandler.getActiveScene().setName(preSceneName);
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }

}

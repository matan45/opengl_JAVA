package app.ecs.components;

import app.ecs.Entity;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.math.components.OLTransform;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;

public final class TransformComponent extends CommonComponent {
    OLTransform olTransform;
    OLVector2f buttonSize;
    ImString entityName;


    public TransformComponent(Entity ownerEntity, OLTransform olTransform) {
        super(ownerEntity);
        this.olTransform = olTransform;
        buttonSize = new OLVector2f();
        entityName = new ImString(ownerEntity.getName(), 256);
    }


    public OLTransform getOlTransform() {
        return olTransform;
    }

    @Override
    public void imguiDraw() {
        ImGui.text("Entity Name");
        if (ImGui.inputText("##", entityName))
            ownerEntity.setName(entityName.get());

        drawVector3("Position", olTransform.getPosition(), 0.0f);
        drawVector3("Rotation", olTransform.getRotation(), 0.0f);
        drawVector3("Scale", olTransform.getScale(), 1.0f);

    }

    private void drawVector3(String title, OLVector3f olVector3f, float resetValue) {
        ImGui.pushID(title);
        ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
        ImGui.text(title);
        ImGui.popStyleColor();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, 0, 0);
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 0.2f;
        buttonSize.x = lineHeight + 3.0f;
        buttonSize.y = lineHeight;
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x) / 3.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X"))
            olVector3f.x = resetValue;
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] xValue = {olVector3f.x};
        ImGui.dragFloat("##X", xValue, 0.1f);
        ImGui.popItemWidth();

        ImGui.sameLine();
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.8f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.2f, 0.9f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.8f, 0.15f, 1.0f);
        if (ImGui.button("Y"))
            olVector3f.y = resetValue;
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] yValue = {olVector3f.y};
        ImGui.dragFloat("##Y", yValue, 0.1f);
        ImGui.popItemWidth();

        ImGui.sameLine();
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.15f, 0.1f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.2f, 0.9f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.15f, 0.8f, 1.0f);
        if (ImGui.button("Z"))
            olVector3f.z = resetValue;
        ImGui.popStyleColor(3);
        ImGui.sameLine();
        float[] zValue = {olVector3f.z};
        ImGui.dragFloat("##Z", zValue, 0.1f);
        ImGui.popItemWidth();

        olVector3f.setOLVector3f(xValue[0], yValue[0], zValue[0]);

        ImGui.popStyleVar();
        ImGui.popID();
    }

}

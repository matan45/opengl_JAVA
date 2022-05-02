package app.utilities;

import app.math.OLVector2f;
import app.math.OLVector3f;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

public class ImguiUtil {

    private static final OLVector2f buttonSize = new OLVector2f();

    public static void drawVector3(String title, OLVector3f olVector3f, float resetValueX,float resetValueY,float resetValueZ) {
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
            olVector3f.x = resetValueX;
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
            olVector3f.y = resetValueY;
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
            olVector3f.z = resetValueZ;
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

package app.editor.component;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;

public class StyleWindow {

    private final StyleMaterial styleMaterial;
    private final StyleMaterial.Style textStyle;
    private final StyleMaterial.Style textDisabled;

    public StyleWindow() {
        styleMaterial = new StyleMaterial();
        //styleMaterial.createMaterialObject(Path.of(""));
        textStyle = getStyle(ImGuiCol.Text);
        textDisabled = getStyle(ImGuiCol.TextDisabled);
    }

    public void showStyleWindow(ImBoolean styleWindowBoolean) {
        if (ImGui.begin("Style", styleWindowBoolean)) {
            ImGui.pushID("StyleText");
            float[] colorText = {textStyle.getR(), textStyle.getG(), textStyle.getB(), textStyle.getA()};
            ImGui.labelText("##Y", "Color Text");
            ImGui.colorEdit4("##Y", colorText, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop | ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreview);
            textStyle.setR(colorText[0]);
            textStyle.setG(colorText[1]);
            textStyle.setB(colorText[2]);
            textStyle.setA(colorText[3]);
            ImGui.popID();

            ImGui.pushID("StyleTextDisabled");
            float[] colorTextDisabled = {textDisabled.getR(), textDisabled.getG(), textDisabled.getB(), textDisabled.getA()};
            ImGui.labelText("##Y", "Color Text Disabled");
            ImGui.colorEdit4("##Y", colorTextDisabled, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop | ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreview);
            textDisabled.setR(colorTextDisabled[0]);
            textDisabled.setG(colorTextDisabled[1]);
            textDisabled.setB(colorTextDisabled[2]);
            textDisabled.setA(colorTextDisabled[3]);
            ImGui.popID();
            ImGui.separator();
            if (ImGui.button("SAVE")) {
                // Code to execute when button 1 is clicked
            }
            // Align the second button to the right side
            ImGui.sameLine(ImGui.getWindowWidth() - ImGui.getStyle().getItemSpacing().x - ImGui.calcTextSize("LOAD").x);
            if (ImGui.button("LOAD")) {
                // Code to execute when button 2 is clicked
            }
        }
        ImGui.end();
    }

    private StyleMaterial.Style getStyle(int flag) {
        return styleMaterial.getStyleList().stream()
                .filter(s -> s.getStyleFlag() == flag).findFirst().orElse(new StyleMaterial.Style());
    }
}

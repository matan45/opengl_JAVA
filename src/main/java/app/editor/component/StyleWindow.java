package app.editor.component;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;

import java.nio.file.Path;

public class StyleWindow {
    private final StyleMaterial styleMaterial;
    private final StyleMaterial.Style textStyle;
    private final StyleMaterial.Style textDisabled;
    private static final String STYLE_PATH = "src\\main\\resources\\editor\\style.config";

    public StyleWindow() {
        styleMaterial = new StyleMaterial();
        styleMaterial.createMaterialObject(Path.of(STYLE_PATH));
        textStyle = getStyle(ImGuiCol.Text);
        textDisabled = getStyle(ImGuiCol.TextDisabled);
    }

    public void showStyleWindow(ImBoolean styleWindowBoolean) {
        if (ImGui.begin("Style", styleWindowBoolean)) {
            ImGui.pushID("StyleText");
            float[] colorText = {textStyle.getR(), textStyle.getG(), textStyle.getB(), textStyle.getA()};
            ImGui.labelText("##Y", "Color Text");
            ImGui.colorEdit4("##Y", colorText, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop | ImGuiColorEditFlags.AlphaBar);
            textStyle.setR(colorText[0]);
            textStyle.setG(colorText[1]);
            textStyle.setB(colorText[2]);
            textStyle.setA(colorText[3]);
            ImGui.popID();

            ImGui.pushID("StyleTextDisabled");
            float[] colorTextDisabled = {textDisabled.getR(), textDisabled.getG(), textDisabled.getB(), textDisabled.getA()};
            ImGui.labelText("##Y", "Color Text Disabled");
            ImGui.colorEdit4("##Y", colorTextDisabled, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop | ImGuiColorEditFlags.AlphaBar);
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

    public void theme() {
        ImGuiStyle style = ImGui.getStyle();
        styleMaterial.getStyleList().forEach(s -> style.setColor(s.getStyleFlag(), s.getR(), s.getG(), s.getB(), s.getA()));
    }

    private StyleMaterial.Style getStyle(int flag) {
        return styleMaterial.getStyleList().stream()
                .filter(s -> s.getStyleFlag() == flag).findFirst().orElse(null);
    }
}

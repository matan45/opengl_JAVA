package app.editor.component;

import imgui.ImGui;
import imgui.ImGuiStyle;
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
        style.setColor(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.13f, 0.14f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.ChildBg, 0.13f, 0.14f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.PopupBg, 0.13f, 0.14f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.Border, 0.43f, 0.43f, 0.50f, 0.50f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBg, 0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.38f, 0.38f, 0.38f, 1.00f);
        style.setColor(ImGuiCol.FrameBgActive, 0.67f, 0.67f, 0.67f, 0.39f);
        style.setColor(ImGuiCol.TitleBg, 0.08f, 0.08f, 0.09f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.08f, 0.08f, 0.09f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);
        style.setColor(ImGuiCol.CheckMark, 0.11f, 0.64f, 0.92f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.11f, 0.64f, 0.92f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.08f, 0.50f, 0.72f, 1.00f);
        style.setColor(ImGuiCol.Button, 0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.ButtonHovered, 0.38f, 0.38f, 0.38f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive, 0.67f, 0.67f, 0.67f, 0.39f);
        style.setColor(ImGuiCol.Header, 0.22f, 0.22f, 0.22f, 1.00f);
        style.setColor(ImGuiCol.HeaderHovered, 0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.HeaderActive, 0.67f, 0.67f, 0.67f, 0.39f);
        style.setColor(ImGuiCol.Separator, 0.43f, 0.43f, 0.50f, 0.50f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.41f, 0.42f, 0.44f, 1.00f);
        style.setColor(ImGuiCol.SeparatorActive, 0.26f, 0.59f, 0.98f, 0.95f);
        style.setColor(ImGuiCol.ResizeGrip, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.29f, 0.30f, 0.31f, 0.67f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        style.setColor(ImGuiCol.Tab, 0.08f, 0.08f, 0.09f, 0.83f);
        style.setColor(ImGuiCol.TabHovered, 0.33f, 0.34f, 0.36f, 0.83f);
        style.setColor(ImGuiCol.TabActive, 0.23f, 0.23f, 0.24f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocused, 0.08f, 0.08f, 0.09f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.13f, 0.14f, 0.15f, 1.00f);
        style.setColor(ImGuiCol.DockingPreview, 0.26f, 0.59f, 0.98f, 0.70f);
        style.setColor(ImGuiCol.DockingEmptyBg, 0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        style.setColor(ImGuiCol.DragDropTarget, 0.11f, 0.64f, 0.92f, 1.00f);
        style.setColor(ImGuiCol.NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(ImGuiCol.NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);

       /* StyleMaterial styleMaterial = new StyleMaterial();
        List<StyleMaterial.Style> styleList = new ArrayList<>();
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleList.add(new StyleMaterial.Style(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f));
        styleMaterial.setStyleList(styleList);
        System.out.println(styleMaterial);*/
        //TODO: read from a file the values

    }

    private StyleMaterial.Style getStyle(int flag) {
        return styleMaterial.getStyleList().stream()
                .filter(s -> s.getStyleFlag() == flag).findFirst().orElse(new StyleMaterial.Style());
    }
}

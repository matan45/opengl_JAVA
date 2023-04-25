package app.editor.component;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImBoolean;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StyleWindow {
    private final StyleMaterial styleMaterial;
    private static final String STYLE_PATH = "src\\main\\resources\\editor\\style.config";


    private final StyleMaterial.Style textStyle;
    private final StyleMaterial.Style textDisabled;
    private final StyleMaterial.Style childBg;
    private final StyleMaterial.Style popupBg;
    private final StyleMaterial.Style border;
    private final StyleMaterial.Style borderShadow;
    private final StyleMaterial.Style frameBg;
    private final StyleMaterial.Style frameBgHovered;
    private final StyleMaterial.Style frameBgActive;
    private final StyleMaterial.Style titleBg;
    private final StyleMaterial.Style titleBgActive;
    private final StyleMaterial.Style menuBarBg;
    private final StyleMaterial.Style scrollbarBg;
    private final StyleMaterial.Style scrollbarGrab;
    private final StyleMaterial.Style scrollbarGrabHovered;
    private final StyleMaterial.Style scrollbarGrabActive;
    private final StyleMaterial.Style checkMark;
    private final StyleMaterial.Style sliderGrab;
    private final StyleMaterial.Style sliderGrabActive;
    private final StyleMaterial.Style button;
    private final StyleMaterial.Style buttonHovered;
    private final StyleMaterial.Style buttonActive;
    private final StyleMaterial.Style header;
    private final StyleMaterial.Style headerHovered;
    private final StyleMaterial.Style headerActive;
    private final StyleMaterial.Style separator;
    private final StyleMaterial.Style separatorHovered;
    private final StyleMaterial.Style separatorActive;
    private final StyleMaterial.Style resizeGrip;
    private final StyleMaterial.Style resizeGripHovered;
    private final StyleMaterial.Style resizeGripActive;
    private final StyleMaterial.Style tap;
    private final StyleMaterial.Style tabHovered;
    private final StyleMaterial.Style tabActive;
    private final StyleMaterial.Style tabUnfocused;
    private final StyleMaterial.Style tabUnfocusedActive;
    private final StyleMaterial.Style dockingPreview;
    private final StyleMaterial.Style dockingEmptyBg;
    private final StyleMaterial.Style plotLines;
    private final StyleMaterial.Style plotLinesHovered;
    private final StyleMaterial.Style plotHistogram;
    private final StyleMaterial.Style plotHistogramHovered;
    private final StyleMaterial.Style textSelectedBg;
    private final StyleMaterial.Style dragDropTarget;
    private final StyleMaterial.Style navHighlight;
    private final StyleMaterial.Style navWindowingHighlight;
    private final StyleMaterial.Style navWindowingDimBg;
    private final StyleMaterial.Style modalWindowDimBg;


    public StyleWindow() {
        styleMaterial = new StyleMaterial();
        styleMaterial.createMaterialObject(Path.of(STYLE_PATH));
        Map<Integer, StyleMaterial.Style> mapStyle = styleMaterial.getStyleList()
                .stream().collect(Collectors.toMap(StyleMaterial.Style::getStyleFlag, Function.identity()));
        textStyle = mapStyle.get(ImGuiCol.Text);
        textDisabled = mapStyle.get(ImGuiCol.WindowBg);
        childBg = mapStyle.get(ImGuiCol.ChildBg);
        popupBg = mapStyle.get(ImGuiCol.PopupBg);
        border = mapStyle.get(ImGuiCol.Border);
        borderShadow = mapStyle.get(ImGuiCol.BorderShadow);
        frameBg = mapStyle.get(ImGuiCol.FrameBg);
        frameBgHovered = mapStyle.get(ImGuiCol.FrameBgHovered);
        frameBgActive = mapStyle.get(ImGuiCol.FrameBgActive);
        titleBg = mapStyle.get(ImGuiCol.TitleBg);
        titleBgActive = mapStyle.get(ImGuiCol.TitleBgActive);
        menuBarBg = mapStyle.get(ImGuiCol.MenuBarBg);
        scrollbarBg = mapStyle.get(ImGuiCol.ScrollbarBg);
        scrollbarGrab = mapStyle.get(ImGuiCol.ScrollbarGrab);
        scrollbarGrabHovered = mapStyle.get(ImGuiCol.ScrollbarGrabHovered);
        scrollbarGrabActive = mapStyle.get(ImGuiCol.ScrollbarGrabActive);
        checkMark = mapStyle.get(ImGuiCol.CheckMark);
        sliderGrab = mapStyle.get(ImGuiCol.SliderGrab);
        sliderGrabActive = mapStyle.get(ImGuiCol.SliderGrabActive);
        button = mapStyle.get(ImGuiCol.Button);
        buttonHovered = mapStyle.get(ImGuiCol.ButtonHovered);
        buttonActive = mapStyle.get(ImGuiCol.ButtonActive);
        header = mapStyle.get(ImGuiCol.Header);
        headerHovered = mapStyle.get(ImGuiCol.HeaderHovered);
        headerActive = mapStyle.get(ImGuiCol.HeaderActive);
        separator = mapStyle.get(ImGuiCol.Separator);
        separatorHovered = mapStyle.get(ImGuiCol.SeparatorHovered);
        separatorActive = mapStyle.get(ImGuiCol.SeparatorActive);
        resizeGrip = mapStyle.get(ImGuiCol.ResizeGrip);
        resizeGripHovered = mapStyle.get(ImGuiCol.ResizeGripHovered);
        resizeGripActive = mapStyle.get(ImGuiCol.ResizeGripActive);
        tap = mapStyle.get(ImGuiCol.Tab);
        tabHovered = mapStyle.get(ImGuiCol.TabHovered);
        tabActive = mapStyle.get(ImGuiCol.TabActive);
        tabUnfocused = mapStyle.get(ImGuiCol.TabUnfocused);
        tabUnfocusedActive = mapStyle.get(ImGuiCol.TabUnfocusedActive);
        dockingPreview = mapStyle.get(ImGuiCol.DockingPreview);
        dockingEmptyBg = mapStyle.get(ImGuiCol.DockingEmptyBg);
        plotLines = mapStyle.get(ImGuiCol.PlotLines);
        plotLinesHovered = mapStyle.get(ImGuiCol.PlotLinesHovered);
        plotHistogram = mapStyle.get(ImGuiCol.PlotHistogram);
        plotHistogramHovered = mapStyle.get(ImGuiCol.PlotHistogramHovered);
        textSelectedBg = mapStyle.get(ImGuiCol.TextSelectedBg);
        dragDropTarget = mapStyle.get(ImGuiCol.DragDropTarget);
        navHighlight = mapStyle.get(ImGuiCol.NavHighlight);
        navWindowingHighlight = mapStyle.get(ImGuiCol.NavWindowingHighlight);
        navWindowingDimBg = mapStyle.get(ImGuiCol.NavWindowingDimBg);
        modalWindowDimBg = mapStyle.get(ImGuiCol.ModalWindowDimBg);
    }

    public void showStyleWindow(ImBoolean styleWindowBoolean) {
        if (ImGui.begin("Style", styleWindowBoolean)) {
            showColorPick("StyleText", textStyle, "Text");
            showColorPick("StyleTextDisabled", textDisabled, "Text Disabled");
            showColorPick("StyleChildBackground", childBg, "Child Background");
            showColorPick("StylePopupBackground", popupBg, "Popup Background");
            showColorPick("StyleBorder", border, "Border");
            showColorPick("StyleBorderShadow", borderShadow, "Border Shadow");
            showColorPick("StyleFrameBackground", frameBg, "Frame Background");
            showColorPick("StyleFrameBackgroundHovered", frameBgHovered, "Frame Background Hovered");
            showColorPick("StyleFrameBackgroundActive", frameBgActive, "Frame Background Active");
            showColorPick("StyleTitleBackground", titleBg, "Title Background");
            showColorPick("StyleTitleBackgroundActive", titleBgActive, "Title Background Active");
            showColorPick("StyleMenuBarBackground", menuBarBg, "Menu Bar Background");
            showColorPick("StyleScrollBarBackground", scrollbarBg, "Scroll Bar Background");
            showColorPick("StyleScrollBarGrab", scrollbarGrab, "Scroll Bar Grab");
            showColorPick("StyleScrollBarGrabHovered", scrollbarGrabHovered, "Scroll Bar Grab Hovered");
            showColorPick("StyleScrollBarGrabActive", scrollbarGrabActive, "Scroll Bar Grab Active");
            showColorPick("StyleCheckMark", checkMark, "Check Mark");
            showColorPick("StyleSliderGrab", sliderGrab, "Slider Grab");
            showColorPick("StyleSliderGrabActive", sliderGrabActive, "Slider Grab Active");
            showColorPick("StyleButton", button, "Button");
            showColorPick("StyleButtonHovered", buttonHovered, "Button Hovered");
            showColorPick("StyleButtonActive", buttonActive, "Button Active");
            showColorPick("StyleHeader", header, "Header");
            showColorPick("StyleHeaderHovered", headerHovered, "Header Hovered");
            showColorPick("StyleHeaderActive", headerActive, "Header Active");
            showColorPick("StyleSeparator", separator, "Separator");
            showColorPick("StyleSeparatorHovered", separatorHovered, "Separator Hovered");
            showColorPick("StyleSeparatorActive", separatorActive, "Separator Active");
            showColorPick("StyleResizeGrip", resizeGrip, "Resize Grip");
            showColorPick("StyleResizeGripHovered", resizeGripHovered, "Resize Grip Hovered");
            showColorPick("StyleResizeGripActive", resizeGripActive, "Resize Grip Active");
            showColorPick("StyleTap", tap, "Tap");
            showColorPick("StyleTapHovered", tabHovered, "Tap Hovered");
            showColorPick("StyleTapActive", tabActive, "Tap Active");
            showColorPick("StyleTapUnfocused", tabUnfocused, "Tap Unfocused");
            showColorPick("StyleTapUnfocusedActive", tabUnfocusedActive, "Tap Unfocused Active");
            showColorPick("StyleDockingPreview", dockingPreview, "Docking Preview");
            showColorPick("StyleDockingEmptyBackground", dockingEmptyBg, "Docking Empty Background");
            showColorPick("StylePlotLines", plotLines, "Plot Lines");
            showColorPick("StylePlotLinesHovered", plotLinesHovered, "Plot Lines Hovered");
            showColorPick("StylePlotHistogram", plotHistogram, "Plot Histogram");
            showColorPick("StylePlotHistogramHovered", plotHistogramHovered, "Plot Histogram Hovered");
            showColorPick("StyleTextSelectedBackground", textSelectedBg, "Text Selected Background");
            showColorPick("StyleDragDropTarget", dragDropTarget, "Drag And Drop Target");
            showColorPick("StyleNavHighlight", navHighlight, "Navigation Highlight");
            showColorPick("StyleNavWindowingHighlight", navWindowingHighlight, "Navigation Window Highlight");
            showColorPick("StyleNavWindowingDimBg", navWindowingDimBg, "Navigation Window Dimension Background");
            showColorPick("StyleModalWindowDimBg", modalWindowDimBg, "Modal Window Dimension Background");

            ImGui.separator();
            if (ImGui.button("SAVE")) {
                theme();
                styleMaterial.staveToFile(Path.of(STYLE_PATH));
            }
        }
        ImGui.end();
    }

    private void showColorPick(String textId, StyleMaterial.Style style, String textLabel) {
        ImGui.pushID(textId);
        float[] colorText = {style.getR(), style.getG(), style.getB(), style.getA()};
        ImGui.labelText("##Y", textLabel);
        ImGui.colorEdit4("##Y", colorText, ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoDragDrop | ImGuiColorEditFlags.AlphaBar);
        style.setR(colorText[0]);
        style.setG(colorText[1]);
        style.setB(colorText[2]);
        style.setA(colorText[3]);
        ImGui.popID();
    }

    public void theme() {
        ImGuiStyle style = ImGui.getStyle();
        styleMaterial.getStyleList().forEach(s -> style.setColor(s.getStyleFlag(), s.getR(), s.getG(), s.getB(), s.getA()));
    }
}

package app.editor.imgui;

import app.utilities.resource.ResourceManager;
import imgui.*;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.nio.file.Paths;

public class ImguiHandler {
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;
    private final long windowHandle;
    private final String glslVersion;
    private static final int FONT_SIZE = 20;

    public ImguiHandler(String glslVersion, long windowHandle) {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        this.glslVersion = glslVersion;
        this.windowHandle = windowHandle;
        init();
    }

    private void init() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        theme();

        // You can use the ImFontGlyphRangesBuilder helper to create glyph ranges based on text input.
        // For example: for a game where your script is known, if you can feed your entire script to it (using addText) and only build the characters the game needs.
        // Here we are using it just to combine all required glyphs in one place
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(FontAwesomeIcons.ICON_RANGE);

        io.getFonts().addFontFromMemoryTTF(ResourceManager.loadFromResources(Paths.get("src\\main\\resources\\editor\\Roboto-Regular.ttf")), FONT_SIZE); // font awesome
        // Font config for additional fonts
        // This is a natively allocated struct so don't forget to call destroy after atlas is built
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setMergeMode(true);  // Enable merge mode to merge cyrillic, japanese and icons with default font

        final short[] glyphRanges = rangesBuilder.buildRanges();
        io.getFonts().addFontFromMemoryTTF(ResourceManager.loadFromResources(Paths.get("src\\main\\resources\\editor\\fa-solid-900.ttf")), FONT_SIZE, fontConfig, glyphRanges); // font awesome
        io.getFonts().build();

        fontConfig.destroy();
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(glslVersion);
    }

    public void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        ImGuizmo.beginFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }


    public void disposeImGui() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void theme() {
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
        style.setGrabRounding(2.3f);
        style.setFrameRounding(2.3f);

        //TODO: read from a file the values

    }
}

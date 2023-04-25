package app.editor.imgui;

import app.utilities.resource.ResourceManager;
import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.implot.ImPlot;
import imgui.extension.implot.ImPlotContext;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.nio.file.Paths;

public class ImguiHandler {
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;
    private final long windowHandle;
    private final String glslVersion;

    private final ImPlotContext imPlotContext;
    private static final int FONT_SIZE = 20;

    public ImguiHandler(String glslVersion, long windowHandle) {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        this.glslVersion = glslVersion;
        this.windowHandle = windowHandle;
        init();
        imPlotContext = ImPlot.createContext();
    }

    private void init() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);

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
        ImPlot.destroyContext(imPlotContext);
        ImGui.destroyContext();
    }

}

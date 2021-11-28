package app.editor.imgui;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImguiHandler {
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;
    private final long windowHandle;
    private final String glslVersion;
    private static final int FONTSIZE = 20;

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
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable | ImGuiConfigFlags.DockingEnable);
        theme();

        // You can use the ImFontGlyphRangesBuilder helper to create glyph ranges based on text input.
        // For example: for a game where your script is known, if you can feed your entire script to it (using addText) and only build the characters the game needs.
        // Here we are using it just to combine all required glyphs in one place
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        io.getFonts().addFontFromMemoryTTF(loadFromResources("src\\main\\resources\\editor\\COOKiE MILK Regular.ttf"), FONTSIZE); // font awesome
        // Font config for additional fonts
        // This is a natively allocated struct so don't forget to call destroy after atlas is built
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setMergeMode(true);  // Enable merge mode to merge cyrillic, japanese and icons with default font

        final short[] glyphRanges = rangesBuilder.buildRanges();
        io.getFonts().addFontFromMemoryTTF(loadFromResources("src\\main\\resources\\editor\\fa-solid-900.ttf"), FONTSIZE, fontConfig, glyphRanges); // font awesome
        io.getFonts().addFontFromMemoryTTF(loadFromResources("src\\main\\resources\\editor\\fa-regular-400.ttf"), FONTSIZE, fontConfig, glyphRanges); // font awesome
        io.getFonts().build();

        fontConfig.destroy();
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(glslVersion);
    }

    public void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    private static byte[] loadFromResources(String name) {
        try {
            return Files.readAllBytes(Paths.get(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disposeImGui() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void theme() {
        ImGuiStyle style = ImGui.getStyle();
        //TODO: read from a file the values
        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setWindowMinSize(300, 300);

        style.setFramePadding(8, 6);
        style.setColor(ImGuiCol.TitleBg, 255, 101, 53, 255);
        style.setColor(ImGuiCol.TitleBgActive, 255, 101, 53, 255);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0, 0, 0, 130);

        style.setColor(ImGuiCol.Button, 31, 30, 31, 255);
        style.setColor(ImGuiCol.ButtonHovered, 41, 40, 41, 255);
        style.setColor(ImGuiCol.ButtonActive, 31, 30, 31, 130);

        style.setColor(ImGuiCol.Header, 0, 0, 0, 0);
        style.setColor(ImGuiCol.HeaderActive, 0, 0, 255, 255);
        style.setColor(ImGuiCol.HeaderHovered, 255, 0, 0, 255);

        style.setColor(ImGuiCol.Border, 0, 0, 200, 255);
        style.setColor(ImGuiCol.BorderShadow, 255, 0, 0, 255);

        style.setColor(ImGuiCol.WindowBg, 211, 211, 211, 100);
        style.setColor(ImGuiCol.DockingPreview, 0, 0, 200, 255);


    }
}

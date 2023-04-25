package app.editor.component;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class StyleWindow {

    public void showStyleWindow(ImBoolean styleWindowBoolean) {
        if (ImGui.begin("Style", styleWindowBoolean)) {

        }
        ImGui.end();
    }
}

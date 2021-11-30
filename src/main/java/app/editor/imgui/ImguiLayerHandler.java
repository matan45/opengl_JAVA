package app.editor.imgui;

import java.util.ArrayList;
import java.util.List;

public class ImguiLayerHandler {
    //TODO add event system observer pattern
    private static final List<ImguiLayer> imguiLayerList = new ArrayList<>();

    private ImguiLayerHandler() {
    }

    public static void renderImGui() {
        for (ImguiLayer imguiLayer : imguiLayerList)
            imguiLayer.render();
    }

    public static void addLayer(ImguiLayer layer) {
        imguiLayerList.add(layer);
    }

    public static void removeLayer(ImguiLayer layer) {
        imguiLayerList.remove(layer);
    }
}
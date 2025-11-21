package app.editor.imgui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImguiLayerHandler {
    private static final List<ImguiLayer> imguiLayerList = new CopyOnWriteArrayList<>();

    private ImguiLayerHandler() {
    }

    public static void renderImGui(float dt) {
        // CopyOnWriteArrayList allows safe iteration while the list is being modified
        for (ImguiLayer imguiLayer : imguiLayerList) {
            imguiLayer.render(dt);
        }
    }

    public static <T extends ImguiLayer> T getImguiLayer(Class<T> imguiLayerClass) {
        for (ImguiLayer c : imguiLayerList) {
            if (imguiLayerClass.isAssignableFrom(c.getClass())) {
                try {
                    return imguiLayerClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                    System.exit(-1);
                }
            }
        }

        return null;
    }

    public static void addLayer(ImguiLayer layer) {
        if (layer != null && !imguiLayerList.contains(layer)) {
            imguiLayerList.add(layer);
            System.out.println("Added ImGui layer: " + layer.getClass().getSimpleName());
        }
    }

    public static void removeLayer(ImguiLayer layer) {
        if (layer != null && imguiLayerList.remove(layer)) {
            System.out.println("Removed ImGui layer: " + layer.getClass().getSimpleName());
        }
    }

}

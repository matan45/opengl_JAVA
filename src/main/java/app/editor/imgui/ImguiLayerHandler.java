package app.editor.imgui;

import java.util.ArrayList;
import java.util.List;

public class ImguiLayerHandler {
    private static final List<ImguiLayer> imguiLayerList = new ArrayList<>();

    private ImguiLayerHandler() {
    }

    public static void renderImGui(float dt) {
        for (ImguiLayer imguiLayer : imguiLayerList)
            imguiLayer.render(dt);
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
        imguiLayerList.add(layer);
    }

    public static void removeLayer(ImguiLayer layer) {
        imguiLayerList.remove(layer);
    }

}

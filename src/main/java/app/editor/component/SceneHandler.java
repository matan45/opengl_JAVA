package app.editor.component;

import app.editor.imgui.ContentBrowser;
import app.editor.imgui.ImguiLayerHandler;

public class SceneHandler {
    private SceneHandler() {
    }

    private static Scene activeScene = new Scene();

    public static Scene getActiveScene() {
        return activeScene;
    }

    public static void setActiveScene(Scene activeScene) {
        SceneHandler.activeScene = activeScene;
        ImguiLayerHandler.getImguiLayer(ContentBrowser.class).setAbsolutePath(activeScene.getPath());
    }
}

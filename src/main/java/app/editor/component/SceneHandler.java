package app.editor.component;

public class SceneHandler {
    private SceneHandler(){}

    private static Scene activeScene = new Scene();

    public static Scene getActiveScene() {
        return activeScene;
    }

    public static void setActiveScene(Scene activeScene) {
        SceneHandler.activeScene = activeScene;
    }
}

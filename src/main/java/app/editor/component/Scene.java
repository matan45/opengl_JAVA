package app.editor.component;

public class Scene {

    private static Scene scene = new Scene();

    private String name;
    private String path;

    private Scene() {
        name = "default scene";
        path = "";
    }

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        Scene.scene = scene;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

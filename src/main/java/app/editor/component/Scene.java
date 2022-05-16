package app.editor.component;

import java.nio.file.Path;

public class Scene {

    private String name;
    private Path path;

    public Scene() {
        name = "default scene";
        path = Path.of(System.getProperty("user.dir"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }


}

package app.utilities.serialize;

public enum FileExtension {
    PREFAB_EXTENSION("prefab"),
    SCENE_EXTENSION("scene");

    String file;

    FileExtension(String file) {
        this.file = file;
    }

    public String getFileName() {
        return file;
    }
}

package app.utilities.serialize;

public enum FileExtension {
    PREFAB_EXTENSION("prefab"),
    MESH_EXTENSION("mesh"),
    SCENE_EXTENSION("scene");

    private final String file;

    FileExtension(String file) {
        this.file = file;
    }

    public String getFileName() {
        return file;
    }
}

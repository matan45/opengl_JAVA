package app.editor.imgui;

public enum DragAndDrop {
    SAVE_ENTITY("saveEntity"),
    LOAD_ENTITY("loadEntity");

    private final String type;

    DragAndDrop(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

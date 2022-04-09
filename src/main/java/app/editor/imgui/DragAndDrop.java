package app.editor.imgui;

public enum DragAndDrop {
    ENTITY("entity");

    private final String type;

    DragAndDrop(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

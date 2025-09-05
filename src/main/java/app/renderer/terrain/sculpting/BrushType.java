package app.renderer.terrain.sculpting;

public enum BrushType {
    RAISE("Raise", "↑"),
    LOWER("Lower", "↓"),
    SMOOTH("Smooth", "~"),
    FLATTEN("Flatten", "═"),
    NOISE("Noise", "※");

    private final String displayName;
    private final String icon;

    BrushType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }

    public BrushType getInverse() {
        return switch (this) {
            case RAISE -> LOWER;
            case LOWER -> RAISE;
            default -> this;
        };
    }
}
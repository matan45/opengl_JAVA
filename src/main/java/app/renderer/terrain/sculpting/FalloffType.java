package app.renderer.terrain.sculpting;

public enum FalloffType {
    LINEAR("Linear"),
    SMOOTH("Smooth"),
    GAUSSIAN("Gaussian"),
    POLYNOMIAL("Polynomial");

    private final String displayName;

    FalloffType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
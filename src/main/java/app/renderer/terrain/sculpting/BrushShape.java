package app.renderer.terrain.sculpting;

public enum BrushShape {
    CIRCLE("Circle"),
    SQUARE("Square");

    private final String displayName;

    BrushShape(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public boolean isInside(float x, float y, float centerX, float centerY, float radius) {
        return switch (this) {
            case CIRCLE -> {
                float dx = x - centerX;
                float dy = y - centerY;
                yield (dx * dx + dy * dy) <= (radius * radius);
            }
            case SQUARE -> {
                float dx = Math.abs(x - centerX);
                float dy = Math.abs(y - centerY);
                yield dx <= radius && dy <= radius;
            }
        };
    }

    public float getDistance(float x, float y, float centerX, float centerY) {
        return switch (this) {
            case CIRCLE -> {
                float dx = x - centerX;
                float dy = y - centerY;
                yield (float) Math.sqrt(dx * dx + dy * dy);
            }
            case SQUARE -> Math.max(Math.abs(x - centerX), Math.abs(y - centerY));
        };
    }
}
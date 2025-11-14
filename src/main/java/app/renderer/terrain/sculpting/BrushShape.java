package app.renderer.terrain.sculpting;

public enum BrushShape {
    CIRCLE("Circle");

    BrushShape(String displayName) {
    }


    public boolean isInside(float x, float y, float centerX, float centerY, float radius) {
        return switch (this) {
            case CIRCLE -> {
                float dx = x - centerX;
                float dy = y - centerY;
                yield (dx * dx + dy * dy) <= (radius * radius);
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
        };
    }
}
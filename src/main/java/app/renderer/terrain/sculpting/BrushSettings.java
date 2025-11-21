package app.renderer.terrain.sculpting;

public class BrushSettings {
    private float size = 50.0f;
    private float strength = 0.5f;
    private float falloff = 0.8f;
    private float targetHeight = 0.0f;
    private BrushType brushType = BrushType.RAISE;
    private BrushShape shape = BrushShape.CIRCLE;
    private FalloffType falloffType = FalloffType.SMOOTH;
    private boolean showPreview = true;

    public BrushSettings() {}

    public BrushSettings(float size, float strength, float falloff) {
        this.size = Math.max(1.0f, size);
        this.strength = Math.max(0.01f, Math.min(2.0f, strength));
        this.falloff = Math.max(0.1f, Math.min(1.0f, falloff));
    }

    public float calculateFalloff(float distance) {
        if (distance > size) return 0.0f;
        
        float normalizedDistance = distance / size;
        return switch (falloffType) {
            case LINEAR -> 1.0f - normalizedDistance;
            case SMOOTH -> 1.0f - (3 * normalizedDistance * normalizedDistance - 2 * normalizedDistance * normalizedDistance * normalizedDistance);
            case GAUSSIAN -> (float) Math.exp(-(normalizedDistance * normalizedDistance) / (2 * 0.33f * 0.33f));
            case POLYNOMIAL -> (float) Math.pow(1 - normalizedDistance * normalizedDistance, 2);
        };
    }

    public float getSize() { return size; }
    public void setSize(float size) { this.size = Math.max(1.0f, size); }

    public float getStrength() { return strength; }
    public void setStrength(float strength) { this.strength = Math.max(0.01f, Math.min(2.0f, strength)); }

    public float getFalloff() { return falloff; }
    public void setFalloff(float falloff) { this.falloff = Math.max(0.1f, Math.min(1.0f, falloff)); }

    public float getTargetHeight() { return targetHeight; }
    public void setTargetHeight(float targetHeight) { this.targetHeight = targetHeight; }

    public BrushType getBrushType() { return brushType; }
    public void setBrushType(BrushType brushType) { this.brushType = brushType; }

    public BrushShape getShape() { return shape; }
    public void setShape(BrushShape shape) { this.shape = shape; }

    public FalloffType getFalloffType() { return falloffType; }
    public void setFalloffType(FalloffType falloffType) { this.falloffType = falloffType; }

    public boolean isShowPreview() { return showPreview; }
    public void setShowPreview(boolean showPreview) { this.showPreview = showPreview; }

    public BrushSettings copy() {
        BrushSettings copy = new BrushSettings(size, strength, falloff);
        copy.targetHeight = targetHeight;
        copy.brushType = brushType;
        copy.shape = shape;
        copy.falloffType = falloffType;
        copy.showPreview = showPreview;
        return copy;
    }
}
package app.renderer.terrain.sculpting;

public class TerrainEditAction {
    private final float worldX;
    private final float worldZ;
    private final BrushSettings brush;
    private final BrushType operation;
    private final long timestamp;

    public TerrainEditAction(float worldX, float worldZ, BrushSettings brush, BrushType operation) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.brush = brush.copy();
        this.operation = operation;
        this.timestamp = System.currentTimeMillis();
    }

    public float getWorldX() { return worldX; }
    public float getWorldZ() { return worldZ; }
    public BrushSettings getBrush() { return brush; }
    public BrushType getOperation() { return operation; }
    public long getTimestamp() { return timestamp; }

    public long getMemoryFootprint() {
        return 4 * 4 + brush.toString().length() * 2 + 8 + 8;
    }
}
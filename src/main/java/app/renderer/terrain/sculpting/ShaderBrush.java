package app.renderer.terrain.sculpting;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;

import java.nio.file.Path;

public class ShaderBrush extends ShaderProgram {
    private int brushPositionUniform;
    private int brushSizeUniform;
    private int brushColorUniform;
    private int brushAlphaUniform;
    private int brushFalloffUniform;
    private int brushShapeUniform;
    private int timeUniform;
    private int modelMatrixUniform;

    public ShaderBrush(Path shaderPath) {
        super(shaderPath);
    }

    @Override
    protected void getAllUniformLocations() {
        brushPositionUniform = getUniformLocation("brushPosition");
        brushSizeUniform = getUniformLocation("brushSize");
        brushColorUniform = getUniformLocation("brushColor");
        brushAlphaUniform = getUniformLocation("brushAlpha");
        brushFalloffUniform = getUniformLocation("brushFalloff");
        brushShapeUniform = getUniformLocation("brushShape");
        timeUniform = getUniformLocation("time");
        modelMatrixUniform = getUniformLocation("model");
    }

    public void loadBrushPosition(OLVector3f position) {
        load3DVector(brushPositionUniform, position);
    }

    public void loadBrushSize(float size) {
        loadFloat(brushSizeUniform, size);
    }

    public void loadBrushColor(OLVector3f color) {
        load3DVector(brushColorUniform, color);
    }

    public void loadBrushAlpha(float alpha) {
        loadFloat(brushAlphaUniform, alpha);
    }

    public void loadBrushFalloff(float falloff) {
        loadFloat(brushFalloffUniform, falloff);
    }

    public void loadBrushShape(int shape) {
        loadInt(brushShapeUniform, shape);
    }

    public void loadTime(float time) {
        loadFloat(timeUniform, time);
    }

    public void loadModelMatrix(OLMatrix4f matrix) {
        loadMatrix(modelMatrixUniform, matrix);
    }

    public void loadBrushData(BrushSettings brush, OLVector3f position, float time) {
        loadBrushPosition(position);
        loadBrushSize(brush.getSize());
        loadBrushFalloff(brush.getFalloff());
        loadBrushShape(brush.getShape().ordinal());
        loadTime(time);

        OLVector3f brushColor = getBrushColor(brush.getBrushType());
        loadBrushColor(brushColor);
        loadBrushAlpha(0.6f);
    }

    private OLVector3f getBrushColor(BrushType brushType) {
        return switch (brushType) {
            case RAISE -> new OLVector3f(0.2f, 1.0f, 0.2f);     // Green
            case LOWER -> new OLVector3f(1.0f, 0.2f, 0.2f);     // Red
            case SMOOTH -> new OLVector3f(0.2f, 0.6f, 1.0f);    // Blue
            case FLATTEN -> new OLVector3f(1.0f, 1.0f, 0.2f);   // Yellow
            case NOISE -> new OLVector3f(1.0f, 0.6f, 0.2f);     // Orange
        };
    }
}
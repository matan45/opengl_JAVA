package app.renderer.terrain;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderTerrainQuadtree extends ShaderProgram {

    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationModelMatrix;
    private int locationScaleNegx;
    private int locationScaleNegz;
    private int locationScalePosx;
    private int locationScalePosz;
    private int locationTileScale;
    private int locationTerrainLength;
    private int locationTerrainWidth;
    private int locationTexTerrainHeight;
    private int locationToggleWireframe;
    private int locationTerrainOrigin;
    private int locationTerrainHeightOffset;
    private int locationViewport;
    private int locationCameraPosition;
    private int locationSightRange;
    private int locationFogColor;
    private int locationIsFog;

    protected ShaderTerrainQuadtree(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());

        locationTileScale = super.getUniformLocation("tileScale");
        locationToggleWireframe = super.getUniformLocation("ToggleWireframe");
        locationViewport = super.getUniformLocation("Viewport");

        locationSightRange = super.getUniformLocation("sightRange");
        locationFogColor = super.getUniformLocation("fogColor");
        locationIsFog = super.getUniformLocation("isFog");

        locationScaleNegx = super.getUniformLocation("tscale_negx");
        locationScaleNegz = super.getUniformLocation("tscale_negz");
        locationScalePosx = super.getUniformLocation("tscale_posx");
        locationScalePosz = super.getUniformLocation("tscale_posz");

        locationTerrainLength = super.getUniformLocation("TerrainLength");
        locationTerrainWidth = super.getUniformLocation("TerrainWidth");
        locationTerrainOrigin = super.getUniformLocation("TerrainOrigin");

        locationTerrainHeightOffset = super.getUniformLocation("TerrainHeightOffset");
        locationTexTerrainHeight = super.getUniformLocation("TexTerrainHeight");

    }

    public void loadCameraPosition(OLVector3f camera) {
        super.load3DVector(locationCameraPosition, camera);
    }

    public void loadIsFog(boolean fog) {
        super.loadBoolean(locationIsFog, fog);
    }

    public void loadSightRange(float sightRange) {
        super.loadFloat(locationSightRange, sightRange);
    }

    public void loadFogColor(OLVector3f fogColor) {
        super.load3DVector(locationFogColor, fogColor);
    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadToggleWireframe(boolean wire) {
        super.loadBoolean(locationToggleWireframe, wire);
    }

    public void loadTexHighMap() {
        super.loadInt(locationTexTerrainHeight, 0);
    }

    public void loadViewPort(OLVector2f viewPort) {
        super.load2DVector(locationViewport, viewPort);
    }

    public void loadTileScale(float scale) {
        super.loadFloat(locationTileScale, 0.5f * scale);
    }

    public void loadScaleNegx(float scale) {
        super.loadFloat(locationScaleNegx, scale);
    }

    public void loadScaleNegz(float scale) {
        super.loadFloat(locationScaleNegz, scale);
    }

    public void loadScalePosx(float scale) {
        super.loadFloat(locationScalePosx, scale);
    }

    public void loadScalePosz(float scale) {
        super.loadFloat(locationScalePosz, scale);
    }

    public void loadTerrainLength(float terrainLength) {
        super.loadFloat(locationTerrainLength, terrainLength);
    }

    public void loadTerrainWidth(float terrainWidth) {
        super.loadFloat(locationTerrainWidth, terrainWidth);
    }

    public void loadTerrainHeightOffset(float terrainHeightOffset) {
        super.loadFloat(locationTerrainHeightOffset, terrainHeightOffset);
    }

    public void loadTerrainOrigin(OLVector3f origin) {
        super.load3DVector(locationTerrainOrigin, origin);
    }


    public void loadProjectionMatrix(OLMatrix4f projection) {
        super.loadMatrix(locationProjectionMatrix, projection);
    }

    public void loadModelMatrix(OLMatrix4f model) {
        super.loadMatrix(locationModelMatrix, model);
    }
}

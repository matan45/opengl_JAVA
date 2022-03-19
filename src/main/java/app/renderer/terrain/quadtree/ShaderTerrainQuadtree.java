package app.renderer.terrain.quadtree;

import app.math.OLMatrix4f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderTerrainQuadtree extends ShaderProgram {

    private int locationProjectionMatrix;
    private int locationViewMatrix;
    private int locationModelMatrix;
    private int locationtscale_negx;
    private int locationtscale_negz;
    private int locationtscale_posx;
    private int locationtscale_posz;
    private int locationtileScale;
    private int locationTerrainLength;
    private int locationTerrainHeight;
    private int locationTerrainWidth;
    private int locationTexTerrainHeight;
    private int locationTerrainOrigin;
    private int locationTerrainHeightOffset;

    protected ShaderTerrainQuadtree(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation(UniformsNames.PROJECTION.getUniformsName());
        locationViewMatrix = super.getUniformLocation(UniformsNames.VIEW.getUniformsName());
        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());

        locationtileScale = super.getUniformLocation("tileScale");

        locationtscale_negx = super.getUniformLocation("tscale_negx");
        locationtscale_negz = super.getUniformLocation("tscale_negz");
        locationtscale_posx = super.getUniformLocation("tscale_posx");
        locationtscale_posz = super.getUniformLocation("tscale_posz");

        locationTerrainLength = super.getUniformLocation("TerrainLength");
        locationTerrainWidth = super.getUniformLocation("TerrainWidth");
        locationTerrainOrigin = super.getUniformLocation("TerrainOrigin");

        locationTerrainHeightOffset = super.getUniformLocation("TerrainHeightOffset");
        locationTexTerrainHeight = super.getUniformLocation("TexTerrainHeight");

    }

    public void loadViewMatrix(OLMatrix4f view) {
        super.loadMatrix(locationViewMatrix, view);
    }

    public void loadTexHighMap(int location) {
        super.loadInt(locationTexTerrainHeight, location);
    }

    public void loadtileScale(float scale) {
        super.loadFloat(locationtileScale, 0.5f * scale);
    }

    public void loadtscale_negx(float scale) {
        super.loadFloat(locationtscale_negx, scale);
    }

    public void loadtscale_negz(float scale) {
        super.loadFloat(locationtscale_negz, scale);
    }

    public void loadtscale_posx(float scale) {
        super.loadFloat(locationtscale_posx, scale);
    }

    public void loadtscale_posz(float scale) {
        super.loadFloat(locationtscale_posz, scale);
    }

    public void loadTerrainLength(float TerrainLength) {
        super.loadFloat(locationTerrainLength, TerrainLength);
    }

    public void loadTerrainWidth(float TerrainWidth) {
        super.loadFloat(locationTerrainWidth, TerrainWidth);
    }

    public void loadTerrainHeightOffset(float TerrainHeightOffset) {
        super.loadFloat(locationTerrainHeightOffset, TerrainHeightOffset);
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

package app.renderer.terrain;

import app.math.OLMatrix4f;
import app.math.OLVector2f;
import app.math.OLVector3f;
import app.renderer.shaders.ShaderProgram;
import app.renderer.shaders.UniformsNames;

import java.nio.file.Path;

public class ShaderTerrainQuadtree extends ShaderProgram {
    private int locationModelMatrix;
    private int locationScaleNegx;
    private int locationScaleNegz;
    private int locationScalePosx;
    private int locationScalePosz;
    private int locationTileScale;
    private int locationTerrainLength;
    private int locationTerrainWidth;
    private int locationTexTerrainHeight;
    private int locationTexTerrainModification;
    private int locationTexSplatMap;
    private int locationToggleWireframe;
    private int locationTerrainOrigin;
    private int locationTerrainHeightOffset;
    private int locationViewport;
    private int locationCameraPosition;
    private int locationSightRange;
    private int locationFogColor;
    private int locationIsFog;
    private int locationIrradianceMap;
    
    private int[] locationAlbedoMaps;
    private int[] locationNormalMaps;
    private int locationNodePosition;

    protected ShaderTerrainQuadtree(Path path) {
        super(path);
    }

    @Override
    protected void getAllUniformLocations() {
        locationAlbedoMaps = new int[4];
        locationNormalMaps = new int[4];

        locationModelMatrix = super.getUniformLocation(UniformsNames.MODEL.getUniformsName());
        locationCameraPosition = super.getUniformLocation(UniformsNames.CAMERA_POSITION.getUniformsName());

        locationTileScale = super.getUniformLocation("tileScale");
        locationToggleWireframe = super.getUniformLocation("ToggleWireframe");
        locationViewport = super.getUniformLocation("Viewport");

        locationSightRange = super.getUniformLocation("sightRange");
        locationFogColor = super.getUniformLocation("fogColor");
        locationIsFog = super.getUniformLocation("isFog");

        locationScaleNegx = super.getUniformLocation("scaleNegx");
        locationScaleNegz = super.getUniformLocation("scaleNegz");
        locationScalePosx = super.getUniformLocation("scalePosx");
        locationScalePosz = super.getUniformLocation("scalePosz");

        locationNodePosition = super.getUniformLocation("nodePosition");

        locationTerrainLength = super.getUniformLocation("TerrainLength");
        locationTerrainWidth = super.getUniformLocation("TerrainWidth");
        locationTerrainOrigin = super.getUniformLocation("TerrainOrigin");

        locationTerrainHeightOffset = super.getUniformLocation("TerrainHeightOffset");
        locationTexTerrainHeight = super.getUniformLocation("TexTerrainHeight");
        locationTexTerrainModification = super.getUniformLocation("TexTerrainModification");
        locationTexSplatMap = super.getUniformLocation("TexSplatMap");
        
        locationIrradianceMap = super.getUniformLocation("irradianceMap");

        for (int i = 0; i < 4; i++) {
            locationAlbedoMaps[i] = super.getUniformLocation("albedoMaps[" + i + "]");
            locationNormalMaps[i] = super.getUniformLocation("normalMaps[" + i + "]");
        }

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
    public void loadNodePosition(OLVector3f nodePosition) {
        super.load3DVector(locationNodePosition, nodePosition);
    }

    public void loadToggleWireframe(boolean wire) {
        super.loadBoolean(locationToggleWireframe, wire);
    }

    public void loadTexHighMap() {

        super.loadInt(locationTexTerrainHeight, 0);
        super.loadInt(locationIrradianceMap, 2);
        
        // Splat map at 5
        super.loadInt(locationTexSplatMap, 5);

        // Albedos at 6, 7, 8, 9
        for (int i = 0; i < 4; i++) {
            super.loadInt(locationAlbedoMaps[i], 6 + i);
        }
        
        // Normals at 10, 11, 12, 13
        for (int i = 0; i < 4; i++) {
            super.loadInt(locationNormalMaps[i], 10 + i);
        }
    }
    
    public void loadTexModificationMap() {
        super.loadInt(locationTexTerrainModification, 1);
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

    public void loadModelMatrix(OLMatrix4f model) {
        super.loadMatrix(locationModelMatrix, model);
    }
}

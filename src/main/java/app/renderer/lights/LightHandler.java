package app.renderer.lights;

import app.math.components.Camera;

import java.util.ArrayList;
import java.util.List;

public class LightHandler {
    private final List<SpotLight> spotLights;
    private final List<PointLight> pointLights;
    private DirectionalLight directionalLight;

    public LightHandler() {
        spotLights = new ArrayList<>();
        pointLights = new ArrayList<>();
    }

    public void addSpotLight(SpotLight spotLight) {
        spotLights.add(spotLight);
    }

    public void removeSpotLight(SpotLight spotLight) {
        spotLights.remove(spotLight);
    }

    public void addPointLight(PointLight pointLight) {
        pointLights.add(pointLight);
    }

    public void removePointLight(PointLight pointLight) {
        pointLights.remove(pointLight);
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public void drawBillboards(Camera camera) {
        for (SpotLight spotLight : spotLights)
            spotLight.drawBillboards(camera);

        for (PointLight pointLight : pointLights)
            pointLight.drawBillboards(camera);
    }
}

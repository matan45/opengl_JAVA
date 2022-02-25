package app.renderer.lights;

import java.util.ArrayList;
import java.util.List;

public class LightHandler {
    private final List<SpotLight> spotLights;
    private final List<PointLight> pointLights;

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
}

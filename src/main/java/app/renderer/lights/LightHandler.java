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

    void addSpotLight(SpotLight spotLight) {
        spotLights.add(spotLight);
    }

    void removeSpotLight(SpotLight spotLight) {
        spotLights.remove(spotLight);
    }

    void addPointLight(PointLight pointLight) {
        pointLights.add(pointLight);
    }

    void removePointLight(PointLight pointLight) {
        pointLights.remove(pointLight);
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }
}

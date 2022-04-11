package app.utilities.serialize;

import app.ecs.components.*;
import app.math.OLVector3f;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Set;

class SerializableComponent {

    protected SerializableComponent() {
    }

    protected JsonArray serializableComponent(final Set<Component> components) {
        JsonArray componentsArray = new JsonArray();
        JsonObject totalComponent = new JsonObject();
        totalComponent.addProperty("TotalComponentCount", components.size());
        componentsArray.add(totalComponent);
        for (Component component : components) {
            componentsArray.add(component.getClass().getSimpleName());
            componentsArray.add(saveComponentFactory(component));
        }
        return componentsArray;
    }

    private JsonObject saveComponentFactory(Component component) {
        JsonObject serializable = new JsonObject();
        switch (component) {
            case TransformComponent transform -> {
                serializable.add("Position", olVector3f(transform.getOlTransform().getPosition()));
                serializable.add("Rotation", olVector3f(transform.getOlTransform().getRotation()));
                serializable.add("Scale", olVector3f(transform.getOlTransform().getScale()));
            }
            case DirectionalLightComponent directionalLight -> {
                serializable.add("Direction", olVector3f(directionalLight.getDirectionalLight().getDirection()));
                serializable.add("Color", olVector3f(directionalLight.getDirectionalLight().getColor()));
                serializable.addProperty("LightIntensity", directionalLight.getDirectionalLight().getDirLightIntensity());
            }
            case PointLightComponent pointLight -> {
                serializable.add("Position", olVector3f(pointLight.getPointLight().getPosition()));
                serializable.add("Color", olVector3f(pointLight.getPointLight().getColor()));
                serializable.addProperty("Quadratic", pointLight.getPointLight().getQuadratic());
                serializable.addProperty("Linear", pointLight.getPointLight().getLinear());
                serializable.addProperty("Constant", pointLight.getPointLight().getConstant());
            }
            case SpotLightComponent SpotLight -> {
                serializable.add("Position", olVector3f(SpotLight.getSpotLight().getPosition()));
                serializable.add("Direction", olVector3f(SpotLight.getSpotLight().getDirection()));
                serializable.add("Color", olVector3f(SpotLight.getSpotLight().getColor()));
                serializable.addProperty("Quadratic", SpotLight.getSpotLight().getQuadratic());
                serializable.addProperty("Linear", SpotLight.getSpotLight().getLinear());
                serializable.addProperty("Constant", SpotLight.getSpotLight().getConstant());
                serializable.addProperty("CutOff", SpotLight.getSpotLight().getCutOff());
                serializable.addProperty("OuterCutOff", SpotLight.getSpotLight().getOuterCutOff());
            }
            case FogComponent fog -> {
                serializable.add("Color", olVector3f(fog.getFog().getFogColor()));
                serializable.addProperty("SightRange", fog.getFog().getSightRange());
            }
            default -> throw new IllegalStateException("Unexpected value: " + component);
        }
        return serializable;
    }

    private JsonArray olVector3f(OLVector3f olVector3f) {
        JsonArray vector3f = new JsonArray();
        vector3f.add(olVector3f.x);
        vector3f.add(olVector3f.y);
        vector3f.add(olVector3f.z);
        return vector3f;
    }
}

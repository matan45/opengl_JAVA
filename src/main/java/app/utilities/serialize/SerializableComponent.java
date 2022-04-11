package app.utilities.serialize;

import app.ecs.components.Component;
import app.ecs.components.DirectionalLightComponent;
import app.ecs.components.TransformComponent;
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

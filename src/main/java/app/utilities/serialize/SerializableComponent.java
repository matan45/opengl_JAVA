package app.utilities.serialize;

import app.ecs.components.Component;
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
        return componentsArray;
    }
}

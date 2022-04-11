package app.utilities.serialize;

import app.ecs.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class SerializableEntity {
    private final SerializableComponent serializableComponent;

    protected SerializableEntity() {
        serializableComponent = new SerializableComponent();
    }

    protected JsonObject serializableEntity(Entity entity) {
        JsonObject result = new JsonObject();
        //case the entity is single
        result.addProperty("EntityName", entity.getName());
        result.add("Components", serializableComponent.serializableComponent(entity.getComponents()));

        //case the entity is a son
        if (entity.getFather() != null) {
            result.addProperty("FatherEntityName", entity.getFather().getName());
            result.add("FatherEntityComponents", serializableComponent.serializableComponent(entity.getFather().getComponents()));
        }
        //case the entity is father
        else if (entity.hasChildren()) {
            result.addProperty("TotalEntitiesChildrenCount", entity.getChildren().size());
            JsonArray jsonArray = new JsonArray();
            for (Entity children : entity.getChildren()) {
                JsonObject childrenJson = new JsonObject();
                childrenJson.addProperty("ChildrenEntityName", children.getName());
                childrenJson.add("ChildrenEntityComponents", serializableComponent.serializableComponent(children.getComponents()));
                jsonArray.add(childrenJson);
            }
            result.add("ChildrenEntities", jsonArray);
        }


        return result;
    }
}

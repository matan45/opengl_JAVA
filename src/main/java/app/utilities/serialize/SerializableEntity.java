package app.utilities.serialize;

import app.ecs.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class SerializableEntity {
    private final SerializableComponent serializableComponent;
    private static final String FATHER_ENTITY = "FatherEntity";
    private static final String CHILDREN_ENTITY = "ChildrenEntities";

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
            JsonObject fatherJson = new JsonObject();
            fatherJson.addProperty("FatherEntityName", entity.getFather().getName());
            fatherJson.add("FatherEntityComponents", serializableComponent.serializableComponent(entity.getFather().getComponents()));
            result.add(FATHER_ENTITY, fatherJson);
        }
        //case the entity is father
        else if (entity.hasChildren()) {
            JsonArray jsonArray = new JsonArray();
            for (Entity children : entity.getChildren()) {
                JsonObject childrenJson = new JsonObject();
                childrenJson.addProperty("ChildrenEntityName", children.getName());
                childrenJson.add("ChildrenEntityComponents", serializableComponent.serializableComponent(children.getComponents()));
                jsonArray.add(childrenJson);
            }
            result.add(CHILDREN_ENTITY, jsonArray);
        }


        return result;
    }

    protected Entity deserializeEntity(JsonObject entityJson) {
        Entity entity = new Entity();
        String entityName = entityJson.get("EntityName").getAsString();
        entity.setName(entityName);
        serializableComponent.deserializeComponent(entityJson.getAsJsonArray("Components"), entity);
        if (entityJson.getAsJsonObject(FATHER_ENTITY) != null) {
            JsonObject jsonFather = entityJson.getAsJsonObject(FATHER_ENTITY);
            Entity entityFather = new Entity();
            String fatherName = jsonFather.get("FatherEntityName").getAsString();
            entityFather.setName(fatherName);
            serializableComponent.deserializeComponent(jsonFather.getAsJsonArray("FatherEntityComponents"), entityFather);
            entity.setFather(entityFather);
        } else if (entityJson.getAsJsonArray(CHILDREN_ENTITY) != null) {
            JsonArray childrenEntities = entityJson.getAsJsonArray(CHILDREN_ENTITY);
            for (int i = 0; i < childrenEntities.size(); i++) {
                Entity childrenEntity = new Entity();
                String childrenName = childrenEntities.get(i).getAsJsonObject().get("ChildrenEntityName").getAsString();
                childrenEntity.setName(childrenName);
                serializableComponent.deserializeComponent(childrenEntities.get(i).getAsJsonObject().getAsJsonArray("ChildrenEntityComponents"), childrenEntity);
                entity.addChildren(childrenEntity);
            }
        }
        return entity;
    }
}

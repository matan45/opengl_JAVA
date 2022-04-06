package app.ecs;

import app.ecs.components.Component;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    private static final List<Entity> entitiesArray = new ArrayList<>();

    private EntitySystem() {
    }

    public static List<Entity> getEntitiesArray() {
        return entitiesArray;
    }

    public static void addEntity(Entity entity) {
        entitiesArray.add(entity);
    }

    public static void addEntityChildren(int father, Entity son) {
        entitiesArray.get(father).addChildren(son);
    }

    public static void addEntityChildren(Entity father, Entity son) {
        father.addChildren(son);
    }

    public static Entity getEntityByIndex(int index) {
        return entitiesArray.get(index);
    }

    public static void removeEntity(int index) {
        entitiesArray.get(index).cleanUp();
        entitiesArray.remove(index);
    }

    public static List<Entity> getEntitiesByName(String name) {
        return entitiesArray.stream().filter(e -> e.getName().equals(name)).toList();
    }

    public static void updateEntities(float dt) {
        entitiesArray.forEach(e -> e.updateComponent(dt));
    }

    public static void closeEntities() {
        entitiesArray.forEach(e -> e.getComponents().forEach(Component::cleanUp));
    }
}

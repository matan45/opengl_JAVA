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

    public static void removeEntity(int index) {
        entitiesArray.get(index).cleanUp();
        entitiesArray.remove(index);
    }

    public static List<Entity> getEntitiesByName(String name) {
        return entitiesArray.stream().filter(e -> e.getName().equals(name)).toList();
    }

    public static void updateEntities(float dt) {
        entitiesArray.forEach(e -> e.getComponents().forEach(c -> c.update(dt)));
    }

    public static void closeEntities() {
        entitiesArray.forEach(e -> e.getComponents().forEach(Component::cleanUp));
    }
}

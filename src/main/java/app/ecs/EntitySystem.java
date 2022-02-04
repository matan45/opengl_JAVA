package app.ecs;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    static List<Entity> entitiesArray = new ArrayList<>();

    private EntitySystem() {
    }

    public static List<Entity> getEntitiesArray() {
        return entitiesArray;
    }

    public static void addEntity(Entity entity) {
        entitiesArray.add(entity);
    }

    public static void removeEntity(int index) {
        entitiesArray.remove(index);
    }

    public static void removeEntity(Entity entity) {
        entitiesArray.remove(entity);
    }

    public static List<Entity> getEntitiesByName(String name) {
        return entitiesArray.stream().filter(e -> e.getName().equals(name)).toList();
    }

    public static void updateEntities(float dt) {
        entitiesArray.forEach(e -> e.getComponents().forEach(c -> c.update(dt)));
    }
}

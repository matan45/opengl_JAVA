package app.ecs;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    static List<Entity> entityMap = new ArrayList<>();

    private EntitySystem() {
    }

    public static List<Entity> getEntities() {
        return entityMap;
    }

    public static void addEntity(Entity entity) {
        entityMap.add(entity);
    }
    public static void removeEntity(int index) {
        entityMap.remove(index);
    }
}

package app.ecs;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    static List<Entity> entityArray = new ArrayList<>();

    private EntitySystem() {
    }

    public static List<Entity> getEntities() {
        return entityArray;
    }

    public static void addEntity(Entity entity) {
        entityArray.add(entity);
    }

    public static void removeEntity(int index) {
        entityArray.remove(index);
    }

    public static void updateEntity(float dt) {
        entityArray.forEach(e -> e.getComponents().forEach(c -> c.update(dt)));
    }
}

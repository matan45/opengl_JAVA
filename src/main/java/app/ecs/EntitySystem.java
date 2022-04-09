package app.ecs;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    private static final List<Entity> entitiesFather = new ArrayList<>();

    private EntitySystem() {
    }

    public static List<Entity> getEntitiesFather() {
        return entitiesFather;
    }

    public static void addEntity(Entity entity) {
        entitiesFather.add(entity);
    }

    public static void addEntityChildren(Entity father, Entity son) {
        son.setFather(father);
        father.addChildren(son);
    }

    public static void removeEntity(Entity entity) {
        if (entity.getFather() == null) {
            entity.cleanUp();
            entitiesFather.remove(entity);
        } else {
            entity.getFather().removeChildren(entity);
        }
    }

    public static List<Entity> getEntitiesByName(String name) {
        //TODO check Children entites
        return entitiesFather.stream().filter(e -> e.getName().equals(name)).toList();
    }

    public static void updateEntities(float dt) {
        entitiesFather.forEach(e -> e.updateComponent(dt));
    }

    public static void closeEntities() {
        entitiesFather.forEach(Entity::cleanUp);
    }
}

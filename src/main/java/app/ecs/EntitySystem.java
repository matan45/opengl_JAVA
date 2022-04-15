package app.ecs;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem {
    private static final List<Entity> entitiesFather = new ArrayList<>();
    private static final List<Entity> entitiesByName = new ArrayList<>();

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
        entitiesByName.clear();
        for (Entity entity : entitiesFather) {
            if (entity.hasChildren())
                entity.getChildren().stream().filter(e -> e.getName().equals(name)).forEach(entitiesByName::add);
            if (entity.getName().equals(name))
                entitiesByName.add(entity);
        }

        return entitiesByName;
    }

    public static void updateEntities(float dt) {
        entitiesFather.forEach(e -> e.updateComponent(dt));
    }

    public static void closeEntities() {
        entitiesFather.forEach(Entity::cleanUp);
        entitiesFather.clear();
    }
}

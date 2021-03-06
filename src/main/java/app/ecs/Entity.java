package app.ecs;

import app.ecs.components.Component;
import app.ecs.components.TransformComponent;
import app.math.components.OLTransform;

import java.util.*;

public class Entity {
    private final int uuid;
    private String name;
    private final Set<Component> components;
    private final Map<Integer, Entity> children;
    private Entity father;
    private String path;

    public Entity(String name, OLTransform olTransform) {
        this.name = name;
        components = new HashSet<>();
        components.add(new TransformComponent(this, olTransform));
        children = new HashMap<>();
        //TODO UUID
        uuid = System.identityHashCode(this);
        path = "";
    }

    public Entity() {
        children = new HashMap<>();
        components = new HashSet<>();
        uuid = System.identityHashCode(this);
        path = "";
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(c);
                return;
            }
        }
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.stream().anyMatch(c -> componentClass.isAssignableFrom(c.getClass()));
    }

    public void updateComponent(float dt) {
        if (hasChildren())
            children.values().forEach(e -> e.updateComponent(dt));
        components.forEach(c -> c.update(dt));
    }

    public void addComponent(Component c) {
        components.add(c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Component> getComponents() {
        return components;
    }

    public int getUuid() {
        return uuid;
    }

    public Entity getFather() {
        return father;
    }

    public void setFather(Entity father) {
        this.father = father;
    }

    public void cleanUp() {
        for (Component c : components)
            c.cleanUp();
        components.clear();

        if (hasChildren())
            children.values().forEach(Entity::cleanUp);
        children.clear();
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void addChildren(Entity entity) {
        children.put(entity.getUuid(), entity);
    }

    public void removeChildren(Entity entity) {
        entity.cleanUp();
        children.remove(entity.getUuid());
    }

    public List<Entity> getChildren() {
        return children.values().stream().toList();
    }

    //case is a prefab
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

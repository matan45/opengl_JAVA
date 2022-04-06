package app.ecs;

import app.ecs.components.Component;
import app.ecs.components.TransformComponent;
import app.math.components.OLTransform;

import java.util.*;

public class Entity {
    //TODO need uuid for ech entity
    private final UUID uuid;
    private String name;
    private final Set<Component> components;
    private boolean isActive;
    private final List<Entity> children;
    private Entity father;
    //case remove children

    public Entity(String name, OLTransform olTransform) {
        this.name = name;
        components = new HashSet<>();
        components.add(new TransformComponent(this, olTransform));
        children = new ArrayList<>();
        uuid = UUID.randomUUID();
    }

    public Entity() {
        children = new ArrayList<>();
        components = new HashSet<>();
        uuid = UUID.randomUUID();
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
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                return true;
            }
        }
        return false;
    }

    public void updateComponent(float dt) {
        if (hasChildren())
            children.forEach(e -> e.getComponents().forEach(c -> c.update(dt)));
        components.forEach(c -> c.update(dt));
    }

    public void addComponent(Component c) {
        this.components.add(c);
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

    public UUID getUuid() {
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
            children.forEach(Entity::cleanUp);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void addChildren(Entity entity) {
        children.add(entity);
    }

    public void removeChildren(Entity entity) {
        children.remove(entity);
    }

    public List<Entity> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity that = (Entity) o;
        return Objects.equals(this.uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}

package app.ecs;

import app.ecs.components.Component;
import app.ecs.components.TransformComponent;
import app.math.components.OLTransform;

import java.util.HashSet;
import java.util.Set;

public class Entity {
    String name;
    Set<Component> components;
    boolean isActive;

    public Entity(String name, OLTransform olTransform) {
        this.name = name;
        components = new HashSet<>();
        components.add(new TransformComponent(this, olTransform));
    }

    public Entity() {
        components = new HashSet<>();
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

    public void cleanUp() {
        for (Component c : components)
            c.cleanUp();
        components.clear();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}

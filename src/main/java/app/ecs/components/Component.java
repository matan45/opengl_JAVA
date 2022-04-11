package app.ecs.components;

import app.ecs.Entity;

import java.util.Objects;

public abstract class Component {
    protected Entity ownerEntity;

    protected Component(Entity ownerEntity) {
        this.ownerEntity = ownerEntity;
    }


    public void update(float dt) {
    }


    public abstract void imguiDraw();


    public abstract void cleanUp();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component that = (Component) o;
        return Objects.equals(ownerEntity.getUuid(), that.ownerEntity.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerEntity);
    }
}

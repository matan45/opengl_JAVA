package app.ecs.components;

import app.ecs.Entity;

import java.util.Objects;

public class CommonComponent implements Component {
    protected Entity ownerEntity;

    public CommonComponent(Entity ownerEntity) {
        this.ownerEntity = ownerEntity;
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void imguiDraw() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonComponent that = (CommonComponent) o;
        return Objects.equals(ownerEntity, that.ownerEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerEntity);
    }
}

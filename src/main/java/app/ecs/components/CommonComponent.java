package app.ecs.components;

import app.ecs.Entity;

public sealed class CommonComponent implements Component permits TransformComponent {
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
}

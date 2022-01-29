package app.ecs.components;

import app.ecs.Entity;

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
}

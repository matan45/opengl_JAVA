package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;

public class ParticleComponent extends Component{
    private final OLTransform olTransform;
    protected ParticleComponent(Entity ownerEntity) {
        super(ownerEntity);
        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
    }

    @Override
    public void imguiDraw() {

    }

    @Override
    public void cleanUp() {

    }
}

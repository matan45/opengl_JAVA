package app.ecs.components;

import app.ecs.Entity;
import app.renderer.draw.EditorRenderer;
import app.renderer.ibl.SkyBox;

public class SkyBoxComponent extends CommonComponent {
    SkyBox skyBox;

    public SkyBoxComponent(Entity ownerEntity) {
        super(ownerEntity);
        skyBox = EditorRenderer.getSkyBox();
    }

    @Override
    public void imguiDraw() {

    }

    @Override
    public void cleanUp() {
        skyBox.setActive(false);
    }
}

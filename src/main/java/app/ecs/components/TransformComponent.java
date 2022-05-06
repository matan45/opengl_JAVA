package app.ecs.components;

import app.ecs.Entity;
import app.math.components.OLTransform;
import imgui.ImGui;
import imgui.type.ImString;

import static app.utilities.ImguiUtil.drawVector3;

public final class TransformComponent extends Component {
    private final OLTransform olTransform;
    private final ImString entityName;


    public TransformComponent(Entity ownerEntity, OLTransform olTransform) {
        super(ownerEntity);
        this.olTransform = olTransform;
        entityName = new ImString(ownerEntity.getName(), 256);
    }

    @Override
    public void imguiDraw() {
        ImGui.text("Entity Name");
        if (ImGui.inputText("##", entityName))
            ownerEntity.setName(entityName.get());

        drawVector3("Position", olTransform.getPosition(), 0.0f, 0.0f, 0.0f);
        drawVector3("Rotation", olTransform.getRotation(), 0.0f, 0.0f, 0.0f);
        drawVector3("Scale", olTransform.getScale(), 1.0f, 1.0f, 1.0f);

    }

    @Override
    public void cleanUp() {

    }

    public OLTransform getOlTransform() {
        return olTransform;
    }
}

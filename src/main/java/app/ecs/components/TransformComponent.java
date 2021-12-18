package app.ecs.components;

import app.ecs.Component;
import imgui.ImGui;

public class TransformComponent implements Component {
    @Override
    public void update(float dt) {

    }

    @Override
    public void imguiDraw() {
        ImGui.text("hi TransformComponent" );
    }

    @Override
    public String getName() {
        return "Transform Component";
    }
}

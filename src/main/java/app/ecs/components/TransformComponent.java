package app.ecs.components;

import app.ecs.Component;
import imgui.ImGui;

public class TransformComponent implements Component {

    float[] val = new float[1];
    float[] val2 = new float[1];

    @Override
    public void update(float dt) {

    }

    @Override
    public void imguiDraw() {
        ImGui.sliderFloat("x", val, 0.0f, 1.0f);
        ImGui.sliderFloat("y", val2, 0.0f, 1.0f);
        ImGui.labelText("", val[0] + "");
        ImGui.labelText("", val2[0] + "");
    }

    @Override
    public String getName() {
        return "Transform Component";
    }
}

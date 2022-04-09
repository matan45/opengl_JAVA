package app.ecs.components;

import java.io.Serializable;

public interface Component extends Serializable {
    void init();

    void update(float dt);

    void imguiDraw();

    void cleanUp();
}

package app.ecs.components;

public interface Component {
    void init();
    void update(float dt);
    void imguiDraw();
    void cleanUp();
}

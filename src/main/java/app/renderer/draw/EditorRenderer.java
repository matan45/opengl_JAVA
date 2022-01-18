package app.renderer.draw;

import app.renderer.framebuffer.Framebuffer;

import static org.lwjgl.opengl.GL11.*;

public class EditorRenderer {
    static Framebuffer framebuffer = new Framebuffer(1920, 1080);

    private EditorRenderer() {
    }

    public static void draw() {
        framebuffer.bind();
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        framebuffer.unbind();
    }

    public static Framebuffer getFramebuffer() {
        return framebuffer;
    }
}

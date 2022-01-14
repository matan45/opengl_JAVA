package app.renderer.framebuffer;

import static org.lwjgl.opengl.GL11.*;

public class FrameBufferTexture {
    int width;
    int height;
    int textureId;

    public FrameBufferTexture(int width, int height) {
        this.width = width;
        this.height = height;
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureId() {
        return textureId;
    }
}

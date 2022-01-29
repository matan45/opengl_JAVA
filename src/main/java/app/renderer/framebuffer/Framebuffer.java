package app.renderer.framebuffer;

import app.renderer.Textures;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer {
    int width;
    int height;
    Textures textures;

    public Framebuffer(int width, int height, Textures textures) {
        this.width = width;
        this.height = height;
        this.textures = textures;
    }

    public int[] createFrameRenderBuffer() {
        // Generate framebuffer
        int fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // Create the texture to render the data to, and attach it to our framebuffer
        int texture = textures.frameBufferTexture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                texture, 0);

        // Create renderers store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        assert glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE : "Error: Framebuffer is not complete";
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return new int[]{fboID, texture};
    }

    public void bind(int id) {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}

package app.renderer.ibl;

import static org.lwjgl.opengl.GL11.*;

public class SkyBox {

    public void init(){
        // configure global opengl state
        // -----------------------------
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL); // set depth function to less than AND equal for skybox depth trick.

    }
}

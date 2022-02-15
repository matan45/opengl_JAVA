package app.editor;

import app.ecs.EntitySystem;
import app.editor.imgui.*;
import app.renderer.draw.EditorRenderer;
import app.utilities.logger.Logger;
import app.utilities.resource.ResourceManager;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class GlfwWindow {
    private long window;
    private ImguiHandler imgui;
    private final String title;
    private int width = 800;
    private int height = 600;
    private MainImgui mainImgui;

    public GlfwWindow(String title) {
        this.title = title;
        init();
    }

    private void init() {
        GLFWErrorCallback glfwErrorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwErrorCallback.set();

        if (!glfwInit())
            throw new RuntimeException("Unable to initialize GLFW");
        //opengl settings
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        //for debug mode
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        //window options
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        //window icon
        ResourceManager.setWindowGLFWIcon(Paths.get("src\\main\\resources\\editor\\icons\\icon-window.png"), window);

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            width = pWidth.get();
            height = pHeight.get();

            // Get the resolution of the primary monitor
            GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert videoMode != null;
            glfwSetWindowPos(
                    window,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically
        glfwSetWindowSizeCallback(window, this::windowSizeChanged);

        //make opengl context
        glfwMakeContextCurrent(window);

        //Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(window);


    }

    private void windowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;
        mainImgui.setHeight(height);
        mainImgui.setWidth(width);
    }

    private void close() {
        EditorRenderer.cleanUp();
        imgui.disposeImGui();

        setCapabilities(null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null);
    }

    public void run() {
        //create opengl context
        createCapabilities();
        EditorRenderer.init();

        imgui = new ImguiHandler("#version 460", window);
        //TODO: more generic to add imgui window
        mainImgui = new MainImgui(title, width, height);
        ImguiLayerHandler.addLayer(mainImgui);
        ImguiLayerHandler.addLayer(new Inspector());
        ImguiLayerHandler.addLayer(new SceneGraph());
        ImguiLayerHandler.addLayer(new LogWindow());
        ImguiLayerHandler.addLayer(new ContentBrowser());
        ImguiLayerHandler.addLayer(new ViewPort());

        Logger.init();
        float deltaTime;
        float dt = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);

            //calculate delta time
            float frame = System.nanoTime();
            deltaTime = ((frame - dt) / 1000000000.0f);
            EntitySystem.updateEntities(deltaTime);
            dt = frame;

            EditorRenderer.draw();

            imgui.startFrame();
            ImguiLayerHandler.renderImGui(deltaTime);
            imgui.endFrame();

            glfwSwapBuffers(window);
        }
        close();
    }

    //TODO manager window
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}

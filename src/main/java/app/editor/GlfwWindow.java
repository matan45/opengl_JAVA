package app.editor;

import app.ecs.EntitySystem;
import app.editor.imgui.*;
import app.renderer.draw.Renderer;
import app.utilities.logger.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;


public class GlfwWindow {
    long window;
    ImguiHandler imgui;
    final String title;
    int width = 800;
    int height = 600;
    MainImgui mainImgui;

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
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //window options
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        //window icon
        try {
            setIcon("C:\\matan\\java\\src\\main\\resources\\editor\\icons\\icon-window.png", window);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        ImguiLayerHandler.cleanLayer();
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

        imgui = new ImguiHandler("#version 460", window);
        //TODO: more generic to add imgui window
        mainImgui = new MainImgui(title, width, height);
        Inspector inspector = new Inspector();
        ImguiLayerHandler.addLayer(mainImgui);
        ImguiLayerHandler.addLayer(inspector);
        ImguiLayerHandler.addLayer(new SceneGraph(inspector));
        ImguiLayerHandler.addLayer(new LogWindow());
        ImguiLayerHandler.addLayer(new ContentBrowser());
        ImguiLayerHandler.addLayer(new ViewPort());

        Logger.init();
        float deltaTime = 0;
        float dt = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);

            //calculate delta time
            float frame = System.nanoTime();
            deltaTime = ((frame - dt) / 1000000000.0f);
            EntitySystem.updateEntity(deltaTime);
            dt = frame;

            Renderer.draw();

            imgui.startFrame();
            ImguiLayerHandler.renderImGui();
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


    //TODO move to resource manger
    public static void setIcon(String path, long window) throws Exception {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        // Icons
        {
            ByteBuffer icon16;
            ByteBuffer icon32;
            try {
                icon16 = ioResourceToByteBuffer(path);
                icon32 = ioResourceToByteBuffer(path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
                ByteBuffer pixels16 = stbi_load_from_memory(icon16, w, h, comp, 4);
                icons.position(0).width(w.get(0)).height(h.get(0)).pixels(pixels16);

                ByteBuffer pixels32 = stbi_load_from_memory(icon32, w, h, comp, 4);
                icons.position(1).width(w.get(0)).height(h.get(0)).pixels(pixels32);

                icons.position(0);
                glfwSetWindowIcon(window, icons);

                stbi_image_free(pixels32);
                stbi_image_free(pixels16);
            }
        }

        memFree(comp);
        memFree(h);
        memFree(w);

    }

    public static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
        ByteBuffer buffer;
        int bufferSize = 8 * 1024;
        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (InputStream source = GlfwWindow.class.getClassLoader().getResourceAsStream(resource);
                 ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}

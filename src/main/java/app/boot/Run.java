package app.boot;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.system.NativeLibraryLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.nfd.NFDPathSet;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.nfd.NativeFileDialog.*;


public class Run {
    public static void main(String[] args) {
        bullet3();
        helloNFD();
    }

    private static void helloNFD() {
        int mod;
        String modDescr;

        mod = GLFW_MOD_CONTROL;
        modDescr = "Ctrl";


        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        long window = glfwCreateWindow(300, 300, "Hello NativeFileDialog!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (windowHnd, key, scancode, action, mods) -> {
            if (action == GLFW_RELEASE) {
                return;
            }

            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(windowHnd, true);
                    break;
                case GLFW_KEY_O:
                    if ((mods & mod) != 0) {
                        if ((mods & GLFW_MOD_SHIFT) != 0) {
                            openMulti();
                        } else if ((mods & GLFW_MOD_ALT) != 0) {
                            openFolder();
                        } else {
                            openSingle();
                        }
                    }
                    break;
                case GLFW_KEY_S:
                    if ((mods & mod) != 0) {
                        save();
                    }
                    break;
            }
        });

        // Center window
        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        glfwSetWindowPos(
                window,
                (vidmode.width() - 300) / 2,
                (vidmode.height() - 300) / 2
        );

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(1);

        System.out.println("Press " + modDescr + "+O to launch the single file open dialog.");
        System.out.println("Press " + modDescr + "+Shift+O to launch the multi file open dialog.");
        System.out.println("Press " + modDescr + "+Alt+O to launch the folder select dialog.");
        System.out.println("Press " + modDescr + "+S to launch the file save dialog.");
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);
            glfwSwapBuffers(window);
        }

        GL.setCapabilities(null);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private static void openSingle() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            checkResult(
                    NFD_OpenDialog("png,jpg;pdf", null, outPath),
                    outPath
            );
        } finally {
            memFree(outPath);
        }
    }

    private static void openMulti() {
        try (NFDPathSet pathSet = NFDPathSet.calloc()) {
            int result = NFD_OpenDialogMultiple("png,jpg;pdf", null, pathSet);
            switch (result) {
                case NFD_OKAY:
                    long count = NFD_PathSet_GetCount(pathSet);
                    for (long i = 0; i < count; i++) {
                        String path = NFD_PathSet_GetPath(pathSet, i);
                        System.out.format("Path %d: %s\n", i, path);
                    }
                    NFD_PathSet_Free(pathSet);
                    break;
                case NFD_CANCEL:
                    System.out.println("User pressed cancel.");
                    break;
                default: // NFD_ERROR
                    System.err.format("Error: %s\n", NFD_GetError());
            }
        }
    }

    private static void openFolder() {
        PointerBuffer outPath = memAllocPointer(1);

        try {
            checkResult(
                    NFD_PickFolder((ByteBuffer) null, outPath),
                    outPath
            );
        } finally {
            memFree(outPath);
        }
    }

    private static void save() {
        PointerBuffer savePath = memAllocPointer(1);

        try {
            checkResult(
                    NFD_SaveDialog("png,jpg;pdf", null, savePath),
                    savePath
            );
        } finally {
            memFree(savePath);
        }
    }

    private static void checkResult(int result, PointerBuffer path) {
        switch (result) {
            case NFD_OKAY:
                System.out.println("Success!");
                System.out.println(path.getStringUTF8(0));
                nNFD_Free(path.get(0));
                break;
            case NFD_CANCEL:
                System.out.println("User pressed cancel.");
                break;
            default: // NFD_ERROR
                System.err.format("Error: %s\n", NFD_GetError());
        }
    }


    private static void bullet3() {
        /*
         * Load a native library from ~/Downloads directory.
         */
        File downloadDirectory = new File("src\\main\\resources", "bulletJNI");
        NativeLibraryLoader.loadLibbulletjme(true, downloadDirectory, "Release", "Sp");
        /*
         * Create a PhysicsSpace using DBVT for broadphase.
         */
        PhysicsSpace.BroadphaseType bPhase = PhysicsSpace.BroadphaseType.DBVT;
        PhysicsSpace space = new PhysicsSpace(bPhase);
        /*
         * Add a static horizontal plane at y=-1.
         */
        float planeY = -1f;
        Plane plane = new Plane(Vector3f.UNIT_Y, planeY);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        float mass = PhysicsBody.massForStatic;
        PhysicsRigidBody floor = new PhysicsRigidBody(planeShape, mass);
        space.addCollisionObject(floor);
        /*
         * Add a vehicle with a boxy chassis.
         */
        CompoundCollisionShape chassisShape = new CompoundCollisionShape();
        BoxCollisionShape box = new BoxCollisionShape(1.2f, 0.5f, 2.4f);
        chassisShape.addChildShape(box, 0f, 1f, 0f);
        mass = 400f;
        PhysicsVehicle vehicle = new PhysicsVehicle(chassisShape, mass);
        vehicle.setMaxSuspensionForce(9e9f);
        vehicle.setSuspensionCompression(4f);
        vehicle.setSuspensionDamping(6f);
        vehicle.setSuspensionStiffness(50f);
        /*
         * Add 4 wheels, 2 in front (for steering) and 2 in back.
         */
        Vector3f axleDirection = new Vector3f(-1, 0, 0);
        Vector3f suspensionDirection = new Vector3f(0, -1, 0);
        float restLength = 0.3f;
        float radius = 0.5f;
        float xOffset = 1f;
        float yOffset = 0.5f;
        float zOffset = 2f;
        vehicle.addWheel(new Vector3f(-xOffset, yOffset, zOffset),
                suspensionDirection, axleDirection, restLength, radius,
                true);
        vehicle.addWheel(new Vector3f(xOffset, yOffset, zOffset),
                suspensionDirection, axleDirection, restLength, radius,
                true);
        vehicle.addWheel(new Vector3f(-xOffset, yOffset, -zOffset),
                suspensionDirection, axleDirection, restLength, radius,
                false);
        vehicle.addWheel(new Vector3f(xOffset, yOffset, -zOffset),
                suspensionDirection, axleDirection, restLength, radius,
                false);

        space.add(vehicle);
        vehicle.accelerate(500f);
        /*
         * 150 iterations with a 20-msec timestep
         */
        float timeStep = 0.02f;
        Vector3f location = new Vector3f();
        for (int i = 0; i < 150; ++i) {
            space.update(timeStep, 0);
            vehicle.getPhysicsLocation(location);
            System.out.println(location);
        }
    }
}

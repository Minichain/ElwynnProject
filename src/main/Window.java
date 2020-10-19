package main;

import audio.OpenALManager;
import listeners.InputListenerManager;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static utils.IOUtils.ioResourceToByteBuffer;

public class Window {
    private static GLFWWindowSizeCallback windowSizeCallback;
    private static GLFWWindowPosCallback windowPosCallback;
    private static long window = -1;
    private static int width = Parameters.getResolutionWidth();
    private static int height = Parameters.getResolutionHeight();
    private static int positionX = 50;
    private static int positionY = 50;
    private static float[] cameraWindowScaleFactor;

    public static void init() {
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        long window = glfwCreateWindow(Window.getWidth(), Window.getHeight(), "ElwynnProject", 0, 0);
        setWindow(window);
        setWindowIcon();
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        OpenGLManager.prepareOpenGL();
        OpenALManager.prepareOpenAL();
        glfwPollEvents();

        /** INPUT LISTENER **/
        InputListenerManager.initMyInputListener();
        setFullScreen(Parameters.isFullScreen());
        setWindowSize(Parameters.getResolutionWidth(), Parameters.getResolutionHeight());

        /** CALLBACKS **/
//        initWindowSizeCallBack();
        initWindowPosCallBack();

        setWindowPosition(positionX, positionY);
        setWindowSize(width, height);
    }

    private static void setWindowPosition(int x, int y) {
        glfwSetWindowPos(getWindow(), x, y);
        positionX = x;
        positionY = y;
    }

    public static void setWindowSize(int w, int h) {
        glfwSetWindowSize(window, w, h);
        glViewport(0, 0, Parameters.getResolutionWidth(), Parameters.getResolutionHeight());
        width = w;
        height = h;
        cameraWindowScaleFactor = new float[]{(float) width / (float) Parameters.getResolutionWidth(), (float) height / (float) Parameters.getResolutionHeight()};
    }

    private static void initWindowSizeCallBack() {
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                setWindowSize(width, height);
            }
        };
        glfwSetWindowSizeCallback(getWindow(), windowSizeCallback);
    }

    private static void initWindowPosCallBack() {
        windowPosCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int x, int y) {
                setWindowPosition(x, y);
            }
        };
        glfwSetWindowPosCallback(getWindow(), windowPosCallback);
    }

    public static void setFullScreen(boolean fullScreen) {
        Parameters.setFullScreen(fullScreen);
        try {
            long monitor = glfwGetPrimaryMonitor();
            if (fullScreen) {
                GLFWVidMode vidMode = glfwGetVideoMode(monitor);
                if (vidMode != null) {
                    glfwSetWindowMonitor(getWindow(), monitor, 0, 0, Parameters.getResolutionWidth(), Parameters.getResolutionHeight(), Parameters.getFramesPerSecond());
                }
            } else {
                glfwSetWindowMonitor(getWindow(), 0, positionX, positionY, Window.getWidth(), Window.getHeight(), 0);
            }
        } catch (Exception e) {
            System.err.print("Error setting full screen to " + fullScreen);
            Parameters.setFullScreen(!fullScreen);
        }
    }

    public static long getWindow() {
        return window;
    }

    public static void setWindow(long window) {
        Window.window = window;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static float[] getCameraWindowScaleFactor() {
        return cameraWindowScaleFactor;
    }

    /**
     * Example extracted from "https://github.com/LWJGL/lwjgl3/blob/7e1dcb80160d9dbce2b041cbf84eec0da15f2661/modules/core/src/test/java/org/lwjgl/demo/glfw/Events.java#L87"
     **/
    private static void setWindowIcon() {
        ByteBuffer icon16;
        ByteBuffer icon32;
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);
        try {
            icon16 = ioResourceToByteBuffer("res/icons/icon_16x16.png", 2048);
            icon32 = ioResourceToByteBuffer("res/icons/icon_32x32.png", 4096);
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

        memFree(comp);
        memFree(h);
        memFree(w);
    }
}

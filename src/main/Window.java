package main;

import listeners.MyInputListener;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.glViewport;

public class Window {
    private static GLFWWindowSizeCallback windowSizeCallback;
    private static long window = -1;
    private static int width = Parameters.getResolutionWidth();
    private static int height = Parameters.getResolutionHeight();
    private static int positionX;
    private static int positionY;
    private static int widthFullScreen = Parameters.getResolutionWidth();
    private static int heightFullScreen = Parameters.getResolutionHeight();
    private static float[] cameraWindowScaleFactor;

    public static void start() {
        long window = glfwCreateWindow(Window.getWidth(), Window.getHeight(), "ElwynnProject", 0, 0);
        setWindow(window);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        MyOpenGL.prepareOpenGL();
        MyOpenAL.prepareOpenAL();
        glfwPollEvents();

        MyInputListener.initMyInputListener();
        initWindowSizeCallBack();
        updateWindowPosition();
        updateWindowSize();

        setFullScreen(Parameters.isFullScreen());
    }

    private static void updateWindowPosition() {
        int[] tempX = new int[1], tempY = new int[1];
        glfwGetWindowPos(window, tempX, tempY);
        positionX = tempX[0];
        positionY = tempY[0];
    }

    private static void updateWindowSize() {
        int[] tempWidth = new int[1], tempHeight = new int[1];
        glfwGetWindowSize(window, tempWidth, tempHeight);
        setWidth(tempWidth[0]);
        setHeight(tempHeight[0]);
        cameraWindowScaleFactor = new float[]{(float) tempWidth[0] / (float) Parameters.getResolutionWidth(), (float) tempHeight[0] / (float) Parameters.getResolutionHeight()};
    }

    private static void initWindowSizeCallBack() {
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                glViewport(0, 0, width, height);
                updateWindowSize();
            }
        };
        glfwSetWindowSizeCallback(getWindow(), windowSizeCallback);
    }

    public static void setFullScreen(boolean fullScreen) {
        Parameters.setFullScreen(fullScreen);
        try {
            long monitor = glfwGetPrimaryMonitor();
            if (fullScreen) {
                GLFWVidMode vidMode = glfwGetVideoMode(monitor);
                if (vidMode != null) {
                    glfwSetWindowMonitor(getWindow(), monitor, 0, 0, vidMode.width(), vidMode.height(), Parameters.getFramesPerSecond());
                }
            } else {
                glfwSetWindowMonitor(getWindow(), 0, 0, 0, Window.getWidth(), Window.getHeight(), 0);
                glfwSetWindowPos(getWindow(), positionX, positionY);
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
        return Parameters.isFullScreen() ? widthFullScreen : width;
    }

    public static void setWidth(int windowWidth) {
        if (Parameters.isFullScreen()) {
            Window.widthFullScreen = windowWidth;
        } else {
            Window.width = windowWidth;
        }
    }

    public static int getHeight() {
        return Parameters.isFullScreen() ? heightFullScreen : height;
    }

    public static void setHeight(int windowHeight) {
        if (Parameters.isFullScreen()) {
            Window.heightFullScreen = windowHeight;
        } else {
            Window.height = windowHeight;
        }
    }

    public static float[] getCameraWindowScaleFactor() {
        return cameraWindowScaleFactor;
    }
}

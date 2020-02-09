package main;

import entities.Camera;
import entities.Scene;
import listeners.MyInputListener;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {
    private static GLFWWindowSizeCallback windowSizeCallback;

    public static void startGame() {
        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);
        Parameters.getInstance().setWindow(window);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);
        MyOpenGL.prepareOpenGL();
        glfwPollEvents();

        MyInputListener.initMyInputListener();
        initWindowSizeCallBack();

        GameStatus.getInstance().setGameRunning(true);
    }

    private static void initWindowSizeCallBack() {
        windowSizeCallback = new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                Parameters.getInstance().setWindowWidth(width);
                Parameters.getInstance().setWindowHeight(height);
                glViewport(0, 0, width, height);
            }
        };
        glfwSetWindowSizeCallback(Parameters.getInstance().getWindow(), windowSizeCallback);
    }

    public static void update(long timeElapsed) {
        Scene.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);
    }

    public static void render(long timeElapsed) {
        MyOpenGL.prepareFrame();
        Scene.getInstance().render();
        UserInterface.getInstance().render(timeElapsed);
    }
}


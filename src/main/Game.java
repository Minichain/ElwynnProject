package main;

import entities.Camera;
import entities.Character;
import entities.Entity;
import entities.Scene;
import listeners.MyInputListener;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {
    private static GLFWWindowSizeCallback windowSizeCallback;

    public static void startGame() {
        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);
        Parameters.getInstance().setWindow(window);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwPollEvents();

        MyInputListener.initMyInputListener();

        windowSizeCallback = new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                Parameters.getInstance().setWindowWidth(width);
                Parameters.getInstance().setWindowHeight(height);
                glViewport(0, 0, width, height);
            }
        };
        glfwSetWindowSizeCallback(Parameters.getInstance().getWindow(), windowSizeCallback);

        GameStatus.getInstance().setGameRunning(true);
    }

    public static void update(long timeElapsed) {
        Character.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);

//        System.out.println("Character at (" + Character.getInstance().getCoordinates().x + ", " + Character.getInstance().getCoordinates().y + ")");
//        System.out.println("Camera at (" + Camera.getInstance().getCoordinates().x + ", " + Camera.getInstance().getCoordinates().y + ")");
    }

    public static void render(long timeElapsed) {
        MyOpenGL.prepareOpenGL();

        Scene.getInstance().render();
        UserInterface.getInstance().render(timeElapsed);
    }
}


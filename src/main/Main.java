package main;

import entities.Character;
import listeners.MyInputListener;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {
        long timeElapsed;
        long lastUpdateTime = 0;
        long currentTime;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);

        glfwShowWindow(window);

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwPollEvents();

        MyInputListener myInputListener = new MyInputListener(window);

        while(!glfwWindowShouldClose(window)) {
            try {
                //Compute the time elapsed since the last frame
                currentTime = System.currentTimeMillis();
                timeElapsed = currentTime - lastUpdateTime;


                glfwPollEvents();
                glClear(GL_COLOR_BUFFER_BIT);

                Game.updateScene(timeElapsed);
                Game.renderScene();

                glfwSwapBuffers(window);

                lastUpdateTime = currentTime;


                //Wait time until processing next frame. FPS locked.
                Thread.sleep(1000 / Parameters.getInstance().getForegroundFramesPerSecond());
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(1);
            }
        }

        glfwTerminate();
        System.exit(0);
    }
}
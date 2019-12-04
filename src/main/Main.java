package main;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);

        glfwShowWindow(window);

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        while(!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT);

            glBegin(GL_QUADS);
            glVertex2f(-0.5f, 0.5f);
            glVertex2f(0.5f, 0.5f);
            glVertex2f(0.5f, -0.5f);
            glVertex2f(-0.5f, -0.5f);
            glEnd();

            glfwSwapBuffers(window);
        }

        glfwTerminate();
        System.exit(0);
    }
}
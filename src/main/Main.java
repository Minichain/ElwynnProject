package main;

import listeners.MyInputListener;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {
        long timeElapsed;
        long lastUpdateTime = System.currentTimeMillis();
        long maxTimeBetweenFrames = 1000 / Parameters.getInstance().getForegroundFramesPerSecond();
        long timeSpentRendering;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        Game.startGame();

        while(!glfwWindowShouldClose(Parameters.getInstance().getWindow()) && GameStatus.getInstance().isGameRunning()) {
            try {
                //Compute the time elapsed since the last frame
                timeElapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();

                glfwPollEvents();
                glClear(GL_COLOR_BUFFER_BIT);

                Game.update(timeElapsed);
                Game.render(timeElapsed);

                glfwSwapBuffers(Parameters.getInstance().getWindow());

                timeSpentRendering = System.currentTimeMillis() - lastUpdateTime;
                //Wait time until processing next frame. FPS locked.
                if (timeSpentRendering < maxTimeBetweenFrames) {
                    Thread.sleep(maxTimeBetweenFrames - timeSpentRendering);
                }
            } catch (InterruptedException e) {
                System.out.println(e);
                exit(1);
            }
        }

        exit(0);
    }

    public static void exit(int status) {
        MyInputListener.release();
        glfwTerminate();
        System.exit(status);
    }
}
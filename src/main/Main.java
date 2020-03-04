package main;

import listeners.MyInputListener;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) {
        long timeElapsed = 0;
        long lastUpdateTime = System.currentTimeMillis();
        long maxTimeBetweenFrames = 1000 / Parameters.getFramesPerSecond();
        long timeSpentUpdatingAndRendering;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        Game.startGame();

        while (!glfwWindowShouldClose(Window.getWindow()) && GameStatus.getStatus() != GameStatus.Status.STOPPED) {
            try {
                //Compute the time elapsed since the last frame
                GameStatus.setRuntime(GameStatus.getRuntime() + timeElapsed);
                timeElapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();

                glfwPollEvents();

                Game.update(timeElapsed);
                Game.render(timeElapsed);

                glfwSwapBuffers(Window.getWindow());

                timeSpentUpdatingAndRendering = System.currentTimeMillis() - lastUpdateTime;
                //Wait time until processing next frame. FPS locked.
                if (timeSpentUpdatingAndRendering < maxTimeBetweenFrames) {
                    Thread.sleep(maxTimeBetweenFrames - timeSpentUpdatingAndRendering);
                }
            } catch (InterruptedException e) {
                System.out.println(e);
                exit(1);
            }
        }

        exit(0);
    }

    public static void exit(int status) {
        Game.stopGame();
        glfwTerminate();

        System.exit(status);
    }
}
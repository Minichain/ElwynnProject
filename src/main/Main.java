package main;

import listeners.MyInputListener;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) {
        long timeElapsed = 0;
        long lastUpdateTime = System.currentTimeMillis();
        long maxTimeBetweenFrames = 1000 / Parameters.getFramesPerSecond();
        long timeSpentRendering;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        Game.startGame();

        while(!glfwWindowShouldClose(Parameters.getWindow()) && GameStatus.getInstance().isGameRunning()) {
            try {
                //Compute the time elapsed since the last frame
                GameStatus.RUNTIME += timeElapsed;
                timeElapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();

                glfwPollEvents();

                Game.update(timeElapsed);
                Game.render(timeElapsed);

                glfwSwapBuffers(Parameters.getWindow());

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
        Game.stopGame();
        MyInputListener.release();
        glfwTerminate();
        System.exit(status);
    }
}
package main;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("natives/windows/x64").getAbsolutePath());

        long timeElapsed = 0;
        long lastUpdateTime = System.currentTimeMillis();
        long maxTimeBetweenFrames;
        long timeSpentUpdatingAndRendering;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        Log.l("OS Name " + System.getProperty("os.name"));
        Log.l("OS Version " + System.getProperty("os.version"));

        Game.startGame();

        while (!glfwWindowShouldClose(Window.getWindow()) && GameStatus.getStatus() != GameStatus.Status.STOPPED) {
            try {
                maxTimeBetweenFrames = 1000 / Parameters.getFramesPerSecond();

                //Compute the time elapsed since the last frame
                GameStatus.setRuntime(GameStatus.getRuntime() + timeElapsed);
                timeElapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();

                Game.update(timeElapsed);
                Game.render(timeElapsed);

                glfwSwapBuffers(Window.getWindow());
                glfwPollEvents();

                timeSpentUpdatingAndRendering = System.currentTimeMillis() - lastUpdateTime;
                //Wait time until processing next frame. FPS locked.
                if (timeSpentUpdatingAndRendering < maxTimeBetweenFrames) {
                    Thread.sleep(maxTimeBetweenFrames - timeSpentUpdatingAndRendering);
                }
            } catch (InterruptedException e) {
                Log.e(String.valueOf(e));
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
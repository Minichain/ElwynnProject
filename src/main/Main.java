package main;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) {
        //Load natives files
        System.setProperty("org.lwjgl.librarypath", new File("natives/windows/x64").getAbsolutePath());

        long timeElapsedNanos = 0;
        long lastUpdateTime = System.nanoTime();
        long maxTimeBetweenFrames;

        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(0);
        }

        Log.l("OS Name " + System.getProperty("os.name"));
        Log.l("OS Version " + System.getProperty("os.version"));

        Game.startGame();

        while (!glfwWindowShouldClose(Window.getWindow()) && GameStatus.getStatus() != GameStatus.Status.STOPPED) {
            maxTimeBetweenFrames = 1000000000 / Parameters.getFramesPerSecond();

            Game.update(timeElapsedNanos);
            Game.render(timeElapsedNanos);

            glfwSwapBuffers(Window.getWindow());
            glfwPollEvents();

            //Wait time until processing next frame. FPS locked.
            timeElapsedNanos = System.nanoTime() - lastUpdateTime;
            while (timeElapsedNanos < maxTimeBetweenFrames) {
                timeElapsedNanos = System.nanoTime() - lastUpdateTime;
            }

            lastUpdateTime = System.nanoTime();
        }

        exit(0);
    }

    public static void exit(int status) {
        Game.stopGame();
        glfwTerminate();

        System.exit(status);
    }
}
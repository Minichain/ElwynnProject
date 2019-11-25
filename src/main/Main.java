package main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration applicationConfiguration = new LwjglApplicationConfiguration();
        applicationConfiguration.title = "ElwynnProject";
        applicationConfiguration.useGL30 = false;
        applicationConfiguration.width = Parameters.getInstance().getWindowWidth();
        applicationConfiguration.height = Parameters.getInstance().getWindowHeight();
        applicationConfiguration.foregroundFPS = 60;
        applicationConfiguration.backgroundFPS = 30;
        new LwjglApplication(new Game(), applicationConfiguration);
    }
}
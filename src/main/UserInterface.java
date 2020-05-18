package main;

import entities.Player;
import listeners.InputListenerManager;
import menu.Menu;
import scene.Camera;
import scene.Scene;
import scene.Tile;
import scene.TileMap;
import text.FloatingText;
import text.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class UserInterface {
    private static UserInterface instance = null;

    public UserInterface() {
        TextRendering.init();
    }

    public static UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    public void render(long timeElapsed) {
        renderDebugUI(timeElapsed);
    }

    private void renderDebugUI(long timeElapsed) {
        OpenGLManager.releaseCurrentShader();
//        System.out.println("Render Debug UI");

        if (Parameters.isDebugMode()) {
            if (timeElapsed <= 0) timeElapsed = 1;
            float fps = 1000f / timeElapsed;
            FramesPerSecond.update(fps);

            /** DEBUG TEXT **/
            ArrayList<String> textList = new ArrayList<>();
            int textScale = 2;
            int topMargin = 10;
            int leftMargin = 10;
            int gapBetweenTexts = 10 * textScale;

            textList.add("Project Version: " + Parameters.getProjectVersion());
            textList.add("Show/Hide Debug Info: F1");
            textList.add("Reset Game: F4");
            textList.add("Save World: F5");
            textList.add("FPS: " + String.format("%.2f", FramesPerSecond.getFramesPerSecond()));
            textList.add("GPU calls: " + OpenGLManager.GPU_CALLS);
            textList.add("Resolution: " + Parameters.getResolutionWidth() + " x " + Parameters.getResolutionHeight());
            textList.add("Window Size: " + Window.getWidth() + " x " + Window.getHeight());
            textList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
            textList.add("Num of Tiles: " + TileMap.getArrayOfTiles().length + " x " + TileMap.getArrayOfTiles()[0].length + " x " + Tile.getNumOfLayers());
            textList.add("Camera World Coordinates: (" + (float) Camera.getInstance().getCoordinates().x + ", " + (float) Camera.getInstance().getCoordinates().y + ")");
            textList.add("Camera Zoom: " + (float) Camera.getZoom());
            textList.add("Player World Coordinates: (" + (float) Player.getInstance().getWorldCoordinates().x + ", " + (float) Player.getInstance().getWorldCoordinates().y + ")");
            textList.add("Player Camera Coordinates: (" + (float) Player.getInstance().getCameraCoordinates().x + ", " + (float) Player.getInstance().getCameraCoordinates().y + ")");
            textList.add("Player Health: " + Player.getInstance().getHealth());
            textList.add("Mouse Camera Coordinates: (" + (float) InputListenerManager.getMouseCameraCoordinates().x + ", " + (float) InputListenerManager.getMouseCameraCoordinates().y + ")");
            textList.add("Mouse World Coordinates: (" + (float) InputListenerManager.getMouseWorldCoordinates().x + ", " + (float) InputListenerManager.getMouseWorldCoordinates().y + ")");
            textList.add("Mouse Window Coordinates: (" + InputListenerManager.getMouseWindowCoordinates().x + ", " + InputListenerManager.getMouseWindowCoordinates().y + ")");
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                textList.add("Game Mode: " + GameMode.getGameMode() + ", Creative Mode: " + GameMode.getCreativeMode());
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    textList.add("Layer Editing: " + GameMode.getLayerEditing());
                }
            } else {
                textList.add("Game Mode: " + " " + GameMode.getGameMode());
            }
            textList.add("Game time: " + String.format("%.2f", GameTime.getGameTime()) + ", light: " + String.format("%.2f", GameTime.getLight()));
            textList.add("Current Weather: " + Weather.getWeatherStatus());
            if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
                textList.add("GAME PAUSED");
            }

            TextRendering.renderText(leftMargin, topMargin, gapBetweenTexts, textList, textScale);

            /** DEBUG LINES **/
            glDisable(GL_BLEND);

            OpenGLManager.glBegin(GL_LINES);

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glVertex2i(Parameters.getResolutionWidth() / 2, 0);
            glVertex2i(Parameters.getResolutionWidth() / 2, Parameters.getResolutionHeight());
            glVertex2i(0, Parameters.getResolutionHeight() / 2);
            glVertex2i(Parameters.getResolutionWidth(), Parameters.getResolutionHeight() / 2);

            glEnd();

            glEnable(GL_BLEND);
        }

        /** FLOATING TEXT **/
        FloatingText.renderAndUpdate(timeElapsed);

        /** HUD **/
        HeadUpDisplay.render(timeElapsed);

        /** MENU **/
        if (Menu.getInstance().isShowing()) {
            Menu.getInstance().render(timeElapsed);
        }
    }
}

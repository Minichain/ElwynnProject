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
    private ArrayList<String> debugTextList;

    public UserInterface() {
        debugTextList = new ArrayList<>();
        TextRendering.init();
    }

    public static UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        /** UPDATE FPS **/
        if (timeElapsed <= 0) timeElapsed = 1;
        float fps = 1000f / timeElapsed;
        FramesPerSecond.update(fps);

        /** UPDATE DEBUG TEXT **/
        if (Parameters.isDebugMode()) {
            debugTextList.clear();

            debugTextList.add("Project Version: " + Parameters.getProjectVersion());
            debugTextList.add("OpenGL version " + glGetString(GL_VERSION));
            debugTextList.add("Show/Hide Debug Info: F1");
            debugTextList.add("Reset Game: F4");
            debugTextList.add("Save World: F5");
            debugTextList.add("FPS: " + String.format("%.2f", FramesPerSecond.getFramesPerSecond()));
            debugTextList.add("GPU calls: " + OpenGLManager.GPU_CALLS);
            debugTextList.add("Resolution: " + Parameters.getResolutionWidth() + " x " + Parameters.getResolutionHeight());
            debugTextList.add("Window Size: " + Window.getWidth() + " x " + Window.getHeight());
            debugTextList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
            debugTextList.add("Num of Tiles: " + TileMap.getArrayOfTiles().length + " x " + TileMap.getArrayOfTiles()[0].length + " x " + Tile.getNumOfLayers());
            debugTextList.add("Camera World Coordinates: (" + (float) Camera.getInstance().getCoordinates().x + ", " + (float) Camera.getInstance().getCoordinates().y + ")");
            debugTextList.add("Camera Zoom: " + (float) Camera.getZoom());
            debugTextList.add("Player World Coordinates: (" + (float) Player.getInstance().getWorldCoordinates().x + ", " + (float) Player.getInstance().getWorldCoordinates().y + ")");
            debugTextList.add("Player Camera Coordinates: (" + (float) Player.getInstance().getCameraCoordinates().x + ", " + (float) Player.getInstance().getCameraCoordinates().y + ")");
            debugTextList.add("Player Health: " + Player.getInstance().getHealth());
            debugTextList.add("Mouse Camera Coordinates: (" + (float) InputListenerManager.getMouseCameraCoordinates().x + ", " + (float) InputListenerManager.getMouseCameraCoordinates().y + ")");
            debugTextList.add("Mouse World Coordinates: (" + (float) InputListenerManager.getMouseWorldCoordinates().x + ", " + (float) InputListenerManager.getMouseWorldCoordinates().y + ")");
            debugTextList.add("Mouse Window Coordinates: (" + InputListenerManager.getMouseWindowCoordinates().x + ", " + InputListenerManager.getMouseWindowCoordinates().y + ")");
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                debugTextList.add("Game Mode: " + GameMode.getGameMode() + ", Creative Mode: " + GameMode.getCreativeMode());
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    debugTextList.add("Layer Editing: " + GameMode.getLayerEditing());
                }
            } else {
                debugTextList.add("Game Mode: " + " " + GameMode.getGameMode());
            }
            debugTextList.add("Game time: " + String.format("%.2f", GameTime.getGameTime()) + ", light: " + String.format("%.2f", GameTime.getLight()));
            debugTextList.add("Current Weather: " + Weather.getWeatherStatus());
            if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
                debugTextList.add("GAME PAUSED");
            }
        }
    }

    public void render(long timeElapsed) {
        OpenGLManager.releaseCurrentShader();

        renderDebugUI(timeElapsed);

        /** FLOATING TEXT **/
        FloatingText.renderAndUpdate(timeElapsed);

        /** HUD **/
        HeadUpDisplay.render(timeElapsed);

        /** MENU **/
        if (Menu.getInstance().isShowing()) {
            Menu.getInstance().render(timeElapsed);
        }
    }

    private void renderDebugUI(long timeElapsed) {
//        System.out.println("Render Debug UI");

        if (!Parameters.isDebugMode()) {
            return;
        }

        /** RENDER DEBUG TEXT **/
        float textScale = 2f * Parameters.getResolutionFactor();
        float topMargin = 10;
        float leftMargin = 10;
        float gapBetweenTexts = 10 * textScale;

        TextRendering.renderText(leftMargin, topMargin, gapBetweenTexts, debugTextList, textScale);

        /** RENDER DEBUG LINES **/
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
}

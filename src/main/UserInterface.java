package main;

import entities.*;
import entities.Character;
import listeners.MyInputListener;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class UserInterface {
    public static UserInterface instance = null;

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
        renderCursorUI(timeElapsed);
        renderDebugUI(timeElapsed);
    }

    private void renderDebugUI(long timeElapsed) {
        if (Parameters.isDebugMode()) {
            if (timeElapsed <= 0) timeElapsed = 1;
            float fps = 1000 / timeElapsed;
            double[] characterCameraCoordinates = Character.getInstance().getCurrentCoordinates().toCameraCoordinates();

            /** DEBUG TEXT **/
            ArrayList<String> textList = new ArrayList<>();
            int textScale = 2;
            int topMargin = 10;
            int leftMargin = 10;
            int gapBetweenTexts = 10 * textScale;

            textList.add("Show/Hide Debug Info: F1, Normal Mode: F2, Creative Mode: F3, Save World: F5");
            textList.add("FPS: " + fps);
            textList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
            textList.add("Num of Tiles: " + TileMap.getArrayOfTiles().length + " x " + TileMap.getArrayOfTiles()[0].length + " x " + Tile.getNumOfLayers());
            textList.add("Camera World Coordinates: (" + (float) Camera.getInstance().getCoordinates().x + ", " + (float) Camera.getInstance().getCoordinates().y + ")");
            textList.add("Camera Zoom: " + (float) Camera.getZoom());
            textList.add("Character World Coordinates: (" + (float) Character.getInstance().getCurrentCoordinates().x + ", " + (float) Character.getInstance().getCurrentCoordinates().y + ")");
            textList.add("Character Camera Coordinates: (" + (float) characterCameraCoordinates[0] + ", " + (float) characterCameraCoordinates[1] + ")");
            textList.add("Character Health: " + Character.getInstance().getHealth());
            textList.add("Mouse Camera Coordinates: (" + (float) MyInputListener.getMousePositionX() + ", " + (float) MyInputListener.getMousePositionY() + ")");
            double[] mouseWorldCoordinates = new Coordinates(MyInputListener.getMousePositionX(), MyInputListener.getMousePositionY()).toWorldCoordinates();
            textList.add("Mouse World Coordinates: (" + (float) mouseWorldCoordinates[0] + ", " + (float) mouseWorldCoordinates[1] + ")");
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                textList.add("Game Mode: " + GameMode.getGameMode() + ", Creative Mode: " + GameMode.getCreativeMode());
            } else {
                textList.add("Game Mode: " + " " + GameMode.getGameMode());
            }

            TextRendering.renderText(leftMargin, topMargin, gapBetweenTexts, textList, textScale);

            /** DEBUG LINES **/
            glDisable(GL_BLEND);
            glBegin(GL_LINES);
            glLineWidth(4);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glVertex2i(Parameters.getWindowWidth() / 2, 0);
            glVertex2i(Parameters.getWindowWidth() / 2, Parameters.getWindowHeight());
            glVertex2i(0, Parameters.getWindowHeight() / 2);
            glVertex2i(Parameters.getWindowWidth(), Parameters.getWindowHeight() / 2);
            glEnd();
            glEnable(GL_BLEND);
        }

        if (Character.getInstance().getStatus() == Character.Status.DEAD) {
            String text = "YOU DIED";
            int scale = 4;
            TextRendering.renderText((Parameters.getWindowWidth() / 2) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2), 450, text, scale);
        }
    }

    private void renderCursorUI(long timeElapsed) {
        int mouseX = MyInputListener.getMousePositionX();
        int mouseY = MyInputListener.getMousePositionY();
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE
                && 0 < mouseX && mouseX < Parameters.getWindowWidth()
                && 0 < mouseY && mouseY < Parameters.getWindowHeight()) {
            TileMap.bindTileSetTexture();
            glBegin(GL_QUADS);
            TileMap.drawTile(MyInputListener.getMouseWheelPosition(), mouseX, mouseY, 2, 1f, 1f, 1f, true);
            glEnd();
        }

        FloatingText.renderAndUpdate();
    }
}

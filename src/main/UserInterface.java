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

            textList.add("Show/Hide Debug Info: F1");
            textList.add("Pause/Resume Game: F2");
            textList.add("Creative/Normal Mode: F3");
            textList.add("Reset Game: F4");
            textList.add("Save World: F5");
            textList.add("FPS: " + fps);
            textList.add("Resolution: " + Parameters.getResolutionWidth() + " x " + Parameters.getResolutionHeight());
            textList.add("Window Size: " + Parameters.getWindowWidth() + " x " + Parameters.getWindowHeight());
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
            if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
                textList.add("GAME PAUSED");
            }

            TextRendering.renderText(leftMargin, topMargin, gapBetweenTexts, textList, textScale);

            /** DEBUG LINES **/
            glDisable(GL_BLEND);
            glBegin(GL_LINES);
            glLineWidth(4);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glVertex2i(Parameters.getResolutionWidth() / 2, 0);
            glVertex2i(Parameters.getResolutionWidth() / 2, Parameters.getResolutionHeight());
            glVertex2i(0, Parameters.getResolutionHeight() / 2);
            glVertex2i(Parameters.getResolutionWidth(), Parameters.getResolutionHeight() / 2);
            glEnd();
            glEnable(GL_BLEND);
        }

        /** YOU DIED **/
        if (Character.getInstance().getStatus() == Character.Status.DEAD) {
            String text = "YOU DIED";
            int scale = 4;
            TextRendering.renderText((Parameters.getWindowWidth() / 2) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2), 450, text, scale);
        }

        /** CREATIVE MODE UI **/
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            int previousTilesToShow = 5;
            int currentTile;
            int x;
            int y;

            TileMap.bindTileSetTexture();
            glBegin(GL_QUADS);
            for (int i = 0; i < 25; i++) {
                currentTile = MyInputListener.getMouseWheelPosition() + i - previousTilesToShow;
                x = 20 + i * 64;
                y = Parameters.getResolutionHeight() - 100;
                if (currentTile == MyInputListener.getMouseWheelPosition()) {   // Highlight the tile we have selected
                    TileMap.drawTile(currentTile, x + 5, y + 5, 2.5, 0f, 0f, 0f, true);
                    TileMap.drawTile(currentTile, x, y, 2.5, 1f, 1f, 1f, true);
                } else {
                    TileMap.drawTile(currentTile, x + 5, y + 5, 2, 0f, 0f, 0f, true);
                    TileMap.drawTile(currentTile, x, y, 2, 0.5f, 0.5f, 0.5f, true);
                }
            }
            glEnd();
        }
    }

    private void renderCursorUI(long timeElapsed) {
        int mouseX = MyInputListener.getMousePositionX();
        int mouseY = MyInputListener.getMousePositionY();
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE
                && 0 < mouseX && mouseX < Parameters.getResolutionWidth()
                && 0 < mouseY && mouseY < Parameters.getResolutionHeight()) {
            TileMap.bindTileSetTexture();
            glBegin(GL_QUADS);
            TileMap.drawTile(MyInputListener.getMouseWheelPosition(), mouseX, mouseY, 2, 1f, 1f, 1f, true);
            glEnd();
        }

        FloatingText.renderAndUpdate();
    }
}

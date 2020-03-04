package main;

import entities.*;
import entities.Player;
import listeners.MyInputListener;
import menu.Menu;

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

            /** DEBUG TEXT **/
            ArrayList<String> textList = new ArrayList<>();
            int textScale = 2;
            int topMargin = 10;
            int leftMargin = 10;
            int gapBetweenTexts = 10 * textScale;

            textList.add("Show/Hide Debug Info: F1");
            textList.add("Reset Game: F4");
            textList.add("Save World: F5");
            textList.add("FPS: " + fps);
            textList.add("Resolution: " + Parameters.getResolutionWidth() + " x " + Parameters.getResolutionHeight());
            textList.add("Window Size: " + Window.getWidth() + " x " + Window.getHeight());
            textList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
            textList.add("Num of Tiles: " + TileMap.getArrayOfTiles().length + " x " + TileMap.getArrayOfTiles()[0].length + " x " + Tile.getNumOfLayers());
            textList.add("Camera World Coordinates: (" + (float) Camera.getInstance().getCoordinates().x + ", " + (float) Camera.getInstance().getCoordinates().y + ")");
            textList.add("Camera Zoom: " + (float) Camera.getZoom());
            textList.add("Player World Coordinates: (" + (float) Player.getInstance().getWorldCoordinates().x + ", " + (float) Player.getInstance().getWorldCoordinates().y + ")");
            textList.add("Player Camera Coordinates: (" + (float) Player.getInstance().getCameraCoordinates().x + ", " + (float) Player.getInstance().getCameraCoordinates().y + ")");
            textList.add("Player Health: " + Player.getInstance().getHealth());
            textList.add("Mouse Camera Coordinates: (" + (float) MyInputListener.getMouseCameraCoordinates().x + ", " + (float) MyInputListener.getMouseCameraCoordinates().y + ")");
            textList.add("Mouse World Coordinates: (" + (float) MyInputListener.getMouseWorldCoordinates().x + ", " + (float) MyInputListener.getMouseWorldCoordinates().y + ")");
            textList.add("Mouse Window Coordinates: (" + MyInputListener.getMouseWindowCoordinates().x + ", " + MyInputListener.getMouseWindowCoordinates().y + ")");
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                textList.add("Game Mode: " + GameMode.getGameMode() + ", Creative Mode: " + GameMode.getCreativeMode());
            } else {
                textList.add("Game Mode: " + " " + GameMode.getGameMode());
            }
            if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
                textList.add("GAME PAUSED");
            }

            glEnable(GL_TEXTURE_2D);
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

        /** FLOATING TEXT **/
        FloatingText.renderAndUpdate(timeElapsed);

        /** YOU DIED **/
        if (Player.getInstance().getStatus() == Player.Status.DEAD) {
            String text = "YOU DIED";
            int scale = 4;
            TextRendering.renderText((Parameters.getResolutionWidth() / 2) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2), 450, text, scale);
        }

        /** CREATIVE MODE UI **/
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            int previousTilesToShow = 5;
            int currentTile;
            int x;
            int y;

            TileMap.bindTileSetTexture();
            glEnable(GL_TEXTURE_2D);
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

        /** MENU **/
        if (Menu.getInstance().isShowing()) {
            Menu.getInstance().render();
        }
    }

    private void renderCursorUI(long timeElapsed) {
        double mouseX = MyInputListener.getMouseCameraCoordinates().x;
        double mouseY = MyInputListener.getMouseCameraCoordinates().y;
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE
                && 0 < mouseX && mouseX < Parameters.getResolutionWidth()
                && 0 < mouseY && mouseY < Parameters.getResolutionHeight()) {
            TileMap.bindTileSetTexture();
            glEnable(GL_TEXTURE_2D);
            glBegin(GL_QUADS);
            TileMap.drawTile(MyInputListener.getMouseWheelPosition(), mouseX, mouseY, 2, 1f, 1f, 1f, true);
            glEnd();
        }
    }
}

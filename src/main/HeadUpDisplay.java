package main;

import entities.Player;
import entities.Sprite;
import entities.SpriteManager;
import scene.TileMap;
import enums.Resolution;
import listeners.InputListenerManager;
import text.TextRendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class HeadUpDisplay {

    public static void render(long timeElapsed) {
        //Width and Height factors relative to 1920x1080 resolution.
        float relativeWidth = (float) Parameters.getResolutionWidth() / (float) Resolution.RESOLUTION_1920_1080.getResolution()[0];
        float relativeHeight = (float) Parameters.getResolutionHeight() / (float) Resolution.RESOLUTION_1920_1080.getResolution()[1];

        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            /** NORMAL MODE HUD **/
            int x = (int) (30f * relativeWidth);
            int y = Parameters.getResolutionHeight() - (int) (70f * relativeHeight);
            int width = (int) (500f * relativeWidth);
            int height = (int) (10f * relativeHeight);
            float healthPercentage = Player.getInstance().getHealth() / Player.MAX_HEALTH;
            float manaPercentage = Player.getInstance().getMana() / Player.MAX_MANA;
            float staminaPercentage = Player.getInstance().getStamina() / Player.MAX_STAMINA;

            glDisable(GL_TEXTURE_2D);
            OpenGLManager.glBegin(GL_TRIANGLES);
            OpenGLManager.drawRectangle(x, y, width, height, 0.5, 1f, 0.25f, 0.25f);
            OpenGLManager.drawRectangle(x, y, width * healthPercentage, height, 1.0, 1f, 0.25f, 0.25f);

            y += (int) (20f * relativeHeight);
            OpenGLManager.drawRectangle(x, y, width, height, 0.5, 0.25f, 0.25f, 1f);
            OpenGLManager.drawRectangle(x, y, width * manaPercentage, height, 1.0, 0.25f, 0.25f, 1f);

            y += (int) (20f * relativeHeight);
            OpenGLManager.drawRectangle(x, y, width, height, 0.5, 0.25f, 1f, 0.25f);
            OpenGLManager.drawRectangle(x, y, width * staminaPercentage, height, 1.0, 0.25f, 1f, 0.25f);
            glEnd();
        } else if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            /** CREATIVE MODE HUD **/
            int previousTilesToShow = 5;
            int currentTile;
            int x;
            int y;

            TileMap.bindTileSetTexture();
            glEnable(GL_TEXTURE_2D);
            OpenGLManager.glBegin(GL_QUADS);
            for (int i = 0; i < 25; i++) {
                currentTile = InputListenerManager.getMouseWheelPosition() + i - previousTilesToShow;
                x = (int) (20f * relativeWidth + i * 128f * relativeWidth);
                y = (int) (Parameters.getResolutionHeight() - 100f * relativeHeight);
                if (currentTile == InputListenerManager.getMouseWheelPosition()) {   // Highlight the tile we have selected
                    TileMap.drawTile(currentTile, x + 5, y + 5, 5 * relativeWidth, 0f, 0f, 0f, true);
                    TileMap.drawTile(currentTile, x, y, 5 * relativeWidth, 1f, 1f, 1f, true);
                } else {
                    TileMap.drawTile(currentTile, x + 5, y + 5, 4 * relativeWidth, 0f, 0f, 0f, true);
                    TileMap.drawTile(currentTile, x, y, 4 * relativeWidth, 0.5f, 0.5f, 0.5f, true);
                }
            }
            glEnd();

            double mouseX = InputListenerManager.getMouseCameraCoordinates().x;
            double mouseY = InputListenerManager.getMouseCameraCoordinates().y;
            if (0 < mouseX && mouseX < Parameters.getResolutionWidth()
                    && 0 < mouseY && mouseY < Parameters.getResolutionHeight()) {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    TileMap.bindTileSetTexture();
                    glEnable(GL_TEXTURE_2D);
                    OpenGLManager.glBegin(GL_QUADS);
                    TileMap.drawTile(InputListenerManager.getMouseWheelPosition(), mouseX + 5, mouseY + 5, 2.5, 0f, 0f, 0f, true);
                    TileMap.drawTile(InputListenerManager.getMouseWheelPosition(), mouseX, mouseY, 2.5, 1f, 1f, 1f, true);
                    glEnd();
                } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                    Coordinates c1 = Coordinates.cameraCoordinatesToTileCoordinates(mouseX, mouseY);
                    Coordinates c2 = Coordinates.tileCoordinatesToWorldCoordinates((int) c1.x, (int) c1.y).toCameraCoordinates();
                    Sprite sprite;
                    switch (InputListenerManager.getMouseWheelPosition() % 7) {
                        case 0:
                            sprite = SpriteManager.getInstance().TREE01;
                            break;
                        case 1:
                            sprite = SpriteManager.getInstance().TREE02;
                            break;
                        case 2:
                            sprite = SpriteManager.getInstance().TREE03;
                            break;
                        case 3:
                            sprite = SpriteManager.getInstance().BUILDING01;
                            break;
                        case 4:
                            sprite = SpriteManager.getInstance().FENCE01;
                            break;
                        case 5:
                            sprite = SpriteManager.getInstance().FENCE02;
                            break;
                        case 6:
                        default:
                            sprite = SpriteManager.getInstance().FENCE03;
                            break;
                    }
                    sprite.draw((int) c2.x, (int) c2.y, 0, 0, 0.5);
                }
            }
        }

        /** YOU DIED **/
        if (Player.getInstance().getStatus() == Player.Status.DEAD) {
            String text = "YOU DIED";
            int scale = 4;
            TextRendering.renderText((Parameters.getResolutionWidth() / 2) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2), 450, text, scale);
        }
    }
}

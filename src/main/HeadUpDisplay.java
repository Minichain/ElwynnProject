package main;

import entities.*;
import scene.TileMap;
import listeners.InputListenerManager;
import text.TextRendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class HeadUpDisplay {
    private static int selectedTile = 0;
    private static int selectedEntity = 0;

    public static void render(long timeElapsed) {
        //Width and Height factors relative to 1920x1080 resolution.

        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            /** NORMAL MODE HUD **/

            /** HEALTH, MANA AND STAMINA **/
            float x = 30f * Parameters.getResolutionFactor();
            float y = Parameters.getResolutionHeight() - 70f * Parameters.getResolutionFactor();
            float width = 500f * Parameters.getResolutionFactor();
            float height = 10f * Parameters.getResolutionFactor();
            float healthPercentage = Player.getInstance().getHealth() / Player.MAX_HEALTH;
            float manaPercentage = Player.getInstance().getMana() / Player.MAX_MANA;
            float staminaPercentage = Player.getInstance().getStamina() / Player.MAX_STAMINA;

            glDisable(GL_TEXTURE_2D);

            OpenGLManager.glBegin(GL_TRIANGLES);
            OpenGLManager.drawRectangle((int) x, (int) y, width, height, 0.5, 1f, 0.25f, 0.25f);
            OpenGLManager.drawRectangle((int) x, (int) y, width * healthPercentage, height, 1.0, 1f, 0.25f, 0.25f);

            y += 20f * Parameters.getResolutionFactor();
            OpenGLManager.drawRectangle((int) x, (int) y, width, height, 0.5, 0.25f, 0.25f, 1f);
            OpenGLManager.drawRectangle((int) x, (int) y, width * manaPercentage, height, 1.0, 0.25f, 0.25f, 1f);

            y += 20f * Parameters.getResolutionFactor();
            OpenGLManager.drawRectangle((int) x, (int) y, width, height, 0.5, 0.25f, 1f, 0.25f);
            OpenGLManager.drawRectangle((int) x, (int) y, width * staminaPercentage, height, 1.0, 0.25f, 1f, 0.25f);
            glEnd();

            glEnable(GL_TEXTURE_2D);

            /** MUSICAL MODE **/
            int numberOfModes = MusicalMode.values().length;
            int spaceBetweenModes = (int) (16f * Parameters.getResolutionFactor());
            float scale = 2f * Parameters.getResolutionFactor();
            width = numberOfModes * 32 * scale + spaceBetweenModes * (numberOfModes - 1);
            x = Parameters.getResolutionWidth() / 2f - width / 2f;
            MusicalMode currentMusicalMode;
            for (int i = 0; i < numberOfModes; i++) {
                currentMusicalMode = MusicalMode.values()[i];
                if (Player.getInstance().getMusicalMode() == currentMusicalMode) {
                    currentMusicalMode.getSprite().draw((int) x, (int) y, 1f, 2.25f * Parameters.getResolutionFactor(), MusicalMode.values()[i].getColor());
                } else {
                    currentMusicalMode.getSprite().draw((int) x, (int) y, 0.5f, 2f * Parameters.getResolutionFactor(), MusicalMode.values()[i].getColor());
                }
                x += 32 * scale + spaceBetweenModes;
            }

            String text;
            float rightPadding;

            /** GOLD COINS **/
            text = "Coins: " + Player.getInstance().getAmountOfGoldCoins();
            rightPadding = (text.length() * TextRendering.CHARACTER_WIDTH + 150f) * Parameters.getResolutionFactor();
            TextRendering.renderText(Window.getWidth() - rightPadding, Window.getHeight() - 50f * Parameters.getResolutionFactor(),
                    text, 2f * Parameters.getResolutionFactor(), false, 1f, 1f, 0.9f, 0f);

            /** HEALTH POTIONS **/
            text = "Health Potions: " + Player.getInstance().getAmountOfHealthPotions();
            rightPadding = (text.length() * TextRendering.CHARACTER_WIDTH + 150f) * Parameters.getResolutionFactor();
            TextRendering.renderText(Window.getWidth() - rightPadding, Window.getHeight() - 75f * Parameters.getResolutionFactor(),
                    text, 2f * Parameters.getResolutionFactor(), false, 1f, 1f, 0f, 0f);

            /** MANA POTIONS **/
            text = "Mana Potions: " + Player.getInstance().getAmountOfManaPotions();
            rightPadding = (text.length() * TextRendering.CHARACTER_WIDTH + 150f) * Parameters.getResolutionFactor();
            TextRendering.renderText(Window.getWidth() - rightPadding, Window.getHeight() - 100f * Parameters.getResolutionFactor(),
                    text, 2f * Parameters.getResolutionFactor(), false, 1f, 0f, 0.1f, 1f);

            /** HASTE POTIONS **/
            text = "Haste Potions: " + Player.getInstance().getAmountOfHastePotions();
            rightPadding = (text.length() * TextRendering.CHARACTER_WIDTH + 150f) * Parameters.getResolutionFactor();
            TextRendering.renderText(Window.getWidth() - rightPadding, Window.getHeight() - 125f * Parameters.getResolutionFactor(),
                    text, 2f * Parameters.getResolutionFactor(), false, 1f, 1f, 0.5f, 0f);

        } else if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            /** CREATIVE MODE HUD **/
            if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                int previousTilesToShow = 5, currentTile;
                float x, y;

                TileMap.bindTileSetTexture();
                glEnable(GL_TEXTURE_2D);
                OpenGLManager.glBegin(GL_QUADS);
                for (int i = 0; i < 25; i++) {
                    currentTile = selectedTile + i - previousTilesToShow;
                    x = 20f * Parameters.getResolutionFactor() + i * 128f * Parameters.getResolutionFactor();
                    y = Parameters.getResolutionHeight() - 100f * Parameters.getResolutionFactor();
                    if (currentTile == selectedTile) {   // Highlight the tile we have selected
                        TileMap.drawTile(currentTile, x + 5, y + 10, 10 * Parameters.getResolutionFactor(), 0f, 0f, 0f, true);
                        TileMap.drawTile(currentTile, x, y, 10 * Parameters.getResolutionFactor(), 1f, 1f, 1f, true);
                    } else {
                        TileMap.drawTile(currentTile, x + 5, y + 5, 8 * Parameters.getResolutionFactor(), 0f, 0f, 0f, true);
                        TileMap.drawTile(currentTile, x, y, 8 * Parameters.getResolutionFactor(), 0.5f, 0.5f, 0.5f, true);
                    }
                }
                glEnd();
            } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                int previousEntitiesToShow = 5, currentEntity;
                float x, y;

                for (int i = 0; i < 25; i++) {
                    currentEntity = selectedEntity + i - previousEntitiesToShow;
                    x = 20f * Parameters.getResolutionFactor() + i * 128f * Parameters.getResolutionFactor();
                    y = Parameters.getResolutionHeight() - 100f * Parameters.getResolutionFactor();
                    if (currentEntity == selectedEntity) {   // Highlight the tile we have selected
                        SpriteManager.getStaticEntitySprite(currentEntity).draw((int) x, (int) y, 0, 0, 0.7f, 2f * Parameters.getResolutionFactor());
                    } else {
                        SpriteManager.getStaticEntitySprite(currentEntity).draw((int) x, (int) y, 0, 0, 0.5f, 1.5f * Parameters.getResolutionFactor());
                    }
                }
            }

            /** MOUSE HUD **/
            double mouseX = InputListenerManager.getMouseCameraCoordinates().x;
            double mouseY = InputListenerManager.getMouseCameraCoordinates().y;
            if (0 < mouseX && mouseX < Parameters.getResolutionWidth()
                    && 0 < mouseY && mouseY < Parameters.getResolutionHeight()) {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    TileMap.bindTileSetTexture();
                    glEnable(GL_TEXTURE_2D);
                    OpenGLManager.glBegin(GL_QUADS);
                    TileMap.drawTile(selectedTile, mouseX + 5, mouseY + 5, 5f * Parameters.getResolutionFactor(), 0f, 0f, 0f, true);
                    TileMap.drawTile(selectedTile, mouseX, mouseY, 5f * Parameters.getResolutionFactor(), 1f, 1f, 1f, true);
                    glEnd();
                    glDisable(GL_TEXTURE_2D);
                } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                    Coordinates c1 = Coordinates.cameraCoordinatesToTileCoordinates(mouseX, mouseY);
                    Coordinates c2 = Coordinates.tileCoordinatesToWorldCoordinates((int) c1.x, (int) c1.y).toCameraCoordinates();
                    SpriteManager.getStaticEntitySprite(selectedEntity).draw((int) c2.x, (int) c2.y, 0, 0, 0.5f);
                }
            }
        }

        /** YOU DIED **/
        if (Player.getInstance().getStatus() == Player.Status.DEAD) {
            String text = "YOU DIED";
            float scale = 4f * Parameters.getResolutionFactor();
            TextRendering.renderText((Parameters.getResolutionWidth() / 2f) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2f), 450, text, scale);
        }
    }

    public static int getSelectedTile() {
        return selectedTile;
    }

    public static void setSelectedTile(int selectedTile) {
        if (selectedTile < 0) {
            selectedTile = (TileMap.getArrayOfTiles().length * TileMap.getArrayOfTiles()[0].length) - 1;
        } else {
            selectedTile %= (TileMap.getArrayOfTiles().length * TileMap.getArrayOfTiles()[0].length);
        }
        HeadUpDisplay.selectedTile = selectedTile;
    }

    public static int getSelectedEntity() {
        return selectedEntity;
    }

    public static void setSelectedEntity(int selectedEntity) {
        if (selectedEntity < 0) {
            selectedEntity = SpriteManager.numOfStaticEntitySprites - 1;
        } else {
            selectedEntity %= SpriteManager.numOfStaticEntitySprites;
        }
        HeadUpDisplay.selectedEntity = selectedEntity;
    }
}

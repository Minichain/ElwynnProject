package main;

import entities.*;
import menu.Menu;
import scene.TileMap;
import listeners.InputListenerManager;
import text.TextRendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class HeadUpDisplay {
    private static int selectedTile = 0;
    private static int selectedEntity = 0;

    public static void render() {
        //Width and Height factors relative to 1920x1080 resolution.

        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            /** NORMAL MODE HUD **/

            /** HEALTH, MANA AND STAMINA **/
            renderHealthManaAndStaminaBars();

            /** MUSICAL MODE **/
            renderMusicalModes();

            float rightPaddingText = 100f * Parameters.getResolutionFactor();
            float rightPaddingSprite = 150f * Parameters.getResolutionFactor();

            /** GOLD COINS **/
            renderItemInfo(SpriteManager.getInstance().GOLD_COIN, Player.getInstance().getAmountOfGoldCoins(), rightPaddingSprite,
                    25f * Parameters.getResolutionFactor(), rightPaddingText, 50f * Parameters.getResolutionFactor());

            /** HEALTH POTIONS **/
            renderItemInfo(SpriteManager.getInstance().HEALTH_POTION, Player.getInstance().getAmountOfHealthPotions(), rightPaddingSprite,
                    75f * Parameters.getResolutionFactor(), rightPaddingText, 100f * Parameters.getResolutionFactor());

            SpriteManager.getInstance().H_KEYBOARD_KEY.draw((int) (Window.getWidth() - rightPaddingSprite - 50f * Parameters.getResolutionFactor()),
                    (int) (Window.getHeight() - 75f * Parameters.getResolutionFactor()),
                    0, 0, 1f, 4f * Parameters.getResolutionFactor());

            /** MANA POTIONS **/
            renderItemInfo(SpriteManager.getInstance().MANA_POTION, Player.getInstance().getAmountOfManaPotions(), rightPaddingSprite,
                    125f * Parameters.getResolutionFactor(), rightPaddingText, 150f * Parameters.getResolutionFactor());

            SpriteManager.getInstance().M_KEYBOARD_KEY.draw((int) (Window.getWidth() - rightPaddingSprite - 50f * Parameters.getResolutionFactor()),
                    (int) (Window.getHeight() - 125f * Parameters.getResolutionFactor()),
                    0, 0, 1f, 4f * Parameters.getResolutionFactor());

            /** HASTE POTIONS **/
            renderItemInfo(SpriteManager.getInstance().HASTE_POTION, Player.getInstance().getAmountOfHastePotions(), rightPaddingSprite,
                    175f * Parameters.getResolutionFactor(), rightPaddingText, 200f * Parameters.getResolutionFactor());

            SpriteManager.getInstance().B_KEYBOARD_KEY.draw((int) (Window.getWidth() - rightPaddingSprite - 50f * Parameters.getResolutionFactor()),
                    (int) (Window.getHeight() - 175f * Parameters.getResolutionFactor()),
                    0, 0, 1f, 4f * Parameters.getResolutionFactor());

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
            } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES
                    || GameMode.getCreativeMode() == GameMode.CreativeMode.WARPS) {
                int previousEntitiesToShow = 5, currentEntity;
                float x = 0, y = 0;

                glDisable(GL_TEXTURE_2D);
                OpenGLManager.glBegin(GL_TRIANGLES);
                OpenGLManager.drawRectangle(0, (int) (Window.getHeight() - (250f * Parameters.getResolutionFactor())),
                        Window.getWidth(), 200f * Parameters.getResolutionFactor(), 0.7, 0);
                glEnd();

                for (int i = 0; i < 25; i++) {
                    currentEntity = selectedEntity + i - previousEntitiesToShow;
                    Sprite sprite;
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                        sprite = SpriteManager.getStaticEntitySprite(currentEntity);
                    } else {
                        sprite = SpriteManager.getWarpSprite(currentEntity);
                    }

                    float width;
                    if (sprite != null && (20f + sprite.SPRITE_WIDTH * 2f) > 125f) {
                        width = (20f + sprite.SPRITE_WIDTH * 2f) * Parameters.getResolutionFactor();
                    } else {
                        width = 125f * Parameters.getResolutionFactor();
                    }

                    y = Parameters.getResolutionHeight() - 100f * Parameters.getResolutionFactor();

                    float transparency;
                    float scale;
                    if (currentEntity == selectedEntity) {   // Highlight the tile we have selected
                        transparency = 1f;
                        scale = 2f;
                    } else {
                        transparency = 0.5f;
                        scale = 1.5f;
                    }
                    if (sprite != null) {
                        sprite.draw((int) x, (int) y, 0, 0, transparency, scale * Parameters.getResolutionFactor());
                    }

                    x += width;
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
                    int[] c1 = Coordinates.cameraCoordinatesToTileCoordinates(mouseX, mouseY);
                    Coordinates c2 = Coordinates.tileCoordinatesToWorldCoordinates(c1[0], c1[1]).toCameraCoordinates();
                    SpriteManager.getStaticEntitySprite(selectedEntity).draw((int) c2.x, (int) c2.y, 0, 0, 0.5f);
                } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.WARPS) {
                    int[] c1 = Coordinates.cameraCoordinatesToTileCoordinates(mouseX, mouseY);
                    Coordinates c2 = Coordinates.tileCoordinatesToWorldCoordinates(c1[0], c1[1]).toCameraCoordinates();
                    SpriteManager.getWarpSprite(selectedEntity).draw((int) c2.x, (int) c2.y, 0, 0, 0.5f);
                }
            }
        }

        /** YOU DIED **/
        if (!Menu.getInstance().isShowing() && Player.getInstance().getStatus() == Player.Status.DEAD) {
            String text = Strings.getString("ui_you_died");
            float scale = 4f * Parameters.getResolutionFactor();
            TextRendering.renderText((Parameters.getResolutionWidth() / 2f) - (TextRendering.CHARACTER_WIDTH * scale * text.length() / 2f), 450, text, scale);
        }
    }

    private static void renderItemInfo(Sprite sprite, int amount, float rightPaddingSprite, float bottomPaddingSprite, float rightPaddingText, float bottomPaddingText) {
        sprite.draw((int) (Window.getWidth() - rightPaddingSprite), (int) (Window.getHeight() - bottomPaddingSprite),
                0, 0, 1f, 4f * Parameters.getResolutionFactor());
        TextRendering.renderText(Window.getWidth() - rightPaddingText, Window.getHeight() - bottomPaddingText,
                "x" + amount, 2f * Parameters.getResolutionFactor(), false, 1f);
    }

    private static void renderHealthManaAndStaminaBars() {
        float x = 30f * Parameters.getResolutionFactor();
        float y = Parameters.getResolutionHeight() - 100f * Parameters.getResolutionFactor();
        float scale = 4f;
        float width = (SpriteManager.getInstance().EMPTY_BAR.SPRITE_WIDTH * Parameters.getResolutionFactor() * scale);
        float height = SpriteManager.getInstance().EMPTY_BAR.SPRITE_HEIGHT * Parameters.getResolutionFactor() * scale;
        float healthPercentage = Player.getInstance().getHealth() / Player.MAX_HEALTH;
        float manaPercentage = Player.getInstance().getMana() / Player.MAX_MANA;
        float staminaPercentage = Player.getInstance().getStamina() / Player.MAX_STAMINA;

        renderBar(SpriteManager.getInstance().HEALTH_BAR, x, y, width, height, healthPercentage, scale);
        y += 40f * Parameters.getResolutionFactor();
        renderBar(SpriteManager.getInstance().MANA_BAR, x, y, width, height, manaPercentage, scale);
        y += 40f * Parameters.getResolutionFactor();
        renderBar(SpriteManager.getInstance().STAMINA_BAR, x, y, width, height, staminaPercentage, scale);
    }

    private static void renderBar(Sprite sprite, float x, float y, float width, float height, float percentage, float scale) {
        SpriteManager.getInstance().EMPTY_BAR.draw((int) (x), (int) (y),
                0, 0, 1f, scale * Parameters.getResolutionFactor());
        sprite.customDraw((int) (x), (int) (y), (int) (percentage * width), (int) (height),
                0, 1, percentage, 0, 1f, scale * Parameters.getResolutionFactor());
    }

    private static void renderMusicalModes() {
        int numberOfModes = MusicalMode.values().length;
        int spaceBetweenModes = (int) (16f * Parameters.getResolutionFactor());
        float modeRoom = 64f * Parameters.getResolutionFactor();
        float scale;
        float width = (modeRoom + spaceBetweenModes) * (numberOfModes - 1);
        float x = Parameters.getResolutionWidth() / 2f - width / 2f;
        float y = Parameters.getResolutionHeight() - 80f * Parameters.getResolutionFactor();
        MusicalMode currentMusicalMode;
        float transparency;
        for (int i = 0; i < numberOfModes; i++) {
            currentMusicalMode = MusicalMode.values()[i];
            if (Player.getInstance().getMusicalMode() == currentMusicalMode) {
                transparency = 1f;
                scale = 4.5f;
            } else {
                transparency = 0.5f;
                scale = 4f;
            }
            currentMusicalMode.getSprite().draw((int) x, (int) y, transparency,
                    scale * Parameters.getResolutionFactor(), MusicalMode.values()[i].getColor(), true);
            x += modeRoom + spaceBetweenModes;
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

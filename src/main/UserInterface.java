package main;

import entities.InteractionEntity;
import entities.NonPlayerCharacter;
import entities.Player;
import enums.NonPlayerCharacterAction;
import inventory.Inventory;
import items.Item;
import listeners.ActionManager;
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
    private static boolean HUDVisibility = true;
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
        /** UPDATE DEBUG TEXT **/
        if (Parameters.isDebugMode()) {
            debugTextList.clear();

            debugTextList.add("Project Version: " + Parameters.getProjectVersion());
            debugTextList.add("OpenGL version " + glGetString(GL_VERSION));
            debugTextList.add("Show/Hide Debug Info: " + ActionManager.Action.DEBUG_MENU.getActionKeyString());
            debugTextList.add("Reset Game: " + ActionManager.Action.RESET.getActionKeyString());
            debugTextList.add("Save World: " + ActionManager.Action.SAVE_WORLD.getActionKeyString());
            debugTextList.add("FPS: " + String.format("%.2f", FramesPerSecond.getFramesPerSecond()));
            debugTextList.add("Update time: " + String.format("%.2f", FramesPerSecond.getUpdatingTime()) + " nanoseconds");
            debugTextList.add("Render time: " + String.format("%.2f", FramesPerSecond.getRenderingTime()) + " nanoseconds");
            debugTextList.add("GPU calls: " + OpenGLManager.GPU_CALLS);
            debugTextList.add("Resolution: " + Parameters.getResolutionWidth() + " x " + Parameters.getResolutionHeight());
            debugTextList.add("Window Size: " + Window.getWidth() + " x " + Window.getHeight());
            debugTextList.add("Num of Graphic Entities: " + Scene.getInstance().getListOfGraphicEntities().size());
            debugTextList.add("Num of Tiles: " + TileMap.getArrayOfTiles().length + " x " + TileMap.getArrayOfTiles()[0].length + " x " + Tile.getNumOfLayers());
            debugTextList.add("Camera World Coordinates: (" + (float) Camera.getInstance().getCoordinates().x + ", " + (float) Camera.getInstance().getCoordinates().y + ")");
            debugTextList.add("Camera Zoom: " + (float) Camera.getZoom());
            debugTextList.add("Player World Coordinates: (" + (float) Player.getInstance().getWorldCoordinates().x + ", " + (float) Player.getInstance().getWorldCoordinates().y + ")");
            debugTextList.add("Player Camera Coordinates: (" + (float) Player.getInstance().getCameraCoordinates().x + ", " + (float) Player.getInstance().getCameraCoordinates().y + ")");
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

        /** MUSICAL MODE SELECTOR **/
        if (!Menu.getInstance().isShowing() && MusicalModeSelector.getInstance().isShowing()) {
            MusicalModeSelector.getInstance().update(timeElapsed);
        }

        /** INVENTORY **/
        if (!Menu.getInstance().isShowing() && Player.getInstance().getInventory().isOpened()) {
            Player.getInstance().getInventory().update(timeElapsed);
        }
    }

    public void render(long timeElapsed) {
        OpenGLManager.releaseCurrentShader();

        /** DEBUG INFO **/
        if (Parameters.isDebugMode()) {
            renderDebugUI();
        }

        /** FLOATING TEXT **/
        FloatingText.updateAndRender(timeElapsed);

        /** HUD **/
        if (HUDVisibility) {
            HeadUpDisplay.render();
        }

        /** MENU **/
        if (Menu.getInstance().isShowing()) {
            Menu.getInstance().render();
        }

        /** NPC interactions **/
        if (HUDVisibility && !Menu.getInstance().isShowing() && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            renderNPCInteractions();
        }

        /** MUSICAL MODE SELECTOR **/
        if (HUDVisibility && !Menu.getInstance().isShowing() && MusicalModeSelector.getInstance().isShowing()) {
            MusicalModeSelector.getInstance().render();
        }

        /** INVENTORY **/
        if (HUDVisibility && !Menu.getInstance().isShowing() && Player.getInstance().getInventory().isOpened()) {
            Player.getInstance().getInventory().render();
        }
    }

    private void renderNPCInteractions() {
        for (NonPlayerCharacter nonPlayerCharacter : Scene.getInstance().getListOfNonPlayerCharacters()) {
            InteractionEntity interactionEntity = nonPlayerCharacter.getInteractionEntity();
            if (interactionEntity != null) {
                interactionEntity.drawSprite((int) interactionEntity.getCameraCoordinates().x, (int) interactionEntity.getCameraCoordinates().y);

                if (nonPlayerCharacter.isInteracting()) {
                    if (nonPlayerCharacter.isTalking()) {
                        int x = (int) interactionEntity.getCameraCoordinates().x;
                        int y = (int) (interactionEntity.getCameraCoordinates().y + 25 * Parameters.getResolutionFactor());
                        float scale = 2f * Parameters.getResolutionFactor();
                        TextRendering.renderText(x, y, nonPlayerCharacter.getTalkText().get(nonPlayerCharacter.getTalkTextPage()), scale);

                        if (nonPlayerCharacter.getTalkText().size() > 1) {
                            y = (int) (interactionEntity.getCameraCoordinates().y + 50 * Parameters.getResolutionFactor());
                            TextRendering.renderText(x, y, (nonPlayerCharacter.getTalkTextPage() + 1) + "/" + nonPlayerCharacter.getTalkText().size(), scale);
                        }
                    } else {
                        ArrayList<NonPlayerCharacterAction> listOfActions = null;
                        ArrayList<Item> listOfItems = null;
                        int size = 0;

                        if (nonPlayerCharacter.isWaitingForInteractionSelection()) {
                            listOfActions = nonPlayerCharacter.getAvailableActions();
                            size = listOfActions.size();
                        } else if (nonPlayerCharacter.isSelling()) {
                            listOfItems = nonPlayerCharacter.getListOfItems();
                            size = listOfItems.size() + 1;
                        } else if (nonPlayerCharacter.isBuying()) {
                            listOfItems = Player.getInstance().getListOfItems();
                            size = listOfItems.size() + 1;
                        }

                        glDisable(GL_TEXTURE_2D);
                        OpenGLManager.glBegin(GL_TRIANGLES);
                        int x = (int) nonPlayerCharacter.getCameraCoordinates().x - (int) (325f * Parameters.getResolutionFactor());
                        int y = (int) nonPlayerCharacter.getCameraCoordinates().y - (int) (25f * size * Parameters.getResolutionFactor());
                        OpenGLManager.drawRectangle(x, y, 300, size * 25 + 50, 0.8, 0.2f);
                        glEnd();

                        x = (int) nonPlayerCharacter.getCameraCoordinates().x - (int) (300f * Parameters.getResolutionFactor());
                        for (int i = 0; i < size; i++) {
                            String text;
                            if (nonPlayerCharacter.isWaitingForInteractionSelection()) {
                                text = listOfActions.get(i).toString();
                            } else {
                                if (i == size - 1) text = Strings.getString("ui_quit_npc");
                                else text = listOfItems.get(i).getName().concat(" ").concat(String.valueOf(listOfItems.get(i).getCost())).concat("gc");
                            }
                            if (i == nonPlayerCharacter.getSelectedItem()) {
                                text += " <-";
                            }
                            y = (int) nonPlayerCharacter.getCameraCoordinates().y - (int) (((25f * size - 25f) - i * 25f) * Parameters.getResolutionFactor());
                            TextRendering.renderText(x, y, text, 2f * Parameters.getResolutionFactor());
                        }
                    }
                }
            }
        }
    }

    private void renderDebugUI() {
//        Log.l("Render Debug UI");

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

    public static boolean getHUDVisibility() {
        return HUDVisibility;
    }

    public static void setHUDVisibility(boolean showInterface) {
        Log.l("Set HUD visibility to " + showInterface);
        UserInterface.HUDVisibility = showInterface;
    }
}

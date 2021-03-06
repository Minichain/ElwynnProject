package listeners;

import board.FretBoard;
import console.Console;
import entities.*;
import main.*;
import menu.Menu;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import ui.HeadUpDisplay;
import ui.UserInterface;

import static listeners.MyGLFW.myGlfwGetKeyName;
import static org.lwjgl.glfw.GLFW.*;

public class ActionManager {
    public enum Action {
        MOVE_UP (0),
        MOVE_LEFT (1),
        MOVE_DOWN (2),
        MOVE_RIGHT (3),
        ROLL (4),
        INTERACT (5),
        ATTACK_01 (6),
        ATTACK_02 (7),
        PREVIOUS(8),
        NEXT(9),
        DEBUG_MENU (10),
        HUD_VISIBILITY (11),
        MENU (12),
        SAVE_WORLD (13),
        INCREASE_CAMERA_ZOOM (14),
        DECREASE_CAMERA_ZOOM (15),
        RESET (16),
        CREATIVE_TILES_MODE (17),
        CREATIVE_STATIC_ENTITIES_MODE (18),
        USE_HEALTH_POTION (19),
        USE_MANA_POTION (20),
        USE_HASTE_POTION (21),
        SHOW_MUSICAL_MODE_SELECTOR (22),
        OPEN_INVENTORY (23),
        DEBUG_KEY (24),
        CREATIVE_WARPS_MODE (25),
        OPEN_CLOSE_CONSOLE (26),
        FRET_BOARD_BUTTON_01 (27),
        FRET_BOARD_BUTTON_02 (28),
        FRET_BOARD_BUTTON_03 (29),
        FRET_BOARD_BUTTON_04 (30),
        ;

        int actionValue;

        Action(int actionValue) {
            this.actionValue = actionValue;
        }

        public int getActionValue() {
            return actionValue;
        }

        public int[] getActionKey() {
            int[] key = new int[]{-1, -1};
            switch (this) {
                case MOVE_UP:
                    key[0] = GLFW_KEY_W;
                    break;
                case MOVE_LEFT:
                    key[0] = GLFW_KEY_A;
                    break;
                case MOVE_DOWN:
                    key[0] = GLFW_KEY_S;
                    break;
                case MOVE_RIGHT:
                    key[0] = GLFW_KEY_D;
                    break;
                case ROLL:
                    key[0] = GLFW_KEY_SPACE;
                    break;
                case PREVIOUS:
                    key[0] = GLFW_KEY_Q;
                    break;
                case NEXT:
                    key[0] = GLFW_KEY_E;
                    break;
                case ATTACK_01:
                    key[0] = GLFW_MOUSE_BUTTON_1;
                    break;
                case ATTACK_02:
                    key[0] = GLFW_MOUSE_BUTTON_2;
                    break;
                case INTERACT:
                    key[0] = GLFW_KEY_F;
                    break;
                case HUD_VISIBILITY:
                    key[0] = GLFW_KEY_LEFT_ALT;
                    key[1] = GLFW_KEY_Z;
                    break;
                case SAVE_WORLD:
                    key[0] = GLFW_KEY_LEFT_CONTROL;
                    key[1] = GLFW_KEY_S;
                    break;
                case MENU:
                    key[0] = GLFW_KEY_ESCAPE;
                    break;
                case DEBUG_MENU:
                    key[0] = GLFW_KEY_F1;
                    break;
                case INCREASE_CAMERA_ZOOM:
                    key[0] = GLFW_KEY_UP;
                    break;
                case DECREASE_CAMERA_ZOOM:
                    key[0] = GLFW_KEY_DOWN;
                    break;
                case RESET:
                    key[0] = GLFW_KEY_F5;
                    break;
                case CREATIVE_TILES_MODE:
                case FRET_BOARD_BUTTON_01:
                    key[0] = GLFW_KEY_1;
                    break;
                case CREATIVE_STATIC_ENTITIES_MODE:
                case FRET_BOARD_BUTTON_02:
                    key[0] = GLFW_KEY_2;
                    break;
                case CREATIVE_WARPS_MODE:
                case FRET_BOARD_BUTTON_03:
                    key[0] = GLFW_KEY_3;
                    break;
                case FRET_BOARD_BUTTON_04:
                    key[0] = GLFW_KEY_4;
                    break;
                case USE_HEALTH_POTION:
                    key[0] = GLFW_KEY_H;
                    break;
                case USE_MANA_POTION:
                    key[0] = GLFW_KEY_M;
                    break;
                case USE_HASTE_POTION:
                    key[0] = GLFW_KEY_B;
                    break;
                case SHOW_MUSICAL_MODE_SELECTOR:
                    key[0] = GLFW_KEY_TAB;
                    break;
                case OPEN_INVENTORY:
                    key[0] = GLFW_KEY_I;
                    break;
                case DEBUG_KEY:
                    key[0] = GLFW_KEY_F12;
                    break;
                case OPEN_CLOSE_CONSOLE:
                    key[0] = GLFW_KEY_ENTER;
                    break;
                default:
                    break;
            }
            return key;
        }

        public String getActionKeyString() {
            int[] actionKey = this.getActionKey();
            String actionKeyString = "";
            if (actionKey[0] > 0) {
                actionKeyString = myGlfwGetKeyName(actionKey[0]);
            }
            if (actionKey[1] > 0) {
                actionKeyString = actionKeyString + " + " + myGlfwGetKeyName(actionKey[1]);
            }
            return actionKeyString;
        }
    }

    public static boolean MOVING_UP;
    public static boolean MOVING_LEFT;
    public static boolean MOVING_DOWN;
    public static boolean MOVING_RIGHT;

    public static void processKeyPressed(int key, boolean pressed) {
        processKeyPressed(new int[]{key, -1}, pressed);
    }

    public static void processKeyPressed(int[] key, boolean pressed) {
//        Log.l("Process key pressed " + key[0] + ", " + key[1] + ". Pressed: " + pressed);
        if (isSameKeyCombination(key, Action.OPEN_CLOSE_CONSOLE.getActionKey())) {
            if (!pressed) Console.getInstance().setTyping(!Console.getInstance().isTyping());
        } else if (Console.getInstance().isTyping() && !isMouseInput(key)) {
            if (pressed) Console.getInstance().processInputKey(key);
            return;
        }

        if (isSameKeyCombination(key, Action.MOVE_UP.getActionKey())) {
            MOVING_UP = pressed;
        } else if (isSameKeyCombination(key, Action.MOVE_LEFT.getActionKey())) {
            MOVING_LEFT = pressed;
        } else if (isSameKeyCombination(key, Action.MOVE_DOWN.getActionKey())) {
            MOVING_DOWN = pressed;
        } else if (isSameKeyCombination(key, Action.MOVE_RIGHT.getActionKey())) {
            MOVING_RIGHT = pressed;
        } else if (isSameKeyCombination(key, Action.SAVE_WORLD.getActionKey())) {
            if (!pressed) {
                WorldLoader.getInstance().saveWorld();
            }
        } else if (isSameKeyCombination(key, Action.ROLL.getActionKey())) {
            if (!pressed && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().roll();
            }
        } else if (isSameKeyCombination(key, Action.PREVIOUS.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                //Nothing
            } else {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    if (pressed) HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() - 1);
                } else {
                    if (pressed) HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() - 1);
                }
            }
        } else if (isSameKeyCombination(key, Action.NEXT.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                //Nothing
            } else {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    if (pressed) HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() + 1);
                } else {
                    if (pressed) HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() + 1);
                }
            }
        } else if (isSameKeyCombination(key, Action.INTERACT.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (!pressed) Player.getInstance().interactWithNPC();
            }
        } else if (isSameKeyCombination(key, Action.HUD_VISIBILITY.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (pressed) UserInterface.setHUDVisibility(!UserInterface.getHUDVisibility());
            }
        } else if (isSameKeyCombination(key, Action.MENU.getActionKey())) {
            if (!pressed) Menu.getInstance().setShowing(!Menu.getInstance().isShowing());
        } else if (isSameKeyCombination(key, Action.DEBUG_MENU.getActionKey())) {
            if (!pressed) Parameters.setDebugMode(!Parameters.isDebugMode());
        } else if (isSameKeyCombination(key, Action.INCREASE_CAMERA_ZOOM.getActionKey())) {
            if (pressed) {
                NonPlayerCharacter nonPlayerCharacter = Player.getInstance().getInteractiveNPC();
                if (nonPlayerCharacter != null) {
                    nonPlayerCharacter.setSelectedItem(nonPlayerCharacter.getSelectedItem() - 1);
                } else {
                    Camera.increaseZoom();
                }
            }
        } else if (isSameKeyCombination(key, Action.DECREASE_CAMERA_ZOOM.getActionKey())) {
            if (pressed) {
                NonPlayerCharacter nonPlayerCharacter = Player.getInstance().getInteractiveNPC();
                if (nonPlayerCharacter != null) {
                    nonPlayerCharacter.setSelectedItem(nonPlayerCharacter.getSelectedItem() + 1);
                } else {
                    Camera.decreaseZoom();
                }
            }
        } else if (isSameKeyCombination(key, Action.RESET.getActionKey())) {
            if (!pressed) Scene.getInstance().reset();
        } else if (isSameKeyCombination(key, Action.CREATIVE_TILES_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.TILES);
        } else if (isSameKeyCombination(key, Action.FRET_BOARD_BUTTON_01.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            FretBoard.getInstance().setFretPressed(0, pressed);
        } else if (isSameKeyCombination(key, Action.FRET_BOARD_BUTTON_02.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            FretBoard.getInstance().setFretPressed(1, pressed);
        } else if (isSameKeyCombination(key, Action.FRET_BOARD_BUTTON_03.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            FretBoard.getInstance().setFretPressed(2, pressed);
        } else if (isSameKeyCombination(key, Action.FRET_BOARD_BUTTON_04.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            FretBoard.getInstance().setFretPressed(3, pressed);
        } else if (isSameKeyCombination(key, Action.CREATIVE_STATIC_ENTITIES_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.STATIC_ENTITIES);
        } else if (isSameKeyCombination(key, Action.CREATIVE_WARPS_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.WARPS);
        } else if (isSameKeyCombination(key, Action.ATTACK_01.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().setPlayingMusic(pressed);
            }

            if (!pressed && !Menu.getInstance().isShowing()) {
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {

                } else {
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                        putTile();
                    } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                        putStaticEntity();
                    } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.WARPS) {
                        putWarp();
                    }
                }
            }
        } else if (isSameKeyCombination(key, Action.ATTACK_02.getActionKey())) {
            if (pressed && !Menu.getInstance().isShowing() && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().playNote();
            }

            if (!pressed && !Menu.getInstance().isShowing()) {
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {

                } else {
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                        // Change Tile's collision behaviour
                        Coordinates mouseCameraCoordinates = InputListenerManager.getMouseCameraCoordinates();
                        int[] tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
                        if (0 <= tileCoordinates[0] && tileCoordinates[0] < TileMap.getNumOfHorizontalTiles()
                                && 0 <= tileCoordinates[1] && tileCoordinates[1] < TileMap.getNumOfVerticalTiles()) {
                            TileMap.getArrayOfTiles()[tileCoordinates[0]][tileCoordinates[1]].changeCollisionBehaviour();
                        }
                    } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES
                            || GameMode.getCreativeMode() == GameMode.CreativeMode.WARPS) {
                        for (int i = 0; i < Scene.getInstance().getListOfStaticEntities().size(); i++) {
                            GraphicEntity graphicEntity = Scene.getInstance().getListOfStaticEntities().get(i);
                            if (graphicEntity.isOverEntity(InputListenerManager.getMouseWorldCoordinates())) {
                                /** DELETE ENTITY **/
                                Log.l("Deleting graphicEntity " + graphicEntity.getEntityCode());
                                graphicEntity.setDead(true);
                                Scene.getInstance().getListOfStaticEntities().remove(graphicEntity);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (isSameKeyCombination(key, Action.USE_HEALTH_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useHealthPotion();
        } else if (isSameKeyCombination(key, Action.USE_MANA_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useManaPotion();
        } else if (isSameKeyCombination(key, Action.USE_HASTE_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useHastePotion();
        } else if (isSameKeyCombination(key, Action.SHOW_MUSICAL_MODE_SELECTOR.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().setChoosingMusicalMode(pressed);
            }
        } else if (isSameKeyCombination(key, Action.OPEN_INVENTORY.getActionKey())) {
            if (!pressed && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().getInventory().setOpened(!Player.getInstance().getInventory().isOpened());
            }
        } else if (isSameKeyCombination(key, Action.DEBUG_KEY.getActionKey())) {
            /** DEBUG KEY **/
            if (!pressed) {
//                Player.getInstance().hurt(10000f);
//                Camera.getInstance().shake(100, 1f);
                Log.l("Debug key pressed");
            }
        }
    }

    private static boolean isSameKeyCombination(int[] input1, int[] input2) {
        if (input1[0] == input2[0]) {
            return input2[1] == -1 || input1[1] == input2[1];
        }
        return false;
    }

    /**
     * Mouse inputs codes are [0-4], so if key code is >= 5 is not a mouse input for sure.
     **/
    private static boolean isMouseInput(int[] key) {
        return key[0] < 5 && key[1] < 5;
    }

    private static void putTile() {
        Coordinates mouseCameraCoordinates = InputListenerManager.getMouseCameraCoordinates();
        int[] tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
        int layer;
        switch (GameMode.getLayerEditing()) {
            case FIRST_LAYER:
            default:
                layer = 0;
                break;
            case SECOND_LAYER:
                layer = 1;
                break;
            case THIRD_LAYER:
                layer = 2;
                break;
        }
        TileMap.setTile(tileCoordinates[0], tileCoordinates[1], layer, (byte) (HeadUpDisplay.getSelectedTile()));
    }

    private static void putStaticEntity() {
        Coordinates mouseWorldCoordinates = InputListenerManager.getMouseWorldCoordinates();
        int entity = HeadUpDisplay.getSelectedEntity() % SpriteManager.numOfStaticEntitySprites;
        Log.l("Adding a new Static Entity ");
        switch (entity) {
            case 0:
                new Tree((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 0);
                break;
            case 1:
                new Tree((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 1);
                break;
            case 2:
                new Tree((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 2);
                break;
            case 3:
                new Tree((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 3);
                break;
            case 4:
                new Building((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 0);
                break;
            case 5:
                new Building((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 1);
                break;
            case 6:
                new Fence((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 0);
                break;
            case 7:
                new Fence((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 1);
                break;
            case 8:
                new Fence((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 2);
                break;
            case 9:
                new Fence((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, 3);
                break;
            case 10:
                new Light((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 11:
                new Torch((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 12:
                new UtilityPole((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            default:
                break;
        }
    }

    private static void putWarp() {
        Coordinates mouseWorldCoordinates = InputListenerManager.getMouseWorldCoordinates();
        int entity = HeadUpDisplay.getSelectedEntity() % SpriteManager.numOfWarpSprites;
        Log.l("Adding a new Warp ");
        switch (entity) {
            case 0:
            case 1:
            case 2:
            case 3:
                new LargeWarp((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, entity, "SceneName", new Coordinates(0, 0));
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                new SmallWarp((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y, entity - 4, "SceneName", new Coordinates(0, 0));
                break;
            default:
                break;
        }
    }
}

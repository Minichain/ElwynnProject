package listeners;

import entities.*;
import main.*;
import menu.Menu;
import scene.Camera;
import scene.Scene;
import scene.TileMap;

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
        PREVIOUS_MUSICAL_MODE (8),
        NEXT_MUSICAL_MODE (9),
        DEBUG_MENU (10),
        HUD_VISIBILITY (11),
        MENU (12),
        SAVE_WORLD (13),
        INCREASE_CAMERA_ZOOM (14),
        DECREASE_CAMERA_ZOOM (15),
        RESET (16),
        CREATIVE_TILES_MODE (17),
        CREATIVE_STATIC_ENTITIES_MODE (18),
        IONIAN_MODE (19),
        DORIAN_MODE (20),
        PHRYGIAN_MODE (21),
        LYDIAN_MODE (22),
        MIXOLYDIAN_MODE (23),
        AEOLIAN_MODE (24),
        LOCRIAN_MODE (25),
        CHOOSE_NPC_ACTION (26),
        USE_HEALTH_POTION (27),
        USE_MANA_POTION (28),
        USE_HASTE_POTION (29)
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
                case PREVIOUS_MUSICAL_MODE:
                    key[0] = GLFW_KEY_Q;
                    break;
                case NEXT_MUSICAL_MODE:
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
                case IONIAN_MODE:
                case CREATIVE_TILES_MODE:
                    key[0] = GLFW_KEY_1;
                    break;
                case DORIAN_MODE:
                case CREATIVE_STATIC_ENTITIES_MODE:
                    key[0] = GLFW_KEY_2;
                    break;
                case PHRYGIAN_MODE:
                    key[0] = GLFW_KEY_3;
                    break;
                case LYDIAN_MODE:
                    key[0] = GLFW_KEY_4;
                    break;
                case MIXOLYDIAN_MODE:
                    key[0] = GLFW_KEY_5;
                    break;
                case AEOLIAN_MODE:
                    key[0] = GLFW_KEY_6;
                    break;
                case LOCRIAN_MODE:
                    key[0] = GLFW_KEY_7;
                    break;
                case CHOOSE_NPC_ACTION:
                    key[0] = GLFW_KEY_ENTER;
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
                WorldLoader.saveWorld();
            }
        } else if (isSameKeyCombination(key, Action.ROLL.getActionKey())) {
            if (pressed && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                Player.getInstance().roll();
            }
        } else if (isSameKeyCombination(key, Action.PREVIOUS_MUSICAL_MODE.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (pressed) Player.getInstance().setMusicalMode(Player.getInstance().getMusicalMode().value - 1);
            } else {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    if (pressed) HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() - 1);
                } else {
                    if (pressed) HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() - 1);
                }
            }
        } else if (isSameKeyCombination(key, Action.NEXT_MUSICAL_MODE.getActionKey())) {
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (pressed) Player.getInstance().setMusicalMode(Player.getInstance().getMusicalMode().value + 1);
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
        } else if (isSameKeyCombination(key, Action.CREATIVE_STATIC_ENTITIES_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.STATIC_ENTITIES);
        } else if (isSameKeyCombination(key, Action.IONIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.IONIAN);
        } else if (isSameKeyCombination(key, Action.DORIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.DORIAN);
        } else if (isSameKeyCombination(key, Action.PHRYGIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.PHRYGIAN);
        } else if (isSameKeyCombination(key, Action.LYDIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.LYDIAN);
        } else if (isSameKeyCombination(key, Action.MIXOLYDIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.MIXOLYDIAN);
        } else if (isSameKeyCombination(key, Action.AEOLIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.AEOLIAN);
        } else if (isSameKeyCombination(key, Action.LOCRIAN_MODE.getActionKey()) && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (!pressed) Player.getInstance().setMusicalMode(MusicalMode.LOCRIAN);
        } else if (isSameKeyCombination(key, Action.ATTACK_01.getActionKey())) {
            if (!pressed && !Menu.getInstance().isShowing() && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    putTile();
                } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                    putStaticEntity();
                }
            }
        } else if (isSameKeyCombination(key, Action.ATTACK_02.getActionKey())) {
            if (!pressed && !Menu.getInstance().isShowing() ) {
                if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                        // Change Tile's collision behaviour
                        Coordinates mouseCameraCoordinates = InputListenerManager.getMouseCameraCoordinates();
                        Coordinates tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
                        TileMap.getArrayOfTiles()[(int) tileCoordinates.x][(int) tileCoordinates.y].changeCollisionBehaviour();
                    } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                        for (int i = 0; i < Scene.getInstance().getListOfStaticEntities().size(); i++) {
                            GraphicEntity graphicEntity = Scene.getInstance().getListOfStaticEntities().get(i);
                            if (graphicEntity.isOverEntity(InputListenerManager.getMouseWorldCoordinates())) {
                                /** DELETE ENTITY **/
                                Log.l("Deleting entity!");
                                for (LightSource lightSource : graphicEntity.getLightSources()) {
                                    Scene.getInstance().getListOfLightSources().remove(lightSource);
                                }
                                Scene.getInstance().getListOfStaticEntities().remove(graphicEntity);
                                Scene.getInstance().getListOfEntities().remove(graphicEntity);
                                break;
                            }
                        }
                    }
                }
            }
        } else if (isSameKeyCombination(key, Action.CHOOSE_NPC_ACTION.getActionKey())) {

        } else if (isSameKeyCombination(key, Action.USE_HEALTH_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useHealthPotion();
        } else if (isSameKeyCombination(key, Action.USE_MANA_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useManaPotion();
        } else if (isSameKeyCombination(key, Action.USE_HASTE_POTION.getActionKey())) {
            if (!pressed) Player.getInstance().useHastePotion();
        }
    }

    private static boolean isSameKeyCombination(int[] input1, int[] input2) {
        if (input1[0] == input2[0]) {
            return input2[1] == -1 || input1[1] == input2[1];
        }
        return false;
    }

    private static void putTile() {
        Coordinates mouseCameraCoordinates = InputListenerManager.getMouseCameraCoordinates();
        Coordinates tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
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
        TileMap.setTile((int) tileCoordinates.x, (int) tileCoordinates.y, layer, (byte) (HeadUpDisplay.getSelectedTile()));
    }

    private static void putStaticEntity() {
        Coordinates mouseWorldCoordinates = InputListenerManager.getMouseWorldCoordinates();
        int entity = HeadUpDisplay.getSelectedEntity() % SpriteManager.numOfStaticEntitySprites;
        Log.l("Adding a new Static Entity " + entity);
        switch (entity) {
            case 0:
                new Tree01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 1:
                new Tree02((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 2:
                new Tree03((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 3:
                new Tree04((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 4:
                new Building01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 5:
                new Building02((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 6:
                new Fence01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 7:
                new Fence02((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 8:
                new Fence03((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 9:
                new Fence04((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 10:
                new Light01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            case 11:
                new Torch01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                break;
            default:
                break;
        }
    }
}

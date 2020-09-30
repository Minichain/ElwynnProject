package listeners;

import entities.*;
import menu.Scroll;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import main.*;
import menu.Menu;
import org.lwjgl.glfw.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class InputListenerManager {
    /** CALL BACKS **/
    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseCallback;
    private static GLFWCursorPosCallback mousePosCallback;
    private static GLFWScrollCallback scrollCallback;
    private static GLFWCursorEnterCallback enterCallback;

    /** MOUSE **/
    private static boolean mouseInside;
    private static Coordinates mouseWindowCoordinates = new Coordinates(0, 0);
    private static Coordinates mouseCameraCoordinates = new Coordinates(0, 0);
    private static Coordinates mouseWorldCoordinates = new Coordinates(0, 0);
    private static int mouseWheelPosition;
    public static boolean leftMouseButtonPressed;
    public static boolean rightMouseButtonPressed;

    /** KEYBOARD **/
    private static boolean W_KEY_PRESSED;
    private static boolean A_KEY_PRESSED;
    private static boolean S_KEY_PRESSED;
    private static boolean D_KEY_PRESSED;
    private static boolean LEFT_CTRL_KEY_PRESSED;
    private static boolean LEFT_SHIFT_KEY_PRESSED;

    private static boolean usingKeyboardAndMouse = false;

    /**
     * MOUSE AND KEYBOARD INPUT HANDLING
     */
    public static void initMyInputListener() {
        long window = Window.getWindow();

        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                setKeyPressed(key, action != GLFW_RELEASE);
            }
        };

        mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == GLFW_MOUSE_BUTTON_1) {
                    if (action == GLFW_PRESS) {
                        processLeftMouseButtonPressed();
                    } else if (action == GLFW_RELEASE) {
                        processLeftMouseButtonReleased();
                    }
                } else if (button == GLFW_MOUSE_BUTTON_2) {
                    if (action == GLFW_PRESS) {
                        processRightMouseButtonPressed();
                    } else if (action == GLFW_RELEASE) {
                        processRightMouseButtonReleased();
                    }
                }
            }
        };

        mousePosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                // Transform from window coordinates to camera coordinates
                mouseWindowCoordinates = new Coordinates(x, y);
                mouseCameraCoordinates = Coordinates.windowToCameraCoordinates(x, y);
            }
        };

        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                if (Menu.getInstance().isShowing()) {
                    Scroll scroll = Menu.getInstance().getMenuScrollBar().getScroll();
                    if (yOffset > 0.0) {
                        scroll.y -= 15;
                    } else {
                        scroll.y += 15;
                    }
                    scroll.update(scroll.x, -1, scroll.width, scroll.height);
                } else {
                    if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                        if (yOffset > 0.0) {
                            if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                                HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() - 1);
                            } else {
                                HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() - 1);
                            }
                        } else {
                            if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                                HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() + 1);
                            } else {
                                HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() + 1);
                            }
                        }
                    } else {
                        if (yOffset > 0.0) {
                            Camera.increaseZoom();
                        } else {
                            Camera.decreaseZoom();
                        }
                    }
                }
                mouseWheelPosition += yOffset;
                if (mouseWheelPosition < 0) mouseWheelPosition = 0;
            }
        };

        enterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                mouseInside = entered;
            }
        };

        glfwSetKeyCallback(window, keyCallback);
        glfwSetMouseButtonCallback(window, mouseCallback);
        glfwSetCursorPosCallback(window, mousePosCallback);
        glfwSetScrollCallback(window, scrollCallback);
        glfwSetCursorEnterCallback(window, enterCallback);
    }

    private static void processLeftMouseButtonPressed() {
        leftMouseButtonPressed = true;
        usingKeyboardAndMouse = true;
        if (!Menu.getInstance().isShowing() && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
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
            } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                switch (HeadUpDisplay.getSelectedEntity() % SpriteManager.numOfStaticEntitySprites) {
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
                        new Building01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 4:
                        new Building02((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 5:
                        new Fence01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 6:
                        new Fence02((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 7:
                        new Fence03((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 8:
                        new Fence04((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                    case 9:
                    default:
                        new Light01((int) mouseWorldCoordinates.x, (int) mouseWorldCoordinates.y);
                        break;
                }
            }
        }
    }

    private static void processLeftMouseButtonReleased() {
        leftMouseButtonPressed = false;
    }

    private static void processRightMouseButtonPressed() {
        rightMouseButtonPressed = true;
        usingKeyboardAndMouse = true;
        if (!Menu.getInstance().isShowing() ) {
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                    // Change Tile's collision behaviour
                    Coordinates tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
                    TileMap.getArrayOfTiles()[(int) tileCoordinates.x][(int) tileCoordinates.y].changeCollisionBehaviour();
                } else if (GameMode.getCreativeMode() == GameMode.CreativeMode.STATIC_ENTITIES) {
                    for (int i = 0; i < Scene.getInstance().getListOfStaticEntities().size(); i++) {
                        GraphicEntity graphicEntity = Scene.getInstance().getListOfStaticEntities().get(i);
                        if (graphicEntity.isOverEntity(getMouseWorldCoordinates())) {
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
    }

    private static void processRightMouseButtonReleased() {
        rightMouseButtonPressed = false;
    }

    public static boolean isKeyPressed(int key) {
        switch (key) {
            case GLFW_KEY_W:
                return W_KEY_PRESSED;
            case GLFW_KEY_A:
                return A_KEY_PRESSED;
            case GLFW_KEY_S:
                return S_KEY_PRESSED;
            case GLFW_KEY_D:
                return D_KEY_PRESSED;
        }
        return false;
    }

    private static void setKeyPressed(int key, boolean pressed) {
//        Log.l("setKeyPressed key: " + key + ", pressed: " + pressed);
        usingKeyboardAndMouse = true;
        switch(key) {
            case GLFW_KEY_W:
                W_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_A:
                A_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_S:
                if (LEFT_CTRL_KEY_PRESSED) {
                    if (!pressed) WorldLoader.saveWorld();
                } else {
                    S_KEY_PRESSED = pressed;
                }
                break;
            case GLFW_KEY_D:
                D_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_Q:
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                    if (pressed) Player.getInstance().setMusicalMode(Player.getInstance().getMusicalMode().value - 1);
                } else {
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                        if (pressed) HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() - 1);
                    } else {
                        if (pressed) HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() - 1);
                    }
                }
                break;
            case GLFW_KEY_E:
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                    if (pressed) Player.getInstance().setMusicalMode(Player.getInstance().getMusicalMode().value + 1);
                } else {
                    if (GameMode.getCreativeMode() == GameMode.CreativeMode.TILES) {
                        if (pressed) HeadUpDisplay.setSelectedTile(HeadUpDisplay.getSelectedTile() + 1);
                    } else {
                        if (pressed) HeadUpDisplay.setSelectedEntity(HeadUpDisplay.getSelectedEntity() + 1);
                    }
                }
                break;
            case GLFW_KEY_SPACE:
                if (pressed) {
                    if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                        Player.getInstance().roll();
                    }
                }
                break;
            case GLFW_KEY_ESCAPE:
                if (!pressed) Menu.getInstance().setShowing(!Menu.getInstance().isShowing());
                break;
            case GLFW_KEY_LEFT_CONTROL:
                LEFT_CTRL_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_LEFT_SHIFT:
                LEFT_SHIFT_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_F1:
                if (!pressed) Parameters.setDebugMode(!Parameters.isDebugMode());
                break;
            case GLFW_KEY_F2:
                if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.TILES);
                break;
            case GLFW_KEY_F3:
                if (!pressed) GameMode.setCreativeMode(GameMode.CreativeMode.STATIC_ENTITIES);
                break;
            case GLFW_KEY_F4:
                break;
            case GLFW_KEY_F5:
                if (!pressed) Scene.getInstance().reset();
                break;
            case GLFW_KEY_F6:
                if (!pressed) Player.getInstance().hurt(Player.getInstance().getHealth());
                break;
            case GLFW_KEY_F7:
                break;
            case GLFW_KEY_F8:
                break;
            case GLFW_KEY_F9:
                break;
            case GLFW_KEY_F10:
                break;
            case GLFW_KEY_F11:
                break;
            case GLFW_KEY_F12:
                //FIXME Coins generator only for testing purposes
                if (!pressed) {
                    int numOfCoinsToDrop = (int) (Math.random() * 5) + 5;
                    float areaOfDrop = 25f;
                    for (int i = 0; i < numOfCoinsToDrop; i++) {
                        new GoldCoin((int) ((mouseWorldCoordinates.x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                                (int) ((mouseWorldCoordinates.y - areaOfDrop / 2) + (Math.random() * areaOfDrop)));
                    }
                }
                break;
            case GLFW_KEY_1:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.IONIAN);
                            break;
                        case CREATIVE:
                        default:
                            GameMode.setLayerEditing(GameMode.LayerEditing.FIRST_LAYER);
                            break;
                    }
                }
                break;
            case GLFW_KEY_2:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.DORIAN);
                            break;
                        case CREATIVE:
                        default:
                            GameMode.setLayerEditing(GameMode.LayerEditing.SECOND_LAYER);
                            break;
                    }
                }
                break;
            case GLFW_KEY_3:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.PHRYGIAN);
                            break;
                        case CREATIVE:
                        default:
                            GameMode.setLayerEditing(GameMode.LayerEditing.THIRD_LAYER);
                            break;
                    }
                }
                break;
            case GLFW_KEY_4:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.LYDIAN);
                            break;
                    }
                }
                break;
            case GLFW_KEY_5:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.MIXOLYDIAN);
                            break;
                    }
                }
                break;
            case GLFW_KEY_6:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.AEOLIAN);
                            break;
                    }
                }
                break;
            case GLFW_KEY_7:
                if (!pressed) {
                    switch (GameMode.getGameMode()) {
                        case NORMAL:
                            Player.getInstance().setMusicalMode(MusicalMode.LOCRIAN);
                            break;
                    }
                }
                break;
            case GLFW_KEY_UP:
                if (pressed) Camera.increaseZoom();
                break;
            case GLFW_KEY_DOWN:
                if (pressed) Camera.decreaseZoom();
                break;
        }
    }

    public static void release() {
        keyCallback.free();
        mouseCallback.free();
        mousePosCallback.free();
        scrollCallback.free();
        enterCallback.free();
    }

    public static Coordinates getMouseCameraCoordinates() {
        return mouseCameraCoordinates;
    }

    public static Coordinates getMouseWindowCoordinates() {
        return mouseWindowCoordinates;
    }

    public static void updateMouseWorldCoordinates() {
        mouseWorldCoordinates = mouseCameraCoordinates.toWorldCoordinates();
    }

    public static Coordinates getMouseWorldCoordinates() {
        return mouseWorldCoordinates;
    }

    public static int getMouseWheelPosition() {
        return mouseWheelPosition;
    }

    /**
     * XBOX CONTROLLER INPUT HANDLING
     */
    private static float[] leftJoystickAxes = new float[2];
    private static float[] rightJoystickAxes = new float[2];
    public static float joystickSensibility = 0.15f;

    private static float leftTriggerValue = 0f;
    private static float rightTriggerValue = 0f;
    public static float triggersSensibility = - 0.95f;

    public static boolean aButtonPressed = false;
    public static boolean bButtonPressed = false;

    public static void updateControllerInputs() {
//        Log.l("Controller plugged in: " + glfwGetJoystickName(GLFW_JOYSTICK_1));

        FloatBuffer joystickAxes01 = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
        if (joystickAxes01 != null) {
            for(int i = 0; i < joystickAxes01.capacity(); i++) {
                processJoystickAxesInput(i, joystickAxes01.get(i));
            }
        }

        ByteBuffer joystickButtons01 = GLFW.glfwGetJoystickButtons(GLFW_JOYSTICK_1);
        if (joystickAxes01 != null) {
            for(int i = 0; i < joystickButtons01.capacity(); i++) {
                processJoystickButtonInput(i, joystickButtons01.get(i) == 1);
            }
        }
    }

    public static float[] getLeftJoystickAxes() {
        return leftJoystickAxes;
    }

    public static float[] getRightJoystickAxes() {
        return rightJoystickAxes;
    }

    public static float getLeftTriggerValue() {
        return leftTriggerValue;
    }

    public static float getRightTriggerValue() {
        return rightTriggerValue;
    }

    public static void processJoystickAxesInput(int button, float value) {
        switch (button) {
            case 0:
                if (Math.abs(value) > joystickSensibility) {
//                    Log.l("Left Stick, X axis, value: " + value);
                    leftJoystickAxes[0] = value;
                    usingKeyboardAndMouse = false;
                } else {
                    leftJoystickAxes[0] = 0f;
                }
                break;
            case 1:
                if (Math.abs(value) > joystickSensibility) {
//                    Log.l("Left Stick, Y axis, value: " + value);
                    leftJoystickAxes[1] = value;
                    usingKeyboardAndMouse = false;
                } else {
                    leftJoystickAxes[1] = 0f;
                }
                break;
            case 2:
                if (Math.abs(value) > joystickSensibility) {
//                    Log.l("Right Stick, X axis, value: " + value);
                    rightJoystickAxes[0] = value;
                    usingKeyboardAndMouse = false;
                } else {
                    rightJoystickAxes[0] = 0f;
                }
                break;
            case 3:
                if (Math.abs(value) > joystickSensibility) {
//                    Log.l("Right Stick, Y axis, value: " + value);
                    rightJoystickAxes[1] = value;
                    usingKeyboardAndMouse = false;
                } else {
                    rightJoystickAxes[1] = 0f;
                }
                break;
            case 4:
                if (value > triggersSensibility) {
//                    Log.l("Left Trigger, value: " + value);
                    leftTriggerValue = value;
                } else {
                    leftTriggerValue = 0f;
                }
                break;
            case 5:
                if (value > triggersSensibility) {
//                    Log.l("Right Trigger, value: " + value);
                    rightTriggerValue = value;
                } else {
                    rightTriggerValue = 0f;
                }
                break;
            default:
                break;
        }
    }

    public static void processJoystickButtonInput(int button, boolean pressed) {
        switch (button) {
            case 0: //A
                if (pressed && !aButtonPressed) {
//                    Log.l("A pressed!");
                    aButtonPressed = true;
                    usingKeyboardAndMouse = false;
                } else if (!pressed && aButtonPressed) {
//                    Log.l("A released!");
                    aButtonPressed = false;
                }
                break;
            case 1: //B
                if (pressed && !bButtonPressed) {
//                    Log.l("B pressed!");
                    bButtonPressed = true;
                    usingKeyboardAndMouse = false;
                    if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                        Player.getInstance().roll();
                    }
                } else if (!pressed && bButtonPressed) {
//                    Log.l("B released!");
                    bButtonPressed = false;
                }
                break;
            case 2: //X
                if (pressed) Log.l("X pressed!");
                break;
            case 3: //Y
                if (pressed) Log.l("Y pressed!");
                break;
            case 4: //Left Shoulder
                if (pressed) Log.l("Left Shoulder pressed!");
                break;
            case 5: //Right Shoulder
                if (pressed) Log.l("Right Shoulder pressed!");
                break;
            case 6: //Back Button
                if (pressed) Log.l("Back Button pressed!");
                break;
            case 7: //Start Button
                if (pressed) Log.l("Start Button pressed!");
                break;
            case 8: //Left Sticker
                if (pressed) Log.l("Left Sticker pressed!");
                break;
            case 9: //Right Sticker
                if (pressed) Log.l("Right Sticker pressed!");
                break;
            default:
                break;
        }
    }

    public static boolean isUsingKeyboardAndMouse() {
        return usingKeyboardAndMouse;
    }
}

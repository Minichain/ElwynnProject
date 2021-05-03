package listeners;

import entities.*;
import menu.Scroll;
import scene.Camera;
import main.*;
import menu.Menu;
import org.lwjgl.glfw.*;
import ui.HeadUpDisplay;

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
    private static boolean LEFT_CTRL_KEY_PRESSED;
    private static boolean LEFT_SHIFT_KEY_PRESSED;
    private static boolean LEFT_ALT_KEY_PRESSED;
    private static boolean LEFT_ALT_GR_KEY_PRESSED;

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
                setKeyPressed(button, action == GLFW_PRESS);
                usingKeyboardAndMouse = true;
                leftMouseButtonPressed = (button == GLFW_MOUSE_BUTTON_1) && (action == GLFW_PRESS);
                rightMouseButtonPressed = (button == GLFW_MOUSE_BUTTON_2) && (action == GLFW_PRESS);
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
                        if (yOffset < 0.0) {
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

    private static void setKeyPressed(int key, boolean pressed) {
//        Log.l("setKeyPressed key: " + key + ", pressed: " + pressed);
        usingKeyboardAndMouse = true;
        switch (key) {
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL:
                LEFT_CTRL_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT:
                LEFT_SHIFT_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_LEFT_ALT:
                LEFT_ALT_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_RIGHT_ALT:
                LEFT_ALT_GR_KEY_PRESSED = pressed;
                break;
        }

        sendInputToActionManager(key, pressed);
    }

    private static void sendInputToActionManager(int key, boolean pressed) {
        int[] keyCombination = new int[]{-1, -1};
        if (LEFT_CTRL_KEY_PRESSED) {
            keyCombination[0] = GLFW_MOD_CONTROL;
            keyCombination[1] = key;
        } else if (LEFT_SHIFT_KEY_PRESSED) {
            keyCombination[0] = GLFW_MOD_SHIFT;
            keyCombination[1] = key;
        } else if (LEFT_ALT_KEY_PRESSED) {
            keyCombination[0] = GLFW_KEY_LEFT_ALT;
            keyCombination[1] = key;
        } else if (LEFT_ALT_GR_KEY_PRESSED) {
            keyCombination[0] = GLFW_KEY_RIGHT_ALT;
            keyCombination[1] = key;
        } else {
            keyCombination[0] = key;
        }
        ActionManager.processKeyPressed(keyCombination, pressed);
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

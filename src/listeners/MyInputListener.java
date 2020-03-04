package listeners;

import entities.Camera;
import entities.Scene;
import entities.TileMap;
import main.*;
import menu.Menu;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class MyInputListener {
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
        if (!Menu.getInstance().isShowing() && GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            Coordinates tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
            int layer = 0;
            switch (GameMode.getCreativeMode()) {
                case FIRST_LAYER:
                    layer = 0;
                    break;
                case SECOND_LAYER:
                    layer = 1;
                    break;
                case THIRD_LAYER:
                    layer = 2;
                    break;
            }
            TileMap.setTile((int) tileCoordinates.x, (int) tileCoordinates.y, layer, (byte) (MyInputListener.getMouseWheelPosition()));
        }
    }

    private static void processLeftMouseButtonReleased() {
        leftMouseButtonPressed = false;
    }

    private static void processRightMouseButtonPressed() {
        rightMouseButtonPressed = true;
        if (!Menu.getInstance().isShowing() ) {
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                // Change Tile's collision behaviour
                Coordinates tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mouseCameraCoordinates.x, mouseCameraCoordinates.y);
                TileMap.getArrayOfTiles()[(int) tileCoordinates.x][(int) tileCoordinates.y].changeCollisionBehaviour();
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
        switch(key) {
            case GLFW_KEY_W:
                W_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_A:
                A_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_S:
                S_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_D:
                D_KEY_PRESSED = pressed;
                break;
            case GLFW_KEY_ESCAPE:
                if (pressed) Menu.getInstance().setShowing(!Menu.getInstance().isShowing());
                break;
            case GLFW_KEY_F1:
                if (pressed) Parameters.setDebugMode(!Parameters.isDebugMode());
                break;
            case GLFW_KEY_F2:
                break;
            case GLFW_KEY_F3:
                break;
            case GLFW_KEY_F4:
                if (pressed) Scene.getInstance().initEntities();
                break;
            case GLFW_KEY_F5:
                if (pressed) WorldLoader.saveWorld();
                break;
            case GLFW_KEY_F6:
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
                break;
            case GLFW_KEY_1:
                if (pressed) GameMode.setCreativeMode(GameMode.CreativeMode.FIRST_LAYER);
                break;
            case GLFW_KEY_2:
                if (pressed) GameMode.setCreativeMode(GameMode.CreativeMode.SECOND_LAYER);
                break;
            case GLFW_KEY_3:
                if (pressed) GameMode.setCreativeMode(GameMode.CreativeMode.THIRD_LAYER);
                break;
            case GLFW_KEY_4:
                break;
            case GLFW_KEY_UP:
                if (pressed) Camera.setZoom(Camera.getZoom() + 0.1);
                break;
            case GLFW_KEY_DOWN:
                if (pressed) Camera.setZoom(Camera.getZoom() - 0.1);
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
}

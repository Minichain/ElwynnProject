package listeners;

import entities.Camera;
import entities.Scene;
import main.*;
import org.lwjgl.glfw.*;
import utils.MathUtils;

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
    private static int mousePositionX;
    private static int mousePositionY;
    private static int mouseWheelPosition;
    public static boolean leftMouseButtonPressed;
    public static boolean rightMouseButtonPressed;

    /** KEYBOARD **/
    public static boolean wKeyPressed;
    public static boolean aKeyPressed;
    public static boolean sKeyPressed;
    public static boolean dKeyPressed;

    public static void initMyInputListener() {
        long window = Parameters.getWindow();

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
                mousePositionX = (int) x;
                mousePositionY = (int) y;
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
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            int[] tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mousePositionX, mousePositionY);
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
            Scene.getInstance().setTile(tileCoordinates[0], tileCoordinates[1], layer, (byte) (MyInputListener.getMouseWheelPosition()));
        }
    }

    private static void processLeftMouseButtonReleased() {
        leftMouseButtonPressed = false;
    }

    private static void processRightMouseButtonPressed() {
        rightMouseButtonPressed = true;
        if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
            // Change Tile's collision behaviour
            int[] tileCoordinates = Coordinates.cameraCoordinatesToTileCoordinates(mousePositionX, mousePositionY);
            Scene.getArrayOfTiles()[tileCoordinates[0]][tileCoordinates[1]].changeCollisionBehaviour();
        }
    }

    private static void processRightMouseButtonReleased() {
        rightMouseButtonPressed = false;
    }

    private static void setKeyPressed(int key, boolean pressed) {
        if (key == GLFW_KEY_W) {
            wKeyPressed = pressed;
        } else if (key == GLFW_KEY_A) {
            aKeyPressed = pressed;
        } else if (key == GLFW_KEY_S) {
            sKeyPressed = pressed;
        } else if (key == GLFW_KEY_D) {
            dKeyPressed = pressed;
        } else if (key == GLFW_KEY_ESCAPE && pressed) {
            GameStatus.getInstance().setGameRunning(false);
        } else if (key == GLFW_KEY_F1 && pressed) {
            Parameters.setDebugMode(!Parameters.isDebugMode());
        } else if (key == GLFW_KEY_F2 && pressed) {
            GameMode.setGameMode(GameMode.Mode.NORMAL);
        } else if (key == GLFW_KEY_F3 && pressed) {
            GameMode.setGameMode(GameMode.Mode.CREATIVE);
        } else if (key == GLFW_KEY_F4 && pressed) {
            Scene.getInstance().initEntities();
        } else if (key == GLFW_KEY_F5 && pressed) {
            WorldLoader.saveWorld();
        } else if (key == GLFW_KEY_1 && pressed) {
            GameMode.setCreativeMode(GameMode.CreativeMode.FIRST_LAYER);
        } else if (key == GLFW_KEY_2 && pressed) {
            GameMode.setCreativeMode(GameMode.CreativeMode.SECOND_LAYER);
        } else if (key == GLFW_KEY_3 && pressed) {
            GameMode.setCreativeMode(GameMode.CreativeMode.THIRD_LAYER);
        } else if (key == GLFW_KEY_UP && pressed) {
            Camera.setZoom(Camera.getZoom() + 0.1);
        } else if (key == GLFW_KEY_DOWN && pressed) {
            Camera.setZoom(Camera.getZoom() - 0.1);
        }
    }

    public static double[] computeMovementVector(long timeElapsed, double speed) {
        double[] movement = new double[2];
        if (MyInputListener.sKeyPressed) {
            movement[1] = 1;
        }
        if (MyInputListener.aKeyPressed) {
            movement[0] = -1;
        }
        if (MyInputListener.wKeyPressed) {
            movement[1] = -1;
        }
        if (MyInputListener.dKeyPressed) {
            movement[0] = 1;
        }

        movement = MathUtils.normalizeVector(movement);
        movement[0] *= timeElapsed * speed;
        movement[1] *= timeElapsed * speed;

        return movement;
    }

    public static void release() {
        keyCallback.free();
        mouseCallback.free();
        mousePosCallback.free();
        scrollCallback.free();
        enterCallback.free();
    }

    public static int getMousePositionX() {
        return mousePositionX;
    }

    public static int getMousePositionY() {
        return mousePositionY;
    }

    public static int getMouseWheelPosition() {
        return mouseWheelPosition;
    }
}

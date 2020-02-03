package listeners;

import entities.Scene;
import main.Coordinates;
import main.GameMode;
import main.GameStatus;
import main.Parameters;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class MyInputListener {
    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseCallback;
    private static GLFWCursorPosCallback mousePosCallback;
    private static GLFWScrollCallback scrollCallback;
    private static GLFWCursorEnterCallback enterCallback;

    private static boolean mouseInside;
    private static int mousePositionX;
    private static int mousePositionY;
    private static int mouseWheelPosition;

    public static boolean wKeyPressed;
    public static boolean aKeyPressed;
    public static boolean sKeyPressed;
    public static boolean dKeyPressed;

    public static void initMyInputListener() {
        long window = Parameters.getInstance().getWindow();

        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                setKeyPressed(key, action != GLFW_RELEASE);
            }
        };

        mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    processLeftButtonPressed();
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

    private static void processLeftButtonPressed() {
        if (GameMode.getInstance().getGameMode() == GameMode.Mode.CREATIVE) {
            double[] globalCoordinates = new Coordinates(mousePositionX, mousePositionY).toGlobalCoordinates();
            Scene.getInstance().setTile((int) (globalCoordinates[0] / (Scene.getTileWidth() * Scene.getZoom())),
                    (int) (globalCoordinates[1] / (Scene.getTileHeight() * Scene.getZoom())), (byte) (MyInputListener.getMouseWheelPosition() % 10));
        }
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
            Parameters.getInstance().setDebugMode(!Parameters.getInstance().isDebugMode());
        } else if (key == GLFW_KEY_F2 && pressed) {
            GameMode.getInstance().setGameMode(GameMode.Mode.NORMAL);
        } else if (key == GLFW_KEY_F3 && pressed) {
            GameMode.getInstance().setGameMode(GameMode.Mode.CREATIVE);
        }
    }

    public static double[] computeMovementVector(long timeElapsed, double speed) {
        double[] movement = new double[2];
        if (MyInputListener.sKeyPressed) {
            movement[1] = movement[1] + timeElapsed * speed;
        }
        if (MyInputListener.aKeyPressed) {
            movement[0] = movement[0] - timeElapsed * speed;
        }
        if (MyInputListener.wKeyPressed) {
            movement[1] = movement[1] - timeElapsed * speed;
        }
        if (MyInputListener.dKeyPressed) {
            movement[0] = movement[0] + timeElapsed * speed;
        }

        //Normalize movement. The module of the movement vector must stay close to 1.
        if (movement[0] != 0 && movement[1] != 0) {
            movement[0] *= 0.75;
            movement[1] *= 0.75;
        }
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

package listeners;

import entities.Scene;
import main.Coordinates;
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

    public static boolean mouseInside;
    public static int mousePositionX;
    public static int mousePositionY;
    public static int mouseWheelPosition;

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
                    double[] globalCoordinates = new Coordinates(mousePositionX, mousePositionY).toGlobalCoordinates();
                    Scene.getInstance().setTile((int) (globalCoordinates[0] / (Scene.getTileWidth() * Scene.getZoom())),
                            (int) (globalCoordinates[1] / (Scene.getTileHeight() * Scene.getZoom())), (byte) (MyInputListener.getMouseWheelPosition() % 10));
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

    private static void setKeyPressed(int key, boolean pressed) {
        if (key == GLFW_KEY_W) {
            wKeyPressed = pressed;
        } else if (key == GLFW_KEY_A) {
            aKeyPressed = pressed;
        } else if (key == GLFW_KEY_S) {
            sKeyPressed = pressed;
        } else if (key == GLFW_KEY_D) {
            dKeyPressed = pressed;
        } else if (key == GLFW_KEY_ESCAPE) {
            GameStatus.getInstance().setGameRunning(false);
        }
    }

    public static void release() {
        keyCallback.free();
        mouseCallback.free();
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

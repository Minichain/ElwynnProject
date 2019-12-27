package listeners;

import main.Parameters;
import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class MyInputListener {
    private static MyInputListener instance;

    private static GLFWKeyCallback keyCallback;

    public static int mousePositionX;
    public static int mousePositionY;
    public static int mouseWheelPosition;

    public static boolean wKeyPressed;
    public static boolean aKeyPressed;
    public static boolean sKeyPressed;
    public static boolean dKeyPressed;

    public MyInputListener() {
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                System.out.println("MyInputListener: Key: " + key + ", ScanCode: " + scanCode);
                setKeyPressed(key, action != GLFW_RELEASE);
            }
        };
        glfwSetKeyCallback(Parameters.getInstance().getWindow(), keyCallback);
    }

    public MyInputListener getInstance() {
        if (instance == null) {
            instance = new MyInputListener();
        }
        return instance;
    }

    public void setKeyPressed(int key, boolean pressed) {
        if (key == GLFW_KEY_W) {
            wKeyPressed = pressed;
        } else if (key == GLFW_KEY_A) {
            aKeyPressed = pressed;
        } else if (key == GLFW_KEY_S) {
            sKeyPressed = pressed;
        } else if (key == GLFW_KEY_D) {
            dKeyPressed = pressed;
        }
    }
}

package listeners;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class MyInputListener {
    private static GLFWKeyCallback keyCallback;

    public static int mousePositionX;
    public static int mousePositionY;
    public static int mouseWheelPosition;

    public static boolean wKeyPressed;
    public static boolean aKeyPressed;
    public static boolean sKeyPressed;
    public static boolean dKeyPressed;

    public MyInputListener(long window) {
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                System.out.println("MyInputListener: Key: " + key + ", ScanCode: " + scanCode);

                if ( action == GLFW_PRESS ) {
                    if ( key == GLFW_KEY_W ) {
                        wKeyPressed = true;
                    } else if ( key == GLFW_KEY_A ) {
                        aKeyPressed = true;
                    } else if ( key == GLFW_KEY_S ) {
                        sKeyPressed = true;
                    } else if ( key == GLFW_KEY_D ) {
                        dKeyPressed = true;
                    }
                } else if ( action == GLFW_RELEASE ) {
                    if ( key == GLFW_KEY_W ) {
                        wKeyPressed = false;
                    } else if ( key == GLFW_KEY_A ) {
                        aKeyPressed = false;
                    } else if ( key == GLFW_KEY_S ) {
                        sKeyPressed = false;
                    } else if ( key == GLFW_KEY_D ) {
                        dKeyPressed = false;
                    }
                } else if ( action == GLFW_REPEAT ) {

                }
            }
        });
    }
}

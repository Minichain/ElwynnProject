package listeners;

import main.Strings;

import static org.lwjgl.glfw.GLFW.*;

public class MyGLFW {
    public static String myGlfwGetKeyName(int key) {
        switch (key) {
            case GLFW_KEY_F1:
                return "F1";
            case GLFW_KEY_F2:
                return "F2";
            case GLFW_KEY_F3:
                return "F3";
            case GLFW_KEY_F4:
                return "F4";
            case GLFW_KEY_F5:
                return "F5";
            case GLFW_KEY_F6:
                return "F6";
            case GLFW_KEY_F7:
                return "F7";
            case GLFW_KEY_F8:
                return "F8";
            case GLFW_KEY_F9:
                return "F9";
            case GLFW_KEY_F10:
                return "F10";
            case GLFW_KEY_F11:
                return "F11";
            case GLFW_KEY_F12:
                return "F12";
            case GLFW_MOUSE_BUTTON_1:
                return "LEFT MOUSE BUTTON";
            case GLFW_MOUSE_BUTTON_2:
                return "RIGHT MOUSE BUTTON";
            case GLFW_KEY_SPACE:
                return Strings.getString("key_space_bar");
            case GLFW_KEY_ESCAPE:
                return "ESC";
            case GLFW_KEY_LEFT_ALT:
                return "ALT";
            case GLFW_KEY_LEFT_CONTROL:
                return "CTRL";
            case GLFW_KEY_LEFT_SHIFT:
                return "SHIFT";
            case GLFW_KEY_TAB:
                return Strings.getString("key_tab");
            default:
                return glfwGetKeyName(key, 0);
        }
    }
}

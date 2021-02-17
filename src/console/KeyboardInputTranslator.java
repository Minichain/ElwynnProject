package console;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInputTranslator {
    public static String translateKeyInputIntoChar(int[] key) {
        switch (key[0]) {
            case GLFW_KEY_SPACE:
                return " ";
            case GLFW_MOD_SHIFT:
                switch (key[1]) {
                    case GLFW_KEY_1:
                        return "!";
                    case GLFW_KEY_2:
                        return "\"";
                    case GLFW_KEY_3:
                        return "·";
                    case GLFW_KEY_4:
                        return "$";
                    case GLFW_KEY_5:
                        return "%";
                    case GLFW_KEY_6:
                        return "&";
                    case GLFW_KEY_7:
                        return "/";
                    case GLFW_KEY_8:
                        return "(";
                    case GLFW_KEY_9:
                        return ")";
                    case GLFW_KEY_0:
                        return "=";
                    default:
                        String input = glfwGetKeyName(key[1], 0);
                        if (input != null && input.matches(".*[a-zñ].*")) {
                            return input.toUpperCase();
                        } else {
                            return "";
                        }
                }
            case GLFW_KEY_RIGHT_ALT:
                switch (key[1]) {
                    case GLFW_KEY_1:
                        return "|";
                    case GLFW_KEY_2:
                        return "@";
                    case GLFW_KEY_3:
                        return "#";
                    case GLFW_KEY_4:
                        return "~";
                    case GLFW_KEY_5:
                    case GLFW_KEY_E:
                        return "€";
                    case GLFW_KEY_6:
                        return "¬";
                    default:
                        return "";
                }
            default:
                return glfwGetKeyName(key[0], 0);
        }
    }
}

package console;

import main.*;
import text.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Console {
    private static ArrayList<String> listOfTextLines;
    private static Coordinates coordinates;
    private static int maxNumberOfLines;
    private static boolean typingMode;
    private static String currentInput;

    public static void init() {
        listOfTextLines = new ArrayList<>();
        maxNumberOfLines = 10;
        typingMode = false;
        currentInput = "";
    }

    public static void update(long timeElapsed) {
        coordinates = new Coordinates(10, Window.getHeight() - 180);
    }

    public static void render() {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        if (isTypingMode()) {
            OpenGLManager.glBegin(GL_TRIANGLES);
            OpenGLManager.drawRectangle((int) coordinates.x, (int) coordinates.y, 1000, 24 * Parameters.getResolutionFactor(), 0.5, 0f, 0f, 0f);
            glEnd();
        }

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);

        String textLine;
        if (!currentInput.isEmpty()) {
            TextRendering.renderText((int) coordinates.x, (int) coordinates.y, currentInput, 2f * Parameters.getResolutionFactor(),
                    true, 1, 1f, 1f, 1f);
        }
        for (int i = 1; i <= listOfTextLines.size(); i++) {
            textLine = listOfTextLines.get(listOfTextLines.size() - i);
            TextRendering.renderText((int) coordinates.x, (int) coordinates.y - i * (24 * Parameters.getResolutionFactor()), textLine,
                    2f * Parameters.getResolutionFactor(), true, 1, 1f, 1f, 1f);
        }

        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void addNewLine(String newLine) {
        listOfTextLines.add(newLine);
        if (listOfTextLines.size() > maxNumberOfLines) {
            listOfTextLines.remove(0);
        }
    }

    public static boolean isTypingMode() {
        return typingMode;
    }

    public static void setTypingMode(boolean typingMode) {
        Console.typingMode = typingMode;
        if (!typingMode) {
            if (!currentInput.isEmpty()) {
                Log.l(currentInput);
            }
            currentInput = "";
        }
    }

    public static void processInputKey(int[] key) {
        if (key[0] == GLFW_KEY_BACKSPACE && currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            String translatedInput = translateKeyInput(key[0]);
            if (translatedInput != null) {
                currentInput = currentInput.concat(translatedInput);
            }
        }
    }

    public static String translateKeyInput(int key) {
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
            case GLFW_KEY_SPACE:
                return " ";
            default:
                return glfwGetKeyName(key, 0);
        }
    }
}
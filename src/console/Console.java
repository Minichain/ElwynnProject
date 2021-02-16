package console;

import entities.Player;
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
                processConsoleInput(currentInput);
            }
            currentInput = "";
        }
    }

    public static void processInputKey(int[] key) {
        if (key[0] == GLFW_KEY_BACKSPACE && currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            String translatedInput = translateKeyInput(key);
            if (translatedInput != null) {
                currentInput = currentInput.concat(translatedInput);
            }
        }
    }

    private static void processConsoleInput(String input) {
        if (input.startsWith("/")) {
            String[] intputSplitted;
            if (input.startsWith("/weather")) {
                intputSplitted = input.split(" ");
                if (intputSplitted[1].equals("clear")) {
                    Weather.setWeatherStatus(Weather.WeatherStatus.CLEAR);
                } else if (intputSplitted[1].equals("rain") || intputSplitted[1].equals("raining")) {
                    Weather.setWeatherStatus(Weather.WeatherStatus.RAINING);
                } else {
                    Log.e("Unrecognized command");
                }
            } else if (input.startsWith("/time")) {
                intputSplitted = input.split(" ");
                try {
                    float time = Float.parseFloat(intputSplitted[1]);
                    if (time >= 0) {
                        GameTime.setGameTime(time);
                    } else {
                        Log.e("Invalid time");
                    }
                } catch (Exception e) {
                    Log.e("Invalid time");
                }
            } else if (input.startsWith("/tp")) {
                intputSplitted = input.split(" ");
                try {
                    int x = Integer.parseInt(intputSplitted[1]);
                    int y = Integer.parseInt(intputSplitted[2]);
                    Player.getInstance().setWorldCoordinates(new Coordinates(x, y));
                } catch (Exception e) {
                    Log.e("Invalid coordinates");
                }
            } else {
                Log.e("Invalid command");
            }
        } else {
            Log.l(currentInput);
        }
    }

    public static String translateKeyInput(int[] key) {
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
                    case GLFW_KEY_7:
                        return "/";
                    default:
                        return "";
                }
            case GLFW_KEY_RIGHT_ALT:
                switch (key[1]) {
                    case GLFW_KEY_1:
                        return "|";
                    case GLFW_KEY_2:
                        return "@";
                    case GLFW_KEY_3:
                        return "#";
                    default:
                        return "";
                }
            default:
                return glfwGetKeyName(key[0], 0);
        }
    }
}
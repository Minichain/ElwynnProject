package console;

import entities.Player;
import main.*;
import text.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Console {
    private static Console instance = null;
    private ArrayList<String> listOfTextLines;
    private Coordinates coordinates;
    private int maxNumberOfLines;
    private boolean typingMode;
    private String currentInput;

    public Console() {
        listOfTextLines = new ArrayList<>();
        maxNumberOfLines = 10;
        typingMode = false;
        currentInput = "";
    }

    public static Console getInstance() {
        if (instance == null) {
            instance = new Console();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        coordinates = new Coordinates(10, Window.getHeight() - 180);
    }

    public void render() {
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

    public void addNewLine(String newLine) {
        listOfTextLines.add(newLine);
        if (listOfTextLines.size() > maxNumberOfLines) {
            listOfTextLines.remove(0);
        }
    }

    public boolean isTypingMode() {
        return typingMode;
    }

    public void setTypingMode(boolean typingMode) {
        this.typingMode = typingMode;
        if (!typingMode) {
            if (!currentInput.isEmpty()) {
                processConsoleInput(currentInput);
            }
            currentInput = "";
        }
    }

    public void processInputKey(int[] key) {
        if (key[0] == GLFW_KEY_BACKSPACE && currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            String translatedInput = KeyboardInputTranslator.translateKeyInputIntoChar(key);
            if (translatedInput != null) {
                currentInput = currentInput.concat(translatedInput);
            }
        }
    }

    private void processConsoleInput(String input) {
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
}
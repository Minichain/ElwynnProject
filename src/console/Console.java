package console;

import main.*;
import text.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Console {
    private static Console instance = null;
    private ArrayList<ConsoleLine> listOfTextLines;
    private Coordinates coordinates;
    private int maxNumberOfLines;
    private boolean typingMode;
    private String currentInput;
    private double oscillation;

    private class ConsoleLine {
        private float timeToLive = 10000f;
        private float timeLiving;
        private String string;

        public ConsoleLine(String string) {
            this.string = string;
            this.timeLiving = 0;
        }

        public void update(long timeElapsed) {
            this.timeLiving += timeElapsed;
            if (this.timeLiving > this.timeToLive) this.timeLiving = this.timeToLive;
        }

        public void render(int x, int y) {
            TextRendering.renderText(x, y, this.string,
                    2f * Parameters.getHeightResolutionFactor(), true, 1f - (timeLiving / timeToLive), 1f, 1f, 1f);
        }

        public String getString() {
            return string;
        }
    }

    public Console() {
        listOfTextLines = new ArrayList<>();
        maxNumberOfLines = 10;
        typingMode = false;
        currentInput = "";
        oscillation = 0;
    }

    public static Console getInstance() {
        if (instance == null) {
            instance = new Console();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        coordinates = new Coordinates(10, Window.getHeight() - 180);
        for (ConsoleLine listOfTextLine : listOfTextLines) {
            listOfTextLine.update(timeElapsed);
        }
        oscillation += (timeElapsed / 200.0);
        oscillation %= (Math.PI * 2);
    }

    public void render() {
        int inputLength = currentInput.length();

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        if (isTypingMode()) {
            OpenGLManager.glBegin(GL_TRIANGLES);
            OpenGLManager.drawRectangle((int) coordinates.x, (int) coordinates.y,
                    1000, 24 * Parameters.getHeightResolutionFactor(), 0.5, 0f, 0f, 0f);
            OpenGLManager.drawRectangle((int) (coordinates.x + (inputLength * TextRendering.CHARACTER_WIDTH * 2f * Parameters.getWidthResolutionFactor())),
                    (int) coordinates.y, 1, 24 * Parameters.getHeightResolutionFactor(),
                    (Math.sin(oscillation) + 1) / 2, 1f, 1f, 1f);
            glEnd();
        }

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);

        if (isTypingMode() && inputLength > 0) {
            TextRendering.renderText((int) coordinates.x, (int) coordinates.y, currentInput, 2f * Parameters.getHeightResolutionFactor(),
                    true, 1, 1f, 1f, 1f);
        }
        for (int i = 1; i <= listOfTextLines.size(); i++) {
            int x = (int) (coordinates.x);
            int y = (int) (coordinates.y - i * (24 * Parameters.getHeightResolutionFactor()));
            listOfTextLines.get(listOfTextLines.size() - i).render(x, y);
        }

        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public void addNewLine(String newLine) {
        listOfTextLines.add(new ConsoleLine(newLine));
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
                ConsoleInputProcessor.processInput(currentInput);
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
}

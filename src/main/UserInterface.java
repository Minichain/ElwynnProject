package main;

import utils.MathUtils;
import entities.*;
import entities.Character;
import listeners.MyInputListener;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class UserInterface {
    public static UserInterface instance = null;
    public static Texture fontSpriteWhite;

    public UserInterface() {
        loadFontSprite();
    }

    public static UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    private void loadFontSprite() {
        String path = "res/sprites/bitmap_font_white.png";
        fontSpriteWhite = Texture.loadTexture(path);
    }

    public void render(long timeElapsed) {
        renderCursorUI();
        renderDebugUI(timeElapsed);
    }

    private void renderDebugUI(long timeElapsed) {
        if (!Parameters.getInstance().isDebugMode()) {
            return;
        }

        if (timeElapsed <= 0) timeElapsed = 1;
        float fps = 1000 / timeElapsed;
        double[] characterLocalCoordinates = Character.getInstance().getCurrentCoordinates().toLocalCoordinates();

        /** DEBUG TEXT **/
        fontSpriteWhite.bind();

        ArrayList<String> textList = new ArrayList<>();
        int textScale = 2;
        int topMargin = 10;
        int leftMargin = 10;
        int gapBetweenTexts = 10 * textScale;

        textList.add("FPS: " + fps);
        textList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
        textList.add("Num of Tiles: " + Scene.getInstance().getArrayOfTiles().length * Scene.getInstance().getArrayOfTiles()[0].length);
        textList.add("Character Coordinates: (" + (float) Character.getInstance().getCurrentCoordinates().x + ", " + (float) Character.getInstance().getCurrentCoordinates().y + ")");
        textList.add("Character Local Coordinates: (" + (float) characterLocalCoordinates[0] + ", " + (float) characterLocalCoordinates[1] + ")");
        textList.add("Mouse Position: (" + (float) MyInputListener.getMousePositionX() + ", " + (float) MyInputListener.getMousePositionY() + ")");
        textList.add("Game Mode: " + " " + GameMode.getInstance().getGameMode());

        glBegin(GL_QUADS);
        for (int i = 0; i < textList.size(); i++) {
            renderText(leftMargin, i * gapBetweenTexts + topMargin, textList.get(i), textScale);
        }
        glEnd();

        /** DEBUG LINES **/
        glDisable(GL_BLEND);
        glBegin(GL_LINES);
        glLineWidth(4);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2i(Parameters.getInstance().getWindowWidth() / 2, 0);
        glVertex2i(Parameters.getInstance().getWindowWidth() / 2, Parameters.getInstance().getWindowHeight());
        glVertex2i(0, Parameters.getInstance().getWindowHeight() / 2);
        glVertex2i(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight() / 2);
        glEnd();
        glEnable(GL_BLEND);
    }

    private void renderText(int x, int y, String textToRender, int scale) {
        int numOfTilesInTileSetX = 18;
        int numOfTilesInTileSetY = 6;
        int characterWidth = fontSpriteWhite.getWidth() / numOfTilesInTileSetX;
        int characterHeight = fontSpriteWhite.getHeight() / numOfTilesInTileSetY;
        int gapBetweenCharacters = 2 * scale;
        String[] characters = textToRender.split("(?!^)");
        for (int i = 0; i < characters.length; i++) {
            int[] characterPosition = getCharacterPosition(characters[i]);
            int tileFromTileSetX = characterPosition[0];
            int tileFromTileSetY = characterPosition[1];
            double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
            double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
            double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
            double v2 = v + (1.0 / (float) numOfTilesInTileSetY);
            MyOpenGL.drawTexture(x + (i * (characterWidth + gapBetweenCharacters)), y, u, v2, u2, v, characterWidth * scale, characterHeight * scale);
        }
    }

    private void renderCursorUI() {
        int mouseX = MyInputListener.getMousePositionX();
        int mouseY = MyInputListener.getMousePositionY();
        if (GameMode.getInstance().getGameMode() == GameMode.Mode.NORMAL) {
            int timeUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "time");
            int characterCoordinatesUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "characterLocalCoordinates");
            ARBShaderObjects.glUseProgramObjectARB(MyOpenGL.programShader01);
            ARBShaderObjects.glUniform1fARB(timeUniformLocation, (float) GameStatus.RUNTIME);

            double[] characterLocalCoordinates = Character.getInstance().getCoordinates().toLocalCoordinates();
            ARBShaderObjects.glUniform2fvARB(characterCoordinatesUniformLocation, new float[]{(float) characterLocalCoordinates[0], Parameters.getInstance().getWindowHeight() - (float) characterLocalCoordinates[1]});

            double[] characterLocalCoords = Character.getInstance().getCurrentCoordinates().toLocalCoordinates();
            double[] v1 = new double[]{mouseX - characterLocalCoords[0], mouseY - characterLocalCoords[1]};
            v1 = MathUtils.normalizeVector(v1);
            double[] v2 = MathUtils.generateOrthonormalVector(v1);
            v2 = MathUtils.normalizeVector(v2);

            float coneWidth = 300;
            float coneLength = 1000;

            double[] vertex1 = new double[]{characterLocalCoords[0] + (v1[0] * coneLength) + (v2[0] * coneWidth),
                    characterLocalCoords[1] + (v1[1] * coneLength) + (v2[1] * coneWidth)};
            double[] vertex2 = new double[]{characterLocalCoords[0] + (v1[0] * coneLength) - (v2[0] * coneWidth),
                    characterLocalCoords[1] + (v1[1] * coneLength) - (v2[1] * coneWidth)};
            double[] vertex3 = new double[]{characterLocalCoords[0], characterLocalCoords[1]};

            glBegin(GL_TRIANGLES);

            glVertex2d(vertex1[0], vertex1[1]);
            glVertex2d(vertex2[0], vertex2[1]);
            glVertex2d(vertex3[0], vertex3[1]);

            glEnd();

            //release the shader
            ARBShaderObjects.glUseProgramObjectARB(0);

            Entity entity;
            for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
                entity = Scene.getInstance().getListOfEntities().get(i);
                double[] entityLocalCoords = entity.getCoordinates().toLocalCoordinates();
                if (entity instanceof Enemy
                        && MathUtils.isPointInsideTriangle(new double[]{entityLocalCoords[0], entityLocalCoords[1]}, vertex1, vertex2, vertex3)) {
//                    System.out.println("Damage dealt to enemy " + i + ", Health: " + ((Enemy) entity).HEALTH);
                    ((Enemy) entity).HEALTH -= 1f;
                }
            }
        } else if (GameMode.getInstance().getGameMode() == GameMode.Mode.CREATIVE
                && 0 < mouseX && mouseX < Parameters.getInstance().getWindowWidth()
                && 0 < mouseY && mouseY < Parameters.getInstance().getWindowHeight()) {
            Scene.getInstance().bindTileSetTexture();
            glBegin(GL_QUADS);
            int numOfTilesInTileSetX = 4;
            int numOfTilesInTileSetY = 4;
            int[] tileFromTileSet = Scene.getInstance().getTile(MyInputListener.getMouseWheelPosition() % 10);
            int tileFromTileSetX = tileFromTileSet[0];
            int tileFromTileSetY = tileFromTileSet[1];
            double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
            double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
            double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
            double v2 = v + (1.0 / (float) numOfTilesInTileSetY);
            MyOpenGL.drawTexture(mouseX, mouseY, u, v2, u2, v, 32, 32);
            glEnd();
        }
    }

    public int[] getCharacterPosition(String character) {
        int[] tileFromTileSet;
        switch (character) {
            case "z":
            default:
                tileFromTileSet = new int[]{0, 0};
                return tileFromTileSet;
            case "{":
                tileFromTileSet = new int[]{1, 0};
                return tileFromTileSet;
            case "|":
                tileFromTileSet = new int[]{2, 0};
                return tileFromTileSet;
            case "}":
                tileFromTileSet = new int[]{3, 0};
                return tileFromTileSet;
            case "~":
                tileFromTileSet = new int[]{4, 0};
                return tileFromTileSet;
            case "h":
                tileFromTileSet = new int[]{0, 1};
                return tileFromTileSet;
            case "i":
                tileFromTileSet = new int[]{1, 1};
                return tileFromTileSet;
            case "j":
                tileFromTileSet = new int[]{2, 1};
                return tileFromTileSet;
            case "k":
                tileFromTileSet = new int[]{3, 1};
                return tileFromTileSet;
            case "l":
                tileFromTileSet = new int[]{4, 1};
                return tileFromTileSet;
            case "m":
                tileFromTileSet = new int[]{5, 1};
                return tileFromTileSet;
            case "n":
                tileFromTileSet = new int[]{6, 1};
                return tileFromTileSet;
            case "o":
                tileFromTileSet = new int[]{7, 1};
                return tileFromTileSet;
            case "p":
                tileFromTileSet = new int[]{8, 1};
                return tileFromTileSet;
            case "q":
                tileFromTileSet = new int[]{9, 1};
                return tileFromTileSet;
            case "r":
                tileFromTileSet = new int[]{10, 1};
                return tileFromTileSet;
            case "s":
                tileFromTileSet = new int[]{11, 1};
                return tileFromTileSet;
            case "t":
                tileFromTileSet = new int[]{12, 1};
                return tileFromTileSet;
            case "u":
                tileFromTileSet = new int[]{13, 1};
                return tileFromTileSet;
            case "v":
                tileFromTileSet = new int[]{14, 1};
                return tileFromTileSet;
            case "w":
                tileFromTileSet = new int[]{15, 1};
                return tileFromTileSet;
            case "x":
                tileFromTileSet = new int[]{16, 1};
                return tileFromTileSet;
            case "y":
                tileFromTileSet = new int[]{17, 1};
                return tileFromTileSet;
            case "V":
                tileFromTileSet = new int[]{0, 2};
                return tileFromTileSet;
            case "W":
                tileFromTileSet = new int[]{1, 2};
                return tileFromTileSet;
            case "X":
                tileFromTileSet = new int[]{2, 2};
                return tileFromTileSet;
            case "Y":
                tileFromTileSet = new int[]{3, 2};
                return tileFromTileSet;
            case "Z":
                tileFromTileSet = new int[]{4, 2};
                return tileFromTileSet;
            case "[":
                tileFromTileSet = new int[]{5, 2};
                return tileFromTileSet;
            case "\\":
                tileFromTileSet = new int[]{6, 2};
                return tileFromTileSet;
            case "]":
                tileFromTileSet = new int[]{7, 2};
                return tileFromTileSet;
            case "^":
                tileFromTileSet = new int[]{8, 2};
                return tileFromTileSet;
            case "_":
                tileFromTileSet = new int[]{9, 2};
                return tileFromTileSet;
            case "´":
                tileFromTileSet = new int[]{10, 2};
                return tileFromTileSet;
            case "a":
                tileFromTileSet = new int[]{11, 2};
                return tileFromTileSet;
            case "b":
                tileFromTileSet = new int[]{12, 2};
                return tileFromTileSet;
            case "c":
                tileFromTileSet = new int[]{13, 2};
                return tileFromTileSet;
            case "d":
                tileFromTileSet = new int[]{14, 2};
                return tileFromTileSet;
            case "e":
                tileFromTileSet = new int[]{15, 2};
                return tileFromTileSet;
            case "f":
                tileFromTileSet = new int[]{16, 2};
                return tileFromTileSet;
            case "g":
                tileFromTileSet = new int[]{17, 2};
                return tileFromTileSet;
            case "D":
                tileFromTileSet = new int[]{0, 3};
                return tileFromTileSet;
            case "E":
                tileFromTileSet = new int[]{1, 3};
                return tileFromTileSet;
            case "F":
                tileFromTileSet = new int[]{2, 3};
                return tileFromTileSet;
            case "G":
                tileFromTileSet = new int[]{3, 3};
                return tileFromTileSet;
            case "H":
                tileFromTileSet = new int[]{4, 3};
                return tileFromTileSet;
            case "I":
                tileFromTileSet = new int[]{5, 3};
                return tileFromTileSet;
            case "J":
                tileFromTileSet = new int[]{6, 3};
                return tileFromTileSet;
            case "K":
                tileFromTileSet = new int[]{7, 3};
                return tileFromTileSet;
            case "L":
                tileFromTileSet = new int[]{8, 3};
                return tileFromTileSet;
            case "M":
                tileFromTileSet = new int[]{9, 3};
                return tileFromTileSet;
            case "N":
                tileFromTileSet = new int[]{10, 3};
                return tileFromTileSet;
            case "O":
                tileFromTileSet = new int[]{11, 3};
                return tileFromTileSet;
            case "P":
                tileFromTileSet = new int[]{12, 3};
                return tileFromTileSet;
            case "Q":
                tileFromTileSet = new int[]{13, 3};
                return tileFromTileSet;
            case "R":
                tileFromTileSet = new int[]{14, 3};
                return tileFromTileSet;
            case "S":
                tileFromTileSet = new int[]{15, 3};
                return tileFromTileSet;
            case "T":
                tileFromTileSet = new int[]{16, 3};
                return tileFromTileSet;
            case "U":
                tileFromTileSet = new int[]{17, 3};
                return tileFromTileSet;
            case "2":
                tileFromTileSet = new int[]{0, 4};
                return tileFromTileSet;
            case "3":
                tileFromTileSet = new int[]{1, 4};
                return tileFromTileSet;
            case "4":
                tileFromTileSet = new int[]{2, 4};
                return tileFromTileSet;
            case "5":
                tileFromTileSet = new int[]{3, 4};
                return tileFromTileSet;
            case "6":
                tileFromTileSet = new int[]{4, 4};
                return tileFromTileSet;
            case "7":
                tileFromTileSet = new int[]{5, 4};
                return tileFromTileSet;
            case "8":
                tileFromTileSet = new int[]{6, 4};
                return tileFromTileSet;
            case "9":
                tileFromTileSet = new int[]{7, 4};
                return tileFromTileSet;
            case ":":
                tileFromTileSet = new int[]{8, 4};
                return tileFromTileSet;
            case ";":
                tileFromTileSet = new int[]{9, 4};
                return tileFromTileSet;
            case "<":
                tileFromTileSet = new int[]{10, 4};
                return tileFromTileSet;
            case "=":
                tileFromTileSet = new int[]{11, 4};
                return tileFromTileSet;
            case ">":
                tileFromTileSet = new int[]{12, 4};
                return tileFromTileSet;
            case "?":
                tileFromTileSet = new int[]{13, 4};
                return tileFromTileSet;
            case "@":
                tileFromTileSet = new int[]{14, 4};
                return tileFromTileSet;
            case "A":
                tileFromTileSet = new int[]{15, 4};
                return tileFromTileSet;
            case "B":
                tileFromTileSet = new int[]{16, 4};
                return tileFromTileSet;
            case "C":
                tileFromTileSet = new int[]{17, 4};
                return tileFromTileSet;
            case " ":
                tileFromTileSet = new int[]{0, 5};
                return tileFromTileSet;
            case "!":
                tileFromTileSet = new int[]{1, 5};
                return tileFromTileSet;
            case "\"":
                tileFromTileSet = new int[]{2, 5};
                return tileFromTileSet;
            case "#":
                tileFromTileSet = new int[]{3, 5};
                return tileFromTileSet;
            case "$":
                tileFromTileSet = new int[]{4, 5};
                return tileFromTileSet;
            case "%":
                tileFromTileSet = new int[]{5, 5};
                return tileFromTileSet;
            case "&":
                tileFromTileSet = new int[]{6, 5};
                return tileFromTileSet;
            case "'":
                tileFromTileSet = new int[]{7, 5};
                return tileFromTileSet;
            case "(":
                tileFromTileSet = new int[]{8, 5};
                return tileFromTileSet;
            case ")":
                tileFromTileSet = new int[]{9, 5};
                return tileFromTileSet;
            case "*":
            case "·":
                tileFromTileSet = new int[]{10, 5};
                return tileFromTileSet;
            case "+":
                tileFromTileSet = new int[]{11, 5};
                return tileFromTileSet;
            case ",":
                tileFromTileSet = new int[]{12, 5};
                return tileFromTileSet;
            case "-":
                tileFromTileSet = new int[]{13, 5};
                return tileFromTileSet;
            case ".":
                tileFromTileSet = new int[]{14, 5};
                return tileFromTileSet;
            case "/":
                tileFromTileSet = new int[]{15, 5};
                return tileFromTileSet;
            case "0":
                tileFromTileSet = new int[]{16, 5};
                return tileFromTileSet;
            case "1":
                tileFromTileSet = new int[]{17, 5};
                return tileFromTileSet;

        }
    }
}

package text;

import main.OpenGLManager;
import main.Texture;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class TextRendering {
    public static Texture fontSpriteWhite;
    public static final int CHARACTER_WIDTH = 7;
    public static final int CHARACTER_HEIGHT = 13;
    private static final int numOfTilesInTileSetX = 18;
    private static final int numOfTilesInTileSetY = 8;

    public static void init() {
        loadFontSprite();
    }

    private static void loadFontSprite() {
        String path = "res/sprites/fonts/bitmap_font_white.png";
        fontSpriteWhite = Texture.loadTexture(path);
    }

    /**
     * Renders a list of Strings. Each string in a different line.
     * This method only binds the font sprite texture and starts the openGL once.
     * */
    public static void renderText(float leftMargin, float topMargin, float gapBetweenTexts, ArrayList<String> textList, float textScale) {
        fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);
        for (int i = 0; i < textList.size(); i++) {
            TextRendering.renderText(leftMargin, i * gapBetweenTexts + topMargin, textList.get(i), textScale, true);
        }
        glDisable(GL_TEXTURE_2D);
        glEnd();
    }

    /**
     * Renders an String at (x, y) with the desired scale.
     * */
    public static void renderText(float x, float y, String textToRender, float scale) {
        renderText(x, y, textToRender, scale, false);
    }

    public static void renderText(float x, float y, String textToRender, float scale, boolean isTextureBoundAndOpenGlBegun) {
        renderText(x, y, textToRender, scale, isTextureBoundAndOpenGlBegun, 1f);
    }

    public static void renderText(float x, float y, String textToRender, float scale, boolean isTextureBoundAndOpenGlBegun, float alpha) {
        renderText(x, y, textToRender, scale, isTextureBoundAndOpenGlBegun, alpha, 1f, 1f, 1f);
    }

    public static void renderText(float x, float y, String textToRender, float scale, boolean isTextureBoundAndOpenGlBegun, float alpha, float r, float g, float b) {
        if (!isTextureBoundAndOpenGlBegun) {
            fontSpriteWhite.bind();
            glEnable(GL_TEXTURE_2D);
            OpenGLManager.glBegin(GL_QUADS);
        }

        String[] characters = textToRender.split("(?!^)");
        int i = 0;
        for (String s : characters) {
            int[] characterPosition = getCharacterPosition(s);
            renderCharacter(characterPosition[0], characterPosition[1], (x + (i * CHARACTER_WIDTH * scale)), y, scale, alpha, r, g, b);
            i++;
        }

        if (!isTextureBoundAndOpenGlBegun) {
            glDisable(GL_TEXTURE_2D);
            glEnd();
        }
    }

    private static void renderCharacter(int tileFromTileSetX, int tileFromTileSetY, float x, float y, float scale, float alpha, float r, float g, float b) {
        float u = ((1f / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
        float v = ((1f / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
        float u2 = u + (1f / (float) numOfTilesInTileSetX);
        float v2 = v + (1f / (float) numOfTilesInTileSetY);
        OpenGLManager.drawTexture((int) x, (int) y, u, v2, u2, v, CHARACTER_WIDTH * scale, CHARACTER_HEIGHT * scale, alpha, r, g, b);
    }

    private static int[] getCharacterPosition(String character) {
        int[] tileFromTileSet;
        switch (character) {
            case "Ü":
                tileFromTileSet = new int[]{0, 0};
                return tileFromTileSet;
            case "ù":
                tileFromTileSet = new int[]{1, 0};
                return tileFromTileSet;
            case "ú":
                tileFromTileSet = new int[]{2, 0};
                return tileFromTileSet;
            case "ü":
                tileFromTileSet = new int[]{3, 0};
                return tileFromTileSet;
            case "Ë":
                tileFromTileSet = new int[]{0, 1};
                return tileFromTileSet;
            case "è":
                tileFromTileSet = new int[]{1, 1};
                return tileFromTileSet;
            case "é":
                tileFromTileSet = new int[]{2, 1};
                return tileFromTileSet;
            case "ë":
                tileFromTileSet = new int[]{3, 1};
                return tileFromTileSet;
            case "Í":
                tileFromTileSet = new int[]{4, 1};
                return tileFromTileSet;
            case "Ì":
                tileFromTileSet = new int[]{5, 1};
                return tileFromTileSet;
            case "Ï":
                tileFromTileSet = new int[]{6, 1};
                return tileFromTileSet;
            case "ì":
                tileFromTileSet = new int[]{7, 1};
                return tileFromTileSet;
            case "í":
                tileFromTileSet = new int[]{8, 1};
                return tileFromTileSet;
            case "ï":
                tileFromTileSet = new int[]{9, 1};
                return tileFromTileSet;
            case "Ò":
                tileFromTileSet = new int[]{10, 1};
                return tileFromTileSet;
            case "Ó":
                tileFromTileSet = new int[]{11, 1};
                return tileFromTileSet;
            case "Ö":
                tileFromTileSet = new int[]{12, 1};
                return tileFromTileSet;
            case "ò":
                tileFromTileSet = new int[]{13, 1};
                return tileFromTileSet;
            case "ó":
                tileFromTileSet = new int[]{14, 1};
                return tileFromTileSet;
            case "ö":
                tileFromTileSet = new int[]{15, 1};
                return tileFromTileSet;
            case "Ù":
                tileFromTileSet = new int[]{16, 1};
                return tileFromTileSet;
            case "Ú":
                tileFromTileSet = new int[]{17, 1};
                return tileFromTileSet;
            case "z":
                tileFromTileSet = new int[]{0, 2};
                return tileFromTileSet;
            case "{":
                tileFromTileSet = new int[]{1, 2};
                return tileFromTileSet;
            case "|":
                tileFromTileSet = new int[]{2, 2};
                return tileFromTileSet;
            case "}":
                tileFromTileSet = new int[]{3, 2};
                return tileFromTileSet;
            case "~":
                tileFromTileSet = new int[]{4, 2};
                return tileFromTileSet;
            case "·":
                tileFromTileSet = new int[]{5, 2};
                return tileFromTileSet;
            case "Ñ":
                tileFromTileSet = new int[]{6, 2};
                return tileFromTileSet;
            case "ñ":
                tileFromTileSet = new int[]{7, 2};
                return tileFromTileSet;
            case "Ç":
                tileFromTileSet = new int[]{8, 2};
                return tileFromTileSet;
            case "ç":
                tileFromTileSet = new int[]{9, 2};
                return tileFromTileSet;
            case "À":
                tileFromTileSet = new int[]{10, 2};
                return tileFromTileSet;
            case "Á":
                tileFromTileSet = new int[]{11, 2};
                return tileFromTileSet;
            case "Ä":
                tileFromTileSet = new int[]{12, 2};
                return tileFromTileSet;
            case "à":
                tileFromTileSet = new int[]{13, 2};
                return tileFromTileSet;
            case "á":
                tileFromTileSet = new int[]{14, 2};
                return tileFromTileSet;
            case "ä":
                tileFromTileSet = new int[]{15, 2};
                return tileFromTileSet;
            case "È":
                tileFromTileSet = new int[]{16, 2};
                return tileFromTileSet;
            case "É":
                tileFromTileSet = new int[]{17, 2};
                return tileFromTileSet;
            case "h":
                tileFromTileSet = new int[]{0, 3};
                return tileFromTileSet;
            case "i":
                tileFromTileSet = new int[]{1, 3};
                return tileFromTileSet;
            case "j":
                tileFromTileSet = new int[]{2, 3};
                return tileFromTileSet;
            case "k":
                tileFromTileSet = new int[]{3, 3};
                return tileFromTileSet;
            case "l":
                tileFromTileSet = new int[]{4, 3};
                return tileFromTileSet;
            case "m":
                tileFromTileSet = new int[]{5, 3};
                return tileFromTileSet;
            case "n":
                tileFromTileSet = new int[]{6, 3};
                return tileFromTileSet;
            case "o":
                tileFromTileSet = new int[]{7, 3};
                return tileFromTileSet;
            case "p":
                tileFromTileSet = new int[]{8, 3};
                return tileFromTileSet;
            case "q":
                tileFromTileSet = new int[]{9, 3};
                return tileFromTileSet;
            case "r":
                tileFromTileSet = new int[]{10, 3};
                return tileFromTileSet;
            case "s":
                tileFromTileSet = new int[]{11, 3};
                return tileFromTileSet;
            case "t":
                tileFromTileSet = new int[]{12, 3};
                return tileFromTileSet;
            case "u":
                tileFromTileSet = new int[]{13, 3};
                return tileFromTileSet;
            case "v":
                tileFromTileSet = new int[]{14, 3};
                return tileFromTileSet;
            case "w":
                tileFromTileSet = new int[]{15, 3};
                return tileFromTileSet;
            case "x":
                tileFromTileSet = new int[]{16, 3};
                return tileFromTileSet;
            case "y":
                tileFromTileSet = new int[]{17, 3};
                return tileFromTileSet;
            case "V":
                tileFromTileSet = new int[]{0, 4};
                return tileFromTileSet;
            case "W":
                tileFromTileSet = new int[]{1, 4};
                return tileFromTileSet;
            case "X":
                tileFromTileSet = new int[]{2, 4};
                return tileFromTileSet;
            case "Y":
                tileFromTileSet = new int[]{3, 4};
                return tileFromTileSet;
            case "Z":
                tileFromTileSet = new int[]{4, 4};
                return tileFromTileSet;
            case "[":
                tileFromTileSet = new int[]{5, 4};
                return tileFromTileSet;
            case "\\":
                tileFromTileSet = new int[]{6, 4};
                return tileFromTileSet;
            case "]":
                tileFromTileSet = new int[]{7, 4};
                return tileFromTileSet;
            case "^":
                tileFromTileSet = new int[]{8, 4};
                return tileFromTileSet;
            case "_":
                tileFromTileSet = new int[]{9, 4};
                return tileFromTileSet;
            case "´":
                tileFromTileSet = new int[]{10, 4};
                return tileFromTileSet;
            case "a":
                tileFromTileSet = new int[]{11, 4};
                return tileFromTileSet;
            case "b":
                tileFromTileSet = new int[]{12, 4};
                return tileFromTileSet;
            case "c":
                tileFromTileSet = new int[]{13, 4};
                return tileFromTileSet;
            case "d":
                tileFromTileSet = new int[]{14, 4};
                return tileFromTileSet;
            case "e":
                tileFromTileSet = new int[]{15, 4};
                return tileFromTileSet;
            case "f":
                tileFromTileSet = new int[]{16, 4};
                return tileFromTileSet;
            case "g":
                tileFromTileSet = new int[]{17, 4};
                return tileFromTileSet;
            case "D":
                tileFromTileSet = new int[]{0, 5};
                return tileFromTileSet;
            case "E":
                tileFromTileSet = new int[]{1, 5};
                return tileFromTileSet;
            case "F":
                tileFromTileSet = new int[]{2, 5};
                return tileFromTileSet;
            case "G":
                tileFromTileSet = new int[]{3, 5};
                return tileFromTileSet;
            case "H":
                tileFromTileSet = new int[]{4, 5};
                return tileFromTileSet;
            case "I":
                tileFromTileSet = new int[]{5, 5};
                return tileFromTileSet;
            case "J":
                tileFromTileSet = new int[]{6, 5};
                return tileFromTileSet;
            case "K":
                tileFromTileSet = new int[]{7, 5};
                return tileFromTileSet;
            case "L":
                tileFromTileSet = new int[]{8, 5};
                return tileFromTileSet;
            case "M":
                tileFromTileSet = new int[]{9, 5};
                return tileFromTileSet;
            case "N":
                tileFromTileSet = new int[]{10, 5};
                return tileFromTileSet;
            case "O":
                tileFromTileSet = new int[]{11, 5};
                return tileFromTileSet;
            case "P":
                tileFromTileSet = new int[]{12, 5};
                return tileFromTileSet;
            case "Q":
                tileFromTileSet = new int[]{13, 5};
                return tileFromTileSet;
            case "R":
                tileFromTileSet = new int[]{14, 5};
                return tileFromTileSet;
            case "S":
                tileFromTileSet = new int[]{15, 5};
                return tileFromTileSet;
            case "T":
                tileFromTileSet = new int[]{16, 5};
                return tileFromTileSet;
            case "U":
                tileFromTileSet = new int[]{17, 5};
                return tileFromTileSet;
            case "2":
                tileFromTileSet = new int[]{0, 6};
                return tileFromTileSet;
            case "3":
                tileFromTileSet = new int[]{1, 6};
                return tileFromTileSet;
            case "4":
                tileFromTileSet = new int[]{2, 6};
                return tileFromTileSet;
            case "5":
                tileFromTileSet = new int[]{3, 6};
                return tileFromTileSet;
            case "6":
                tileFromTileSet = new int[]{4, 6};
                return tileFromTileSet;
            case "7":
                tileFromTileSet = new int[]{5, 6};
                return tileFromTileSet;
            case "8":
                tileFromTileSet = new int[]{6, 6};
                return tileFromTileSet;
            case "9":
                tileFromTileSet = new int[]{7, 6};
                return tileFromTileSet;
            case ":":
                tileFromTileSet = new int[]{8, 6};
                return tileFromTileSet;
            case ";":
                tileFromTileSet = new int[]{9, 6};
                return tileFromTileSet;
            case "<":
                tileFromTileSet = new int[]{10, 6};
                return tileFromTileSet;
            case "=":
                tileFromTileSet = new int[]{11, 6};
                return tileFromTileSet;
            case ">":
                tileFromTileSet = new int[]{12, 6};
                return tileFromTileSet;
            case "?":
                tileFromTileSet = new int[]{13, 6};
                return tileFromTileSet;
            case "@":
                tileFromTileSet = new int[]{14, 6};
                return tileFromTileSet;
            case "A":
                tileFromTileSet = new int[]{15, 6};
                return tileFromTileSet;
            case "B":
                tileFromTileSet = new int[]{16, 6};
                return tileFromTileSet;
            case "C":
                tileFromTileSet = new int[]{17, 6};
                return tileFromTileSet;
            case " ":
                tileFromTileSet = new int[]{0, 7};
                return tileFromTileSet;
            case "!":
                tileFromTileSet = new int[]{1, 7};
                return tileFromTileSet;
            case "\"":
                tileFromTileSet = new int[]{2, 7};
                return tileFromTileSet;
            case "#":
                tileFromTileSet = new int[]{3, 7};
                return tileFromTileSet;
            case "$":
                tileFromTileSet = new int[]{4, 7};
                return tileFromTileSet;
            case "%":
                tileFromTileSet = new int[]{5, 7};
                return tileFromTileSet;
            case "&":
                tileFromTileSet = new int[]{6, 7};
                return tileFromTileSet;
            case "'":
                tileFromTileSet = new int[]{7, 7};
                return tileFromTileSet;
            case "(":
                tileFromTileSet = new int[]{8, 7};
                return tileFromTileSet;
            case ")":
                tileFromTileSet = new int[]{9, 7};
                return tileFromTileSet;
            case "*":
                tileFromTileSet = new int[]{10, 7};
                return tileFromTileSet;
            case "+":
                tileFromTileSet = new int[]{11, 7};
                return tileFromTileSet;
            case ",":
                tileFromTileSet = new int[]{12, 7};
                return tileFromTileSet;
            case "-":
                tileFromTileSet = new int[]{13, 7};
                return tileFromTileSet;
            case ".":
                tileFromTileSet = new int[]{14, 7};
                return tileFromTileSet;
            case "/":
                tileFromTileSet = new int[]{15, 7};
                return tileFromTileSet;
            case "0":
                tileFromTileSet = new int[]{16, 7};
                return tileFromTileSet;
            case "1":
            default:
                tileFromTileSet = new int[]{17, 7};
                return tileFromTileSet;
        }
    }
}

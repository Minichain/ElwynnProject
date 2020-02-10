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

    public UserInterface() {
        TextRendering.init();
    }

    public static UserInterface getInstance() {
        if (instance == null) {
            instance = new UserInterface();
        }
        return instance;
    }

    public void render(long timeElapsed) {
        renderCursorUI();
        renderDebugUI(timeElapsed);
    }

    private void renderDebugUI(long timeElapsed) {
        if (Parameters.getInstance().isDebugMode()) {
            if (timeElapsed <= 0) timeElapsed = 1;
            float fps = 1000 / timeElapsed;
            double[] characterLocalCoordinates = Character.getInstance().getCurrentCoordinates().toLocalCoordinates();

            /** DEBUG TEXT **/
            ArrayList<String> textList = new ArrayList<>();
            int textScale = 2;
            int topMargin = 10;
            int leftMargin = 10;
            int gapBetweenTexts = 10 * textScale;

            textList.add("Show/Hide Debug Info: F1, Normal Mode: F2, Creative Mode: F3, Save World: F5");
            textList.add("FPS: " + fps);
            textList.add("Num of Entities: " + Scene.getInstance().getListOfEntities().size());
            textList.add("Num of Tiles: " + Scene.getInstance().getArrayOfTiles().length + " x " + Scene.getInstance().getArrayOfTiles()[0].length + " x " + Scene.getInstance().getArrayOfTiles()[0][0].length);
            textList.add("Character Coordinates: (" + (float) Character.getInstance().getCurrentCoordinates().x + ", " + (float) Character.getInstance().getCurrentCoordinates().y + ")");
            textList.add("Character Local Coordinates: (" + (float) characterLocalCoordinates[0] + ", " + (float) characterLocalCoordinates[1] + ")");
            textList.add("Mouse Position: (" + (float) MyInputListener.getMousePositionX() + ", " + (float) MyInputListener.getMousePositionY() + ")");
            if (GameMode.getInstance().getGameMode() == GameMode.Mode.CREATIVE) {
                textList.add("Game Mode: " + GameMode.getInstance().getGameMode() + ", Creative Mode: " + GameMode.getInstance().getCreativeMode());
            } else {
                textList.add("Game Mode: " + " " + GameMode.getInstance().getGameMode());
            }

            TextRendering.renderText(leftMargin, topMargin, gapBetweenTexts, textList, textScale);

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
    }

    private void renderCursorUI() {
        int mouseX = MyInputListener.getMousePositionX();
        int mouseY = MyInputListener.getMousePositionY();
        if (GameMode.getInstance().getGameMode() == GameMode.Mode.NORMAL && MyInputListener.leftMouseButtonPressed) {
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
            Scene.getInstance().drawTile(MyInputListener.getMouseWheelPosition(), mouseX, mouseY, 2, 1f, 1f, 1f, true);
            glEnd();
        }
    }
}

package main;

import entities.Camera;
import entities.Character;
import entities.Entity;
import entities.Scene;
import listeners.MyInputListener;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {
    private static GLFWWindowSizeCallback windowSizeCallback;

    public static void startGame() {
        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);
        Parameters.getInstance().setWindow(window);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwPollEvents();

        MyInputListener.initMyInputListener();

        windowSizeCallback = new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                Parameters.getInstance().setWindowWidth(width);
                Parameters.getInstance().setWindowHeight(height);
                glViewport(0, 0, width, height);
            }
        };
        glfwSetWindowSizeCallback(Parameters.getInstance().getWindow(), windowSizeCallback);

        GameStatus.getInstance().setGameRunning(true);
    }

    public static void update(long timeElapsed) {
        Character.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);

//        System.out.println("Character at (" + Character.getInstance().getCoordinates().x + ", " + Character.getInstance().getCoordinates().y + ")");
//        System.out.println("Camera at (" + Camera.getInstance().getCoordinates().x + ", " + Camera.getInstance().getCoordinates().y + ")");
    }

    public static void render(long timeElapsed) {
        MyOpenGL.prepareOpenGL();

        renderScene();
        renderUI();

        if (timeElapsed <= 0) timeElapsed = 1;
        float fps = 1000 / timeElapsed;
        System.out.println("FPS: " + fps);
    }

    private static void renderScene() {
        int renderDistance = 1000;  //TODO This should depend on the Window and Camera parameters
        double[] localCoordinates;

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        Scene.getInstance().bindTileSetTexture();
        glBegin(GL_QUADS);
        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
                double scale = Scene.getZoom();
                int x = (i * (int) (Scene.getTileWidth() * scale));
                int y = (j * (int) (Scene.getTileHeight() * scale));
                double distanceBetweenCharacterAndTile = Utils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                if (distanceBetweenCharacterAndTile < renderDistance) {
                    Scene.getInstance().drawTile(i, j, x, y, scale, (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                }
            }
        }
        glEnd();

        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
        Entity entity;
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        for (int i = 0; i < listOfEntities.size(); i++) {
            entity = listOfEntities.get(i);
            if (Utils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
                localCoordinates = entity.getCoordinates().toLocalCoordinates();
                entity.drawSprite((int) localCoordinates[0], (int) localCoordinates[1]);
            }
        }
    }

    private static void renderUI() {
        /** MOUSE POSITION **/
        int mouseX = MyInputListener.getMousePositionX();
        int mouseY = MyInputListener.getMousePositionY();
        if (0 < mouseX && mouseX < Parameters.getInstance().getWindowWidth()
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

    private void renderDebugUI() {

    }
}


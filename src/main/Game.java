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
    private static MyInputListener myInputListener;
    private static GLFWWindowSizeCallback windowSizeCallback;

    public static void startGame() {
        long window = glfwCreateWindow(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), "ElwynnProject", 0, 0);
        Parameters.getInstance().setWindow(window);
        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glfwPollEvents();

        myInputListener = new MyInputListener();

        windowSizeCallback = new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                Parameters.getInstance().setWindowWidth(width);
                Parameters.getInstance().setWindowHeight(height);
                glViewport(0, 0, width, height);
            }
        };
        glfwSetWindowSizeCallback(Parameters.getInstance().getWindow(), windowSizeCallback);
    }

    public static void updateScene(long timeElapsed) {
        Character.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);

//        System.out.println("Character at (" + Character.getInstance().getCoordinates().x + ", " + Character.getInstance().getCoordinates().y + ")");
//        System.out.println("Camera at (" + Camera.getInstance().getCoordinates().x + ", " + Camera.getInstance().getCoordinates().y + ")");
    }

    public static void renderScene() {
        int renderDistance = 1000;  //TODO This should depend on the Window and Camera parameters
        double[] localCoordinates;

        MyOpenGL.prepareOpenGL();

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        byte[][] arrayOfTiles = Scene.getInstance().getArrayOfTiles();
        //TODO create the background from the tiles and paint it as one image
        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
                int x = (i * 16 * 2);
                int y = (j * 16 * 2);
                localCoordinates = (new Coordinates(x, y)).toLocalCoordinates();
                if (Utils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y)) < renderDistance) {
                    Scene.getInstance().getTile(arrayOfTiles[i][j]).bind();
                    float u = 0;
                    float v = 1f;
                    float u2 = 1f;
                    float v2 = 0f;
                    glBegin(GL_QUADS);
                    MyOpenGL.drawTexture((int) localCoordinates[0], (int) localCoordinates[1], u, v, u2, v2, 16 * 3, 16 * 3);
                    glEnd();
                }
            }
        }

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

//    private void renderUI() {
//        batchUI.begin();
//
//        /** MOUSE POSITION **/
//        int mouseX = Gdx.input.getX();
//        int mouseY = Gdx.input.getY();
//        if (mouseX > 0 && mouseX < Gdx.graphics.getWidth()
//                && mouseY > 0 && mouseY < Gdx.graphics.getHeight()) {
//            batchUI.draw(Scene.getInstance().getTile(3), mouseX, Gdx.graphics.getHeight() - mouseY, 16,16);
//        }
//
//        batchUI.end();
//    }
//
//    private void renderDebugUI() {
//        batchDebugUI.begin();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.BLACK);
//
//        /** DEBUG ELEMENTS **/
//        shapeRenderer.line(0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2);
//        shapeRenderer.line(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
//        bitmapFont.setColor(Color.BLACK);
//        long fps = 1000 / timeElapsed;
//        bitmapFont.draw(batchDebugUI, "FPS: " + fps, 10, 20);
//        bitmapFont.draw(batchDebugUI, "X: " + (int) Character.getInstance().getCurrentCoordinates().x, 10, 35);
//        bitmapFont.draw(batchDebugUI, "Y: " + (int) Character.getInstance().getCurrentCoordinates().y, 10, 50);
//        bitmapFont.draw(batchDebugUI, "Number of entities: " + Scene.getInstance().getListOfEntities().size(), 10, 65);
//
//        shapeRenderer.end();
//        batchDebugUI.end();
//    }
}


package main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import entities.Camera;
import entities.Character;
import entities.Entity;
import entities.Scene;

import java.util.List;

public class Game implements ApplicationListener {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private long timeElapsed;
    private long lastUpdateTime = 0;
    private long currentTime;
    private long maxTimeBetweenFrames = 1000 / Parameters.getInstance().getFramesPerSecond();

    @Override
    public void create() {
        System.out.println("ElwynGraphicsLog:: ApplicationListener create");
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
    }

    @Override
    public void dispose() {
        System.out.println("ElwynGraphicsLog:: ApplicationListener dispose");
        batch.dispose();
    }

    @Override
    public void render() {
        System.out.println("ElwynGraphicsLog:: ApplicationListener update and render");
        //Compute the time elapsed since the last frame
        currentTime = System.currentTimeMillis();
        timeElapsed = currentTime - lastUpdateTime;

        /** PROCESS INPUTS **/
        processInputs();

        /** UPDATE **/
        updateScene();

        /** RENDER **/
        renderScene();

        //Wait time until processing next frame. FPS locked.
        lastUpdateTime = currentTime;
        if ((System.currentTimeMillis() - currentTime) < maxTimeBetweenFrames) {
            try {
                Thread.sleep(maxTimeBetweenFrames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processInputs() {  //TODO InputProcessor class should be created
        if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
            Parameters.getInstance().setDebugMode(!Parameters.getInstance().isDebugMode());
        }
    }

    private void updateScene() {
        Character.getInstance().update(timeElapsed);
        Camera.getInstance().update(timeElapsed);
    }

    private void renderScene() {
        int renderDistance = 2500;  //TODO This should depend on the Window and Camera width/height
        double[] localCoordinates;
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        Gdx.gl.glLineWidth(1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        byte[][] arrayOfTyles = Scene.getInstance().getArrayOfTiles();
        localCoordinates = Scene.getInstance().getCenter().toLocalCoordinates();
        //TODO create the background from the tiles and paint it as one image
        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
                int x = (i * Parameters.getTilesSizeX());
                int y = (j * Parameters.getTilesSizeY());
                if (Utils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y)) < renderDistance) {
                    batch.draw(Scene.getInstance().getTile(arrayOfTyles[i][j]),
                            (float) (x + localCoordinates[0]),
                            (float) (y + localCoordinates[1]),
                            64, 64);
                }
            }
        }

        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
        Entity entity;
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        for (int i = 0; i < listOfEntities.size(); i++) {
            entity = listOfEntities.get(i);
//            System.out.println("Painting tree Nº " + (i + 1)
//                    + " at (" + entity.getCoordinates().getxCoordinate()
//                    + ", " + entity.getCoordinates().getyCoordinate());
            localCoordinates = entity.getCoordinates().toLocalCoordinates();
            if (Utils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
//            if (true) {
                batch.draw(entity.getSprite(),
                        (float)localCoordinates[0]
                                - (float)(entity.getSprite().getRegionWidth() / 2),
                        (float)localCoordinates[1]
                                - (float)(entity.getSprite().getRegionHeight() * 0.75));

                /** DEBUG ELEMENTS **/
                if (Parameters.getInstance().isDebugMode()) {
//                    int radius = 50;
//                    graphics2D.drawOval((int)localCoordinates[0] - radius,
//                            (int)localCoordinates[1] - radius,
//                            radius * 2, radius * 2);
                }
            }
        }

        /** DEBUG ELEMENTS **/
        if (Parameters.getInstance().isDebugMode()) {
            long fps = 1000 / timeElapsed;
            shapeRenderer.line(0, Parameters.getInstance().getWindowHeight()/2,
                    Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight()/2);
            shapeRenderer.line(Parameters.getInstance().getWindowWidth()/2, 0,
                    Parameters.getInstance().getWindowWidth()/2, Parameters.getInstance().getWindowHeight());
            bitmapFont.setColor(Color.BLACK);
            bitmapFont.draw(batch, "FPS: " + fps, 10, 20);
            bitmapFont.draw(batch, "X: " + (int) Character.getInstance().getCurrentCoordinates().getxCoordinate(), 10, 35);
            bitmapFont.draw(batch, "Y: " + (int)Character.getInstance().getCurrentCoordinates().getyCoordinate(), 10, 50);
            bitmapFont.draw(batch, "Number of entities: " + Scene.getInstance().getListOfEntities().size(), 10, 65);
        }

        batch.end();
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("ElwynGraphicsLog:: ApplicationListener resize to (" + width + ", " + height + ")");
        Gdx.graphics.setDisplayMode(width, height, false);
    }

    @Override
    public void pause() {
        System.out.println("ElwynGraphicsLog:: ApplicationListener pause");
    }

    @Override
    public void resume() {
        System.out.println("ElwynGraphicsLog:: ApplicationListener resume");
    }
}


//package main;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import entities.Camera;
//import entities.Character;
//import entities.Entity;
//import entities.Scene;
//import listeners.MyInputListener;
//
//import java.util.List;
//
//public class Game implements ApplicationListener {
//    private SpriteBatch batch;
//    private SpriteBatch batchUI;
//    private SpriteBatch batchDebugUI;
//    private ShapeRenderer shapeRenderer;
//    private BitmapFont bitmapFont;
//    private long timeElapsed;
//    private long lastUpdateTime = 0;
//    private long currentTime;
//    private long maxTimeBetweenFrames = 1000 / Parameters.getInstance().getForegroundFramesPerSecond();
//
//    @Override
//    public void create() {
//        System.out.println("ElwynGraphicsLog:: ApplicationListener create");
//        Camera.getInstance().position.set(
//                (float) Character.getInstance().getCurrentCoordinates().x,
//                (float) Character.getInstance().getCurrentCoordinates().y,
//                0);
//        Camera.getInstance().update();
//        batch = new SpriteBatch();
//        batchUI = new SpriteBatch();
//        batchDebugUI = new SpriteBatch();
//        shapeRenderer = new ShapeRenderer();
//        bitmapFont = new BitmapFont();
//        Gdx.input.setInputProcessor(MyInputListener.getInstance());
//    }
//
//    @Override
//    public void dispose() {
//        System.out.println("ElwynGraphicsLog:: ApplicationListener dispose");
//        batch.dispose();
//        batchUI.dispose();
//        batchDebugUI.dispose();
//    }
//
//    @Override
//    public void render() {
////        System.out.println("ElwynGraphicsLog:: ApplicationListener update and render");
//        //Compute the time elapsed since the last frame
//        currentTime = System.currentTimeMillis();
//        timeElapsed = currentTime - lastUpdateTime;
//
//        /** UPDATE **/
//        updateScene();
//
//        /** RENDER **/
//        renderScene();
//        renderUI();
//        if (Parameters.getInstance().isDebugMode()) {
//            renderDebugUI();
//        }
//
//        //Wait time until processing next frame. FPS locked.
//        lastUpdateTime = currentTime;
////        if ((System.currentTimeMillis() - currentTime) < maxTimeBetweenFrames) {
////            try {
////                Thread.sleep(maxTimeBetweenFrames);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//    }
//
//    private void updateScene() {
//        Character.getInstance().update(timeElapsed);
//        Camera.getInstance().update(timeElapsed);
//
////        System.out.println("Character at (" + Character.getInstance().getCoordinates().x + ", " + Character.getInstance().getCoordinates().y + ")");
////        System.out.println("Camera at (" + Camera.getInstance().getCoordinates().x + ", " + Camera.getInstance().getCoordinates().y + ")");
////        System.out.println("Cursor at (" + Gdx.input.getX() + ", " + Gdx.input.getY() + ")");
//    }
//
//    private void renderScene() {
//        int renderDistance = 1000;  //TODO This should depend on the Window and Camera parameters
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//        batch.setProjectionMatrix(Camera.getInstance().combined);
//
//        /** SCENE BACKGROUND IS DRAWN FIRST **/
//        byte[][] arrayOfTiles = Scene.getInstance().getArrayOfTiles();
//        //TODO create the background from the tiles and paint it as one image
//        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
//            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
//                int x = (i * 16);
//                int y = (j * 16);
//                if (Utils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y)) < renderDistance) {
//                    batch.draw(Scene.getInstance().getTile(arrayOfTiles[i][j]),
//                            (float) x,
//                            (float) y,
//                            16, 16);
//                }
//            }
//        }
//
//        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
//        Entity entity;
//        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
//        for (int i = 0; i < listOfEntities.size(); i++) {
//            entity = listOfEntities.get(i);
//            if (Utils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
//                batch.draw(entity.getSprite(),
//                        (float) entity.getCoordinates().x - (float) (entity.getSprite().getRegionWidth() * 0.5),
//                        (float) entity.getCoordinates().y - (float) (entity.getSprite().getRegionHeight() * 0.5));
//            }
//        }
//
//        batch.end();
//    }
//
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
//
//    @Override
//    public void resize(int width, int height) {
//        System.out.println("ElwynGraphicsLog:: ApplicationListener resize to (" + width + ", " + height + ")");
////        Camera.getInstance().viewportWidth = width;
////        Camera.getInstance().viewportHeight = height;
//        Camera.getInstance().update();
//    }
//
//    @Override
//    public void pause() {
//        System.out.println("ElwynGraphicsLog:: ApplicationListener pause");
//    }
//
//    @Override
//    public void resume() {
//        System.out.println("ElwynGraphicsLog:: ApplicationListener resume");
//    }
//}
//

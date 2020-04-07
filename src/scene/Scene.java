package scene;

import audio.OpenALManager;
import entities.*;
import main.*;
import particles.ParticleManager;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private static Scene instance = null;

    /** ENTITIES **/
    private static ArrayList<GraphicEntity> listOfEntities;
    private static ArrayList<StaticGraphicEntity> listOfStaticEntities;
    private static ArrayList<GraphicEntity> listOfEntitiesToUpdate;
    private static int enemySpawnPeriod = 7500; // In Milliseconds
    private static long lastEnemySpawnTime;

    private static int renderDistance = 1000; //TODO This should depend on the Window and Camera parameters
    private static int updateDistance = 1250; //TODO This should depend on... what?

    private static Coordinates initialCoordinates;

    public static ArrayList<CircleAttack> listOfCircleAttacks;

    private Scene() {
        listOfEntities = new ArrayList<>();
        listOfStaticEntities = new ArrayList<>();
        listOfEntitiesToUpdate = new ArrayList<>();
        listOfCircleAttacks = new ArrayList<>();
        initialCoordinates = new Coordinates(2500, 2500);
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        OpenALManager.playSound(OpenALManager.SOUND_MUSIC_O1);
        updateAndSortEntities(timeElapsed);

        if (GameStatus.getStatus() != GameStatus.Status.RUNNING) {
            return;
        }
        updateEnemiesSpawn();
        for (int i = 0; i < listOfCircleAttacks.size(); i++) {
            listOfCircleAttacks.get(i).update(timeElapsed, true);
            if (listOfCircleAttacks.get(i).isDead()) {
                listOfCircleAttacks.remove(listOfCircleAttacks.get(i));
            }
        }
        ParticleManager.getInstance().updateParticles(timeElapsed);
    }

    private void updateEnemiesSpawn() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastEnemySpawnTime) > enemySpawnPeriod) {
            int distance = (int) ((Math.random() * 250) + 1500);
            double angle = Math.random() * 2 * Math.PI;
            int x = (int) ((Math.cos(angle) * distance) + Player.getInstance().getWorldCoordinates().x);
            int y = (int) ((Math.sin(angle) * distance) + Player.getInstance().getWorldCoordinates().y);
            Coordinates tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
            int i = (int) tileCoordinates.x;
            int j = (int) tileCoordinates.y;
            if (0 < i && i < TileMap.getArrayOfTiles().length
                    && 0 < j && j < TileMap.getArrayOfTiles()[0].length
                    && !TileMap.getArrayOfTiles()[i][j].isCollidable()) {
                new Enemy(x, y);
                lastEnemySpawnTime = currentTime;
            }
        }
    }

    private void updateAndSortEntities(long timeElapsed) {
        if (listOfEntities.isEmpty() || listOfEntitiesToUpdate == null) {
            return;
        }

        /** UPDATE ENTITIES **/
        listOfEntitiesToUpdate.clear();
        for (int i = 0; i < listOfEntities.size(); i++) {
            GraphicEntity currentEntity = listOfEntities.get(i);
            if (GameStatus.getStatus() == GameStatus.Status.RUNNING && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                currentEntity.update(timeElapsed);
            }
            currentEntity.updateCoordinates();

            if (MathUtils.module(Camera.getInstance().getCoordinates(), currentEntity.getWorldCoordinates()) < updateDistance) {
                listOfEntitiesToUpdate.add(currentEntity);
            }
        }

        /** SORT ENTITIES BY DEPTH (BUBBLE ALGORITHM) **/
        //TODO Replace Bubble Algorithm by Insertion Sort Algorithm or Quick Sort Algorithm to improve performance.
        int n = listOfEntitiesToUpdate.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                GraphicEntity entity1 = listOfEntitiesToUpdate.get(j + 1);
                GraphicEntity graphicEntity2 = listOfEntitiesToUpdate.get(j);
                if (entity1.getWorldCoordinates().y < graphicEntity2.getWorldCoordinates().y) {
                    listOfEntitiesToUpdate.set(j + 1, graphicEntity2);
                    listOfEntitiesToUpdate.set(j, entity1);
                }
            }
        }
    }

    public List<GraphicEntity> getListOfEntities() {
        return listOfEntities;
    }

    public List<StaticGraphicEntity> getListOfStaticEntities() {
        return listOfStaticEntities;
    }

    public List<GraphicEntity> getListOfEntitiesToUpdate() {
        return listOfEntitiesToUpdate;
    }

    public static Coordinates getInitialCoordinates() {
        return initialCoordinates;
    }

    public void initEntities() {
        for (int i = 0; i < getListOfEntities().size(); i++) {
            if (getListOfEntities().get(i) instanceof DynamicGraphicEntity) {
                getListOfEntities().remove(i);
                i--;
            }
        }
        Player.getInstance().reset();
        Camera.getInstance().reset();
        Scene.getInstance().getListOfEntities().add(Player.getInstance());
        listOfCircleAttacks.clear();
    }

    public void render() {
        /** COMPUTE WHICH ARE THE TILES WE ARE GOING TO RENDER **/
        int oneAxisDistance = (int) (renderDistance * Math.sin(Math.PI / 2));

        Coordinates topLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        Coordinates topLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topLeftWorldCoordinates.x, topLeftWorldCoordinates.y);

        Coordinates topRightWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x + oneAxisDistance, Camera.getInstance().getCoordinates().y - oneAxisDistance);
        Coordinates topRightTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topRightWorldCoordinates.x, topRightWorldCoordinates.y);

        Coordinates bottomLeftWorldCoordinates = new Coordinates(Camera.getInstance().getCoordinates().x - oneAxisDistance, Camera.getInstance().getCoordinates().y + oneAxisDistance);
        Coordinates bottomLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(bottomLeftWorldCoordinates.x, bottomLeftWorldCoordinates.y);

        /** FIRST LAYER OF TILES IS DRAWN FIRST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 0);

        /** SECOND LAYER IS DRAWN AT THE SAME TIME AS ENTITIES, BY ORDER OF DEPTH **/
        renderSecondLayerOfTilesAndEntities(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);

        /** THIRD AND LAST LAYER OF TILES IS DRAWN LAST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 2);

        /** COLLIDABLE TILES **/
        if ((GameMode.getGameMode() == GameMode.Mode.CREATIVE || Parameters.isDebugMode())) {
            renderCollidableTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);
        }
    }

    private void renderSecondLayerOfTilesAndEntities(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates) {
        GraphicEntity entity = null;
        int entityIterator = 0;
        int firstTileRowToDraw = (int) topLeftTileCoordinates.y;
        int lastTileRowToDraw = (int) bottomLeftTileCoordinates.y;
        int tileRowIterator = firstTileRowToDraw;
        while (tileRowIterator < lastTileRowToDraw) {
            if (entityIterator < listOfEntitiesToUpdate.size()) {
                entity = listOfEntitiesToUpdate.get(entityIterator);
            }
            if (entity != null && entity.getWorldCoordinates().y < Coordinates.tileCoordinatesToWorldCoordinates(0, tileRowIterator).y) {
                if (entity instanceof Tree) {
                    //System.out.println("AdriHell:: Tree at Tile Coordinates: " + ((Tree) entity).getTileCoordinates().toString());
                    //System.out.println("AdriHell:: Tree at World Coordinates: " + ((Tree) entity).getWorldCoordinates().toString());
                    //System.out.println("AdriHell:: Tree at Camera Coordinates: " + ((Tree) entity).getCameraCoordinates().toString());
                }
                entity.drawSprite((int) entity.getCameraCoordinates().x, (int) entity.getCameraCoordinates().y);
                entity = null;
                entityIterator++;
            } else {
                TileMap.bindTileSetTexture();
                OpenGLManager.glBegin(GL_QUADS);
                for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
                    int k = 1;
                    if (0 < i && i < TileMap.getArrayOfTiles().length
                            && 0 < tileRowIterator && tileRowIterator < TileMap.getArrayOfTiles()[0].length
                            && TileMap.getArrayOfTiles()[i][tileRowIterator].getLayerValue(k) != 0) {
                        double scale = Camera.getZoom();
                        int x = i * TileMap.TILE_WIDTH;
                        int y = tileRowIterator * TileMap.TILE_HEIGHT;
                        double distanceBetweenPlayerAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                        TileMap.drawTile(i, tileRowIterator, k, x, y, scale, (float) (renderDistance - distanceBetweenPlayerAndTile) / renderDistance);
                    }
                }
                glEnd();
                tileRowIterator++;
            }
        }
    }

    private void renderLayerOfTiles(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates, int layerToRender) {
        TileMap.bindTileSetTexture();
        OpenGLManager.glBegin(GL_QUADS);
        for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
            for (int j = (int) topLeftTileCoordinates.y; j < bottomLeftTileCoordinates.y; j++) {
                if (0 < i && i < TileMap.getArrayOfTiles().length
                        && 0 < j && j < TileMap.getArrayOfTiles()[0].length
                        && TileMap.getArrayOfTiles()[i][j].getLayerValue(layerToRender) != 0) {
                    double scale = Camera.getZoom();
                    int x = i * TileMap.TILE_WIDTH;
                    int y = j * TileMap.TILE_HEIGHT;
                    double distanceBetweenPlayerAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                    TileMap.drawTile(i, j, layerToRender, x, y, scale, (float) (renderDistance - distanceBetweenPlayerAndTile) / renderDistance);
                }
            }
        }
        glEnd();
    }

    private void renderCollidableTiles(Coordinates topLeftTileCoordinates, Coordinates topRightTileCoordinates, Coordinates bottomLeftTileCoordinates) {
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = (int) topLeftTileCoordinates.x; i < topRightTileCoordinates.x; i++) {
            for (int j = (int) topLeftTileCoordinates.y; j < bottomLeftTileCoordinates.y; j++) {
                if (0 < i && i < TileMap.getArrayOfTiles().length
                        && 0 < j && j < TileMap.getArrayOfTiles()[0].length) {
                    if (TileMap.getArrayOfTiles()[i][j].isCollidable()) {
                        double scale = Camera.getZoom();
                        int x = i * TileMap.TILE_WIDTH;
                        int y = j * TileMap.TILE_HEIGHT;
                        Coordinates cameraCoordinates = new Coordinates(x, y);
                        cameraCoordinates = cameraCoordinates.toCameraCoordinates();
                        int width = (int) (TileMap.TILE_WIDTH * scale);
                        int height = (int) (TileMap.TILE_HEIGHT * scale);
                        OpenGLManager.drawRectangle((int) cameraCoordinates.x, (int) cameraCoordinates.y, width, height, 0.5, 1f, 0f, 0f);
                    }
                }
            }
        }
        glEnd();
    }
}

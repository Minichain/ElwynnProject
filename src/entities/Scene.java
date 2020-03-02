package entities;

import audio.OpenALManager;
import main.*;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private static Scene instance = null;

    /** ENTITIES **/
    private static ArrayList<Entity> listOfEntities;
    private static ArrayList<Entity> listOfEntitiesToUpdate;
    private static int enemySpawnPeriod = 5000; // In Milliseconds
    private static long lastEnemySpawnTime;

    private static int renderDistance = 1000; //TODO This should depend on the Window and Camera parameters
    private static int updateDistance = 1250; //TODO This should depend on... what?

    private static Coordinates initialCoordinates;

    public static ArrayList<CircleAttack> listOfCircleAttacks;

    private Scene() {
        listOfEntities = new ArrayList<>();
        listOfEntitiesToUpdate = new ArrayList<>();
        listOfCircleAttacks = new ArrayList<>();
        initialCoordinates = new Coordinates(2500, 2500);
        loadWorld();
        loadSprites();
        OpenALManager.playSound(OpenALManager.SOUND_MUSIC_O1);
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }

    private void loadWorld() {
        TileMap.loadMap();
    }

    private void loadSprites() {
        TileMap.loadSprites();
    }

    public void update(long timeElapsed) {
        updateAndSortEntities(timeElapsed);
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
        if (listOfEntities.size() < 2) {
            new Enemy(2840, 2290);
        }
        /*
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastEnemySpawnTime) > enemySpawnPeriod) {
            int distance = (int) ((Math.random() * 250) + 1500);
            double angle = Math.random() * 2 * Math.PI;
            int x = (int) ((Math.cos(angle) * distance) + Character.getInstance().getCurrentCoordinates().x);
            int y = (int) ((Math.sin(angle) * distance) + Character.getInstance().getCurrentCoordinates().y);
            int[] tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
            int i = tileCoordinates[0];
            int j = tileCoordinates[1];
            if (0 < i && i < TileMap.getArrayOfTiles().length
                    && 0 < j && j < TileMap.getArrayOfTiles()[0].length
                    && !TileMap.getArrayOfTiles()[i][j].isCollidable()) {
                new Enemy(x, y);
                lastEnemySpawnTime = currentTime;
            }
        }
        */
    }

    private void updateAndSortEntities(long timeElapsed) {
        if (listOfEntities.isEmpty() || listOfEntitiesToUpdate == null) {
            return;
        }

        /** UPDATE ENTITIES **/
        listOfEntitiesToUpdate.clear();
        for (int i = 0; i < listOfEntities.size(); i++) {
            Entity currentEntity = listOfEntities.get(i);
            currentEntity.update(timeElapsed);

            if (MathUtils.module(Camera.getInstance().getCoordinates(), currentEntity.getCoordinates()) < updateDistance) {
                listOfEntitiesToUpdate.add(currentEntity);
            }
        }

        /** SORT ENTITIES BY DEPTH (BUBBLE ALGORITHM) **/
        //TODO Replace Bubble Algorithm by Insertion Sort Algorithm or Quick Sort Algorithm to improve performance.
        int n = listOfEntitiesToUpdate.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                Entity entity1 = listOfEntitiesToUpdate.get(j + 1);
                Entity entity2 = listOfEntitiesToUpdate.get(j);
                if (entity1.getCoordinates().y < entity2.getCoordinates().y) {
                    listOfEntitiesToUpdate.set(j + 1, entity2);
                    listOfEntitiesToUpdate.set(j, entity1);
                }
            }
        }
    }

    public List<Entity> getListOfEntities() {
        return listOfEntities;
    }

    public List<Entity> getListOfEntitiesToUpdate() {
        return listOfEntitiesToUpdate;
    }

    public static Coordinates getInitialCoordinates() {
        return initialCoordinates;
    }

    public void initEntities() {
        Scene.getInstance().getListOfEntities().clear();
        Character.getInstance().resetCharacter();
        Camera.getInstance().resetCamera();
        Scene.getInstance().getListOfEntities().add(Character.getInstance());
        listOfCircleAttacks.clear();
    }

    public void render() {
        /** COMPUTE WHICH ARE THE TILES WE ARE GOING TO RENDER **/
        int oneAxisDistance = (int) (renderDistance * Math.sin(Math.PI / 2));
        int[] cameraWorldCoordinates = new int[]{(int) Camera.getInstance().getCoordinates().x, (int) Camera.getInstance().getCoordinates().y};

        int[] topLeftWorldCoordinates = new int[]{cameraWorldCoordinates[0] - oneAxisDistance, cameraWorldCoordinates[1] - oneAxisDistance};
        int[] topLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topLeftWorldCoordinates[0], topLeftWorldCoordinates[1]);

        int[] topRightWorldCoordinates = new int[]{cameraWorldCoordinates[0] + oneAxisDistance, cameraWorldCoordinates[1] - oneAxisDistance};
        int[] topRightTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(topRightWorldCoordinates[0], topRightWorldCoordinates[1]);

        int[] bottomLeftWorldCoordinates = new int[]{cameraWorldCoordinates[0] - oneAxisDistance, cameraWorldCoordinates[1] + oneAxisDistance};
        int[] bottomLeftTileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(bottomLeftWorldCoordinates[0], bottomLeftWorldCoordinates[1]);

        /** FIRST LAYER OF TILES IS DRAWN FIRST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 0);

        /** SECOND LAYER IS DRAWN AT THE SAME TIME AS ENTITIES, BY ORDER OF DEPTH **/
        renderSecondLayerOfTilesAndEntities(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);

        /** THIRD AND LAST LAYER OF TILES IS DRAWN LAST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 2);
    }

    private void renderSecondLayerOfTilesAndEntities(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates) {
        double[] entityCameraCoordinates;
        Entity entity = null;
        int entityIterator = 0;
        int firstTileRowToDraw = topLeftTileCoordinates[1];
        int lastTileRowToDraw = bottomLeftTileCoordinates[1];
        int tileRowIterator = firstTileRowToDraw;
        while (tileRowIterator < lastTileRowToDraw) {
            if (entityIterator < listOfEntitiesToUpdate.size()) {
                entity = listOfEntitiesToUpdate.get(entityIterator);
            }
            if (entity != null && entity.getCoordinates().y < Coordinates.tileCoordinatesToWorldCoordinates(0, tileRowIterator)[1]) {
                entityCameraCoordinates = entity.getCoordinates().toCameraCoordinates();
                entity.drawSprite((int) entityCameraCoordinates[0], (int) entityCameraCoordinates[1], entity.getSpriteSheet());
                entity = null;
                entityIterator++;
            } else {
                TileMap.bindTileSetTexture();
                glBegin(GL_QUADS);
                for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
                    int k = 1;
                    if (0 < i && i < TileMap.getArrayOfTiles().length
                            && 0 < tileRowIterator && tileRowIterator < TileMap.getArrayOfTiles()[0].length
                            && TileMap.getArrayOfTiles()[i][tileRowIterator].getLayerValue(k) != 0) {
                        double scale = Camera.getZoom();
                        int x = i * TileMap.TILE_WIDTH;
                        int y = tileRowIterator * TileMap.TILE_HEIGHT;
                        double distanceBetweenCharacterAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                        TileMap.drawTile(i, tileRowIterator, k, x, y, scale, (float) (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                    }
                }
                glEnd();
                tileRowIterator++;
            }
        }
    }

    private void renderLayerOfTiles(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates, int layerToRender) {
        TileMap.bindTileSetTexture();
        glBegin(GL_QUADS);
        for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
            for (int j = topLeftTileCoordinates[1]; j < bottomLeftTileCoordinates[1]; j++) {
                if (TileMap.getArrayOfTiles()[i][j].getLayerValue(layerToRender) != 0) {
                    double scale = Camera.getZoom();
                    int x = i * TileMap.TILE_WIDTH;
                    int y = j * TileMap.TILE_HEIGHT;
                    double distanceBetweenCharacterAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                    TileMap.drawTile(i, j, layerToRender, x, y, scale, (float) (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                }
            }
        }
        glEnd();
    }
}

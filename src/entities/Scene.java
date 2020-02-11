package entities;

import main.*;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private static Scene instance = null;

    /** ENTITIES **/
    private static List<Entity> listOfEntitiesToUpdate;
    private static int enemySpawnPeriod = 1000; // In Milliseconds
    private static long lastEnemySpawnTime;

    /** TILES **/
    private static List<Entity> listOfEntities;
    private static byte[][][] arrayOfTiles;
    private static int numOfHorizontalTiles = 1000;
    private static int numOfVerticalTiles = 1000;
    private static int tileLayers = 4;
    private static Texture tileSet;
    private static int tileWidth = 16;
    private static int tileHeight = 16;

    private static int renderDistance = 1000; //TODO This should depend on the Window and Camera parameters
    private static int updateDistance = 1250; //TODO This should depend on... what?

    private static Coordinates initialCoordinates;

    private Scene() {
        listOfEntities = new ArrayList<>();
        listOfEntitiesToUpdate = new ArrayList<>();
        initialCoordinates = new Coordinates(2500, 2500);
        loadWorld();
        loadSprites();
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }

    private void loadWorld() {
        arrayOfTiles = WorldLoader.loadWorld("world");
        if (arrayOfTiles == null || arrayOfTiles.length == 0) {
            arrayOfTiles = new byte[numOfHorizontalTiles][numOfVerticalTiles][tileLayers];
            for (int i = 0; i < numOfHorizontalTiles; i++) {
                for (int j = 0; j < numOfVerticalTiles; j++) {
                    arrayOfTiles[i][j][0] = (byte) (((Math.random() * 100) % 4) + 1); //Layer 1
                    arrayOfTiles[i][j][1] = (byte) 0;  //Layer 2
                    arrayOfTiles[i][j][2] = (byte) 0;  //Layer 3
                    arrayOfTiles[i][j][3] = (byte) 0;  //Collision layer. 0 -> NO COLLISION, 1 -> COLLISION
                }
            }
        }
    }

    private void loadSprites() {
        String path;
        path = "res/sprites/tiles/tileset.png";
        tileSet = Texture.loadTexture(path);
    }

    public void bindTileSetTexture() {
        tileSet.bind();
    }

    public void drawTile(int i, int j, int k, int x, int y, double scale, float distanceFactor) {
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length) {
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE && arrayOfTiles[i][j][tileLayers - 1] == (byte) 1) { // COLLISION Tile
                drawTile(arrayOfTiles[i][j][k], x, y, scale, 1f, 0.5f, 0.5f, false); // Draw the tile more red
            } else {
                drawTile(arrayOfTiles[i][j][k], x, y, scale, distanceFactor, distanceFactor, distanceFactor, false);
            }
        }
    }

    public void drawTile(int tileType, int x, int y, double scale, float r, float g, float b, boolean isCameraCoordinates) {
        double[] cameraCoordinates = new double[]{x, y};
        if (!isCameraCoordinates) cameraCoordinates = (new Coordinates(x, y)).toCameraCoordinates();

        int numOfTilesInTileSetX = tileSet.getWidth() / tileWidth;
        int numOfTilesInTileSetY = tileSet.getHeight() / tileHeight;
        int[] tileFromTileSet = getTile(tileType);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];

        double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
        double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
        double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
        double v2 = v + (1.0 / (float) numOfTilesInTileSetY);

        MyOpenGL.drawTexture((int) cameraCoordinates[0], (int) cameraCoordinates[1], u, v2, u2, v, (int) (tileWidth * scale), (int) (tileHeight * scale), r, g, b);
    }

    public int[] getTile(int tile) {
        int x = tileSet.getWidth() / tileWidth;
        int y = tileSet.getHeight() / tileHeight;
        tile %= (x * y);
        return new int[]{tile % x, y - 1 - (tile / x)};
    }

    public void setTile(int i, int j, int k, byte value) {
        int x = tileSet.getWidth() / tileWidth;
        int y = tileSet.getHeight() / tileHeight;
        value %= (x * y);
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length) {
            arrayOfTiles[i][j][k] = value;
        }
    }

    public static int getTileWidth() {
        return tileWidth;
    }

    public static int getTileHeight() {
        return tileHeight;
    }

    public static int getNumOfHorizontalTiles() {
        return numOfHorizontalTiles;
    }

    public static int getNumOfVerticalTiles() {
        return numOfVerticalTiles;
    }

    public static int getNumOfTileLayers() {
        return tileLayers;
    }

    public void update(long timeElapsed) {
        updateAndSortEntities(timeElapsed);
        updateEnemiesSpawn();
    }

    private void updateEnemiesSpawn() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastEnemySpawnTime) > enemySpawnPeriod) {
            int distance = (int) ((Math.random() * 250) + 1500);
            double angle = Math.random() * 2 * Math.PI;
            int x = (int) ((Math.cos(angle) * distance) + Character.getInstance().getCurrentCoordinates().x);
            int y = (int) ((Math.sin(angle) * distance) + Character.getInstance().getCurrentCoordinates().y);
            int[] tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
            int i = tileCoordinates[0];
            int j = tileCoordinates[1];
            if (0 < i && i < arrayOfTiles.length
                    && 0 < j && j < arrayOfTiles[0].length
                    && arrayOfTiles[i][j][tileLayers - 1] == 0) {
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
            Entity currentEntity = listOfEntities.get(i);
            if (currentEntity instanceof Character) {
                ((Character) currentEntity).update(timeElapsed);
            } else if (currentEntity instanceof Enemy) {
                ((Enemy) currentEntity).update(timeElapsed);
            }

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
                Scene.getInstance().bindTileSetTexture();
                glBegin(GL_QUADS);
                for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
                    int k = 1;
                    if (0 < i && i < arrayOfTiles.length
                            && 0 < tileRowIterator && tileRowIterator < arrayOfTiles[0].length
                            && arrayOfTiles[i][tileRowIterator][k] != 0) {
                        double scale = Camera.getZoom();
                        int x = i * tileWidth;
                        int y = tileRowIterator * tileHeight;
                        double distanceBetweenCharacterAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                        drawTile(i, tileRowIterator, k, x, y, scale, (float) (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                    }
                }
                glEnd();
                tileRowIterator++;
            }
        }
    }

    private void renderLayerOfTiles(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates, int layerToRender) {
        Scene.getInstance().bindTileSetTexture();
        glBegin(GL_QUADS);
        for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
            for (int j = topLeftTileCoordinates[1]; j < bottomLeftTileCoordinates[1]; j++) {
                double scale = Camera.getZoom();
                int x = i * tileWidth;
                int y = j * tileHeight;
                double distanceBetweenCharacterAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                drawTile(i, j, layerToRender, x, y, scale, (float) (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
            }
        }
        glEnd();
    }

    public static byte[][][] getArrayOfTiles() {
        return arrayOfTiles;
    }

    public static boolean checkCollisionWithTile(int x, int y) {
        int[] tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
        int i = tileCoordinates[0];
        int j = tileCoordinates[1];
        int k = Scene.getNumOfTileLayers() - 1;
        byte[][][] arrayOfTiles = getArrayOfTiles();
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length) {
            return (arrayOfTiles[i][j][k] == (byte) 1);
        } else {
            return false;
        }
    }
}

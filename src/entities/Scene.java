package entities;

import main.Coordinates;
import main.GameMode;
import main.MyOpenGL;
import main.Texture;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private static Scene instance = null;
    private static List<Entity> listOfEntities;
    private static List<Entity> listOfEntitiesToUpdate;
    private static byte[][][] arrayOfTiles;
    private static int numOfHorizontalTiles = 1000;
    private static int numOfVerticalTiles = 1000;
    private static int tileLayers = 4;
    private static Texture tileSet;
    private static int tileWidth = 16;
    private static int tileHeight = 16;
    private static double zoom = 2;
    private static int renderDistance = 1750; //TODO This should depend on the Window and Camera parameters
    private static int updateDistance = 2000; //TODO This should depend on... what?

    private Scene() {
        listOfEntities = new ArrayList<>();
        listOfEntitiesToUpdate = new ArrayList<>();
        arrayOfTiles = new byte[numOfHorizontalTiles][numOfVerticalTiles][tileLayers];
        for (int i = 0; i < numOfHorizontalTiles; i++) {
            for (int j = 0; j < numOfVerticalTiles; j++) {
                arrayOfTiles[i][j][0] = (byte)((Math.random() * 100) % 4); //Layer 1
                arrayOfTiles[i][j][1] = (byte) -1;  //Layer 2
                arrayOfTiles[i][j][2] = (byte) -1;  //Layer 3
                arrayOfTiles[i][j][3] = (byte) 0;   //Collision layer. 0 -> NO COLLISION, 1 -> COLLISION
            }
        }
        loadSprites();
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
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
            if (GameMode.getInstance().getGameMode() == GameMode.Mode.CREATIVE && arrayOfTiles[i][j][tileLayers - 1] == (byte) 1) { // COLLISION Tile
                drawTile(arrayOfTiles[i][j][k], x, y, scale, 1f, 0.5f, 0.5f, false); // Draw the tile more red
            } else {
                drawTile(arrayOfTiles[i][j][k], x, y, scale, distanceFactor, distanceFactor, distanceFactor, false);
            }
        }
    }

    public void drawTile(int tileType, int x, int y, double scale, float r, float g, float b, boolean isLocalCoordinates) {
        double[] localCoordinates = new double[]{x, y};
        if (!isLocalCoordinates) localCoordinates = (new Coordinates(x, y)).toLocalCoordinates();

        int numOfTilesInTileSetX = tileSet.getWidth() / tileWidth;
        int numOfTilesInTileSetY = tileSet.getHeight() / tileHeight;
        int[] tileFromTileSet = getTile(tileType);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];

        double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
        double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
        double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
        double v2 = v + (1.0 / (float) numOfTilesInTileSetY);

        MyOpenGL.drawTexture((int) localCoordinates[0], (int) localCoordinates[1], u, v2, u2, v, (int) (tileWidth * scale), (int) (tileHeight * scale), r, g, b);
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

    public void initEntities() {
        Scene.getInstance().getListOfEntities().clear();
        Character.getInstance().resetCharacter();
        Camera.getInstance().resetCamera();
        Scene.getInstance().getListOfEntities().add(Character.getInstance());
    }

    public static double getZoom() {
        return zoom;
    }

    public void render() {
        /** COMPUTE WHICH ARE THE TILES WE ARE GOING TO RENDER **/
        int oneAxisDistance = (int) (renderDistance * Math.sin(Math.PI / 2));
        int[] cameraGlobalCoordinates = new int[]{(int) Camera.getInstance().getCoordinates().x, (int) Camera.getInstance().getCoordinates().y};

        int[] topLeftGlobalCoordinates = new int[]{cameraGlobalCoordinates[0] - oneAxisDistance, cameraGlobalCoordinates[1] - oneAxisDistance};
        int[] topLeftTileCoordinates = Coordinates.globalCoordinatesToTileCoordinates(topLeftGlobalCoordinates[0], topLeftGlobalCoordinates[1]);

        int[] topRightGlobalCoordinates = new int[]{cameraGlobalCoordinates[0] + oneAxisDistance, cameraGlobalCoordinates[1] - oneAxisDistance};
        int[] topRightTileCoordinates = Coordinates.globalCoordinatesToTileCoordinates(topRightGlobalCoordinates[0], topRightGlobalCoordinates[1]);

        int[] bottomLeftGlobalCoordinates = new int[]{cameraGlobalCoordinates[0] - oneAxisDistance, cameraGlobalCoordinates[1] + oneAxisDistance};
        int[] bottomLeftTileCoordinates = Coordinates.globalCoordinatesToTileCoordinates(bottomLeftGlobalCoordinates[0], bottomLeftGlobalCoordinates[1]);

        /** FIRST LAYER OF TILES IS DRAWN FIRST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 0);

        /** SECOND LAYER IS DRAWN AT THE SAME TIME AS ENTITIES, BY ORDER OF DEPTH **/
        renderSecondLayerOfTilesAndEntities(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates);

        /** THIRD AND LAST LAYER OF TILES IS DRAWN LAST **/
        renderLayerOfTiles(topLeftTileCoordinates, topRightTileCoordinates, bottomLeftTileCoordinates, 2);
    }

    private void renderSecondLayerOfTilesAndEntities(int[] topLeftTileCoordinates, int[] topRightTileCoordinates, int[] bottomLeftTileCoordinates) {
        double[] entityLocalCoordinates;
        Entity entity = null;
        int entityIterator = 0;
        int firstTileRowToDraw = topLeftTileCoordinates[1];
        int lastTileRowToDraw = bottomLeftTileCoordinates[1];
        int tileRowIterator = firstTileRowToDraw;
        while (tileRowIterator < lastTileRowToDraw) {
            if (entityIterator < listOfEntitiesToUpdate.size()) {
                entity = listOfEntitiesToUpdate.get(entityIterator);
            }
            if (entity != null && entity.getCoordinates().y < Coordinates.tileCoordinatesToGlobalCoordinates(0, tileRowIterator)[1]) {
                entityLocalCoordinates = entity.getCoordinates().toLocalCoordinates();
                entity.drawSprite((int) entityLocalCoordinates[0], (int) entityLocalCoordinates[1], entity.getSpriteSheet());
                entity = null;
                entityIterator++;
            } else {
                Scene.getInstance().bindTileSetTexture();
                glBegin(GL_QUADS);
                for (int i = topLeftTileCoordinates[0]; i < topRightTileCoordinates[0]; i++) {
                    int k = 1;
                    if (0 < i && i < arrayOfTiles.length
                            && 0 < tileRowIterator && tileRowIterator < arrayOfTiles[0].length
                            && arrayOfTiles[i][tileRowIterator][k] != -1) {
                        double scale = zoom;
                        int x = (i * (int) (tileWidth * scale));
                        int y = (tileRowIterator * (int) (tileHeight * scale));
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
                double scale = zoom;
                int x = (i * (int) (tileWidth * scale));
                int y = (j * (int) (tileHeight * scale));
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
        int[] tileCoordinates = Coordinates.globalCoordinatesToTileCoordinates(x, y);
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

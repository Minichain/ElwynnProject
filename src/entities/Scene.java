package entities;

import main.Coordinates;
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
    private static byte[][] arrayOfTiles;
    private static int numOfHorizontalTiles = 1000;
    private static int numOfVerticalTiles = 1000;
    private static Texture tileSet;
    private static int tileWidth = 16;
    private static int tileHeight = 16;
    private static double zoom = 2;
    private static int renderDistance = 1750; //TODO This should depend on the Window and Camera parameters
    private static int updateDistance = 2000; //TODO This should depend on... what?

    private Scene() {
        listOfEntities = new ArrayList<>();
        listOfEntitiesToUpdate = new ArrayList<>();
        arrayOfTiles = new byte[numOfHorizontalTiles][numOfVerticalTiles];
        for (int i = 0; i < numOfHorizontalTiles; i++) {
            for (int j = 0; j < numOfVerticalTiles; j++) {
                arrayOfTiles[i][j] = (byte)((Math.random() * 100) % 4);
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

    public void drawTile(int i, int j, int x, int y, double scale, double alpha) {
        drawTile(arrayOfTiles[i][j], x, y, scale, alpha, false);
    }

    public void drawTile(int tileType, int x, int y, double scale, double alpha, boolean isLocalCoordinates) {
        double[] localCoordinates;
        if (isLocalCoordinates) {
            localCoordinates = new double[]{x, y};
        } else {
            localCoordinates = (new Coordinates(x, y)).toLocalCoordinates();
        }
        int numOfTilesInTileSetX = tileSet.getWidth() / tileWidth;
        int numOfTilesInTileSetY = tileSet.getHeight() / tileHeight;
        int[] tileFromTileSet = getTile(tileType);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];
        double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
        double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
        double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
        double v2 = v + (1.0 / (float) numOfTilesInTileSetY);
        MyOpenGL.drawTexture((int) localCoordinates[0], (int) localCoordinates[1], u, v2, u2, v, (int) (tileWidth * scale), (int) (tileHeight * scale), alpha);
    }

    public int[] getTile(int tile) {
        int x = tileSet.getWidth() / tileWidth;
        int y = tileSet.getHeight() / tileHeight;
        tile %= (x * y);
        return new int[]{tile % x, y - 1 - (tile / y)};
    }

    public void setTile(int x, int y, byte value) {
        value %= 64;
        if (0 < x && x < arrayOfTiles.length
                && 0 < y && y < arrayOfTiles[0].length) {
            arrayOfTiles[x][y] = value;
        }
    }

    public static int getTileWidth() {
        return tileWidth;
    }

    public static int getTileHeight() {
        return tileHeight;
    }

    public int getNumOfHorizontalTiles() {
        return numOfHorizontalTiles;
    }

    public int getNumOfVerticalTiles() {
        return numOfVerticalTiles;
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

        //TODO add Insertion Sort Algorithm
        //TODO add Quick Sort Algorithm
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
        /** SCENE BACKGROUND IS DRAWN FIRST **/
        renderSceneBackground();

        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
        renderEntities();
    }

    private void renderSceneBackground() {
        Scene.getInstance().bindTileSetTexture();
        glBegin(GL_QUADS);
        for (int i = 0; i < Scene.getInstance().getNumOfHorizontalTiles(); i++) {
            for (int j = 0; j < Scene.getInstance().getNumOfVerticalTiles(); j++) {
                double scale = zoom;
                int x = (i * (int) (tileWidth * scale));
                int y = (j * (int) (tileHeight * scale));
                double distanceBetweenCharacterAndTile = MathUtils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y));
                if (distanceBetweenCharacterAndTile < renderDistance) {
                    drawTile(i, j, x, y, scale, (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                }
            }
        }
        glEnd();
    }

    private void renderEntities() {
        double[] localCoordinates;
        Entity entity;
        for (int i = 0; i < listOfEntitiesToUpdate.size(); i++) {
            entity = listOfEntitiesToUpdate.get(i);
            if (MathUtils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
                localCoordinates = entity.getCoordinates().toLocalCoordinates();
                entity.drawSprite((int) localCoordinates[0], (int) localCoordinates[1], entity.getSpriteSheet());
            }
        }
    }

    public byte[][] getArrayOfTiles() {
        return arrayOfTiles;
    }
}

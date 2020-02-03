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
    private static byte[][] arrayOfTiles;
    private static int numOfHorizontalTiles;
    private static int numOfVerticalTiles;
    private static Texture tileSet;
    private static int tileWidth = 16;
    private static int tileHeight = 16;
    private static double zoom = 2;
    private static int renderDistance;

    private Scene() {
        listOfEntities = new ArrayList<>();
        renderDistance = 1500;  //TODO This should depend on the Window and Camera parameters
        numOfHorizontalTiles = 1000;
        numOfVerticalTiles = 1000;
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

    private void drawTile(int i, int j, int x, int y, double scale, double alpha) {
        drawTile(arrayOfTiles[i][j], x, y, scale, alpha);
    }

    private void drawTile(int tileType, int x, int y, double scale, double alpha) {
        double[] localCoordinates = (new Coordinates(x, y)).toLocalCoordinates();
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
        int[] tileFromTileSet;
        switch (tile) {
            case 0:
                tileFromTileSet = new int[]{0, 3};
                return tileFromTileSet;
            case 1:
                tileFromTileSet = new int[]{1, 3};
                return tileFromTileSet;
            case 2:
                tileFromTileSet = new int[]{2, 3};
                return tileFromTileSet;
            case 3:
                tileFromTileSet = new int[]{3, 3};
                return tileFromTileSet;
            case 4:
                tileFromTileSet = new int[]{0, 2};
                return tileFromTileSet;
            case 5:
                tileFromTileSet = new int[]{1, 2};
                return tileFromTileSet;
            case 6:
                tileFromTileSet = new int[]{2, 2};
                return tileFromTileSet;
            case 7:
                tileFromTileSet = new int[]{3, 2};
                return tileFromTileSet;
            case 8:
                tileFromTileSet = new int[]{0, 1};
                return tileFromTileSet;
            case 9:
                tileFromTileSet = new int[]{1, 1};
                return tileFromTileSet;
            case 10:
                tileFromTileSet = new int[]{2, 1};
                return tileFromTileSet;
            case 11:
            default:
                tileFromTileSet = new int[]{3, 1};
                return tileFromTileSet;
        }
    }

    public void setTile(int x, int y, byte value) {
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
        sortAndUpdateEntities(timeElapsed);
    }

    private void sortAndUpdateEntities(long timeElapsed) {
        if (listOfEntities.isEmpty()) {
            return;
        }

        /** SORT ENTITIES (BUBBLE ALGORITHM) **/
        int n = listOfEntities.size() - 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n - i); j++) {
                Entity entity1 = listOfEntities.get(j + 1);
                Entity entity2 = listOfEntities.get(j);
                if (entity1.getCoordinates().y < entity2.getCoordinates().y) {
                    listOfEntities.set(j + 1, entity2);
                    listOfEntities.set(j, entity1);
                }
            }
        }

        //TODO add Insertion Sort Algorithm
        //TODO add Quick Sort Algorithm

        /** UPDATE ENTITIES **/
        for (int i = 0; i < listOfEntities.size(); i++) {
            Entity currentEntity = listOfEntities.get(i);
            if (currentEntity instanceof Character) {
                ((Character) currentEntity).update(timeElapsed);
            } else if (currentEntity instanceof Enemy) {
                ((Enemy) currentEntity).update(timeElapsed);
            }
        }
    }

    public List<Entity> getListOfEntities() {
        return listOfEntities;
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
                    Scene.getInstance().drawTile(i, j, x, y, scale, (renderDistance - distanceBetweenCharacterAndTile) / renderDistance);
                }
            }
        }
        glEnd();
    }

    private void renderEntities() {
        double[] localCoordinates;
        Entity entity;
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        for (int i = 0; i < listOfEntities.size(); i++) {
            entity = listOfEntities.get(i);
            if (MathUtils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
                localCoordinates = entity.getCoordinates().toLocalCoordinates();
                entity.drawSprite((int) localCoordinates[0], (int) localCoordinates[1]);
            }
        }
    }

    public byte[][] getArrayOfTiles() {
        return arrayOfTiles;
    }
}

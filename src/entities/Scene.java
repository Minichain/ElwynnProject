package entities;

import main.Coordinates;
import main.MyOpenGL;
import main.Texture;
import main.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private static Scene instance = null;
    private static List<Entity> listOfEntities = new ArrayList<>();
    private static byte[][] arrayOfTiles;
    private int sceneX;
    private int sceneY;
    private static Texture tileSet;
    private static int tileWidth = 16;
    private static int tileHeight = 16;
    private static double zoom = 2;

    private Scene() {
        sceneX = 1000;
        sceneY = 1000;
        arrayOfTiles = new byte[sceneX][sceneY];
        for (int i = 0; i < sceneX; i++) {
            for (int j = 0; j < sceneY; j++) {
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
        double[] localCoordinates = (new Coordinates(x, y)).toLocalCoordinates();
        int numOfTilesInTileSetX = tileSet.getWidth() / tileWidth;
        int numOfTilesInTileSetY = tileSet.getHeight() / tileHeight;
        int[] tileFromTileSet = getTile(arrayOfTiles[i][j]);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];
        double u = ((1.0 / (float) numOfTilesInTileSetX)) * tileFromTileSetX;
        double v = ((1.0 / (float) numOfTilesInTileSetY)) * tileFromTileSetY;
        double u2 = u + (1.0 / (float) numOfTilesInTileSetX);
        double v2 = v + (1.0 / (float) numOfTilesInTileSetY);
        MyOpenGL.drawTextureAlpha((int) localCoordinates[0], (int) localCoordinates[1], u, v2, u2, v, (int) (tileWidth * scale), (int) (tileHeight * scale), alpha);
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

    public int getSceneX() {
        return sceneX;
    }

    public int getSceneY() {
        return sceneY;
    }

    public void sortListOfEntitiesByDepth() {
        int numberOfTrees = listOfEntities.size();
        if (numberOfTrees <= 1) {
            return;
        }

        List<Entity> tempListOfTrees = new ArrayList<>();
        Entity minDepthTree =  null;
        for (int i = 0; i < numberOfTrees; i++) {
            double minDepth = 10000;
            for (int j = 0; j < listOfEntities.size(); j++) {
                if (listOfEntities.get(j).getCoordinates().y < minDepth) {
                    minDepth = listOfEntities.get(j).getCoordinates().y;
                    minDepthTree = listOfEntities.get(j);
                }
            }
            if (minDepthTree != null) {
                listOfEntities.remove(minDepthTree);
                tempListOfTrees.add(minDepthTree);
            }
        }
        listOfEntities = tempListOfTrees;
    }

    public List<Entity> getListOfEntities() {
        sortListOfEntitiesByDepth();
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
        int renderDistance = 1000;  //TODO This should depend on the Window and Camera parameters
        double[] localCoordinates;

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        Scene.getInstance().bindTileSetTexture();
        glBegin(GL_QUADS);
        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
                double scale = zoom;
                int x = (i * (int) (tileWidth * scale));
                int y = (j * (int) (tileHeight * scale));
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

    public byte[][] getArrayOfTiles() {
        return arrayOfTiles;
    }
}

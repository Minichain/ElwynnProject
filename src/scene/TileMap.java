package scene;

import entities.Sprite;
import entities.SpriteManager;
import main.*;

public class TileMap {
    /** TILES **/
    private static Tile[][] arrayOfTiles;
    private static int numOfHorizontalTiles = 0;
    private static int numOfVerticalTiles = 0;

    public static int TILE_WIDTH = 8;
    public static int TILE_HEIGHT = 8;

    public static Sprite getTileSet() {
        return SpriteManager.getInstance().TILESET;
    }

    public static void bindTileSetTexture() {
        getTileSet().getSpriteSheet().bind();
    }

    public static Tile[][] getArrayOfTiles() {
        return arrayOfTiles;
    }

    public static void setArrayOfTiles(Tile[][] aot) {
        if (aot == null || aot.length == 0) {
            arrayOfTiles = new Tile[numOfHorizontalTiles][numOfVerticalTiles];
            for (int i = 0; i < numOfHorizontalTiles; i++) {
                for (int j = 0; j < numOfVerticalTiles; j++) {
                    arrayOfTiles[i][j] = new Tile();
                    arrayOfTiles[i][j].setLayerValue(0, (byte) (((Math.random() * 100) % 3) + 1));
                }
            }
        } else {
            arrayOfTiles = aot;
        }
    }

    public static int getNumOfHorizontalTiles() {
        return numOfHorizontalTiles;
    }

    public static void setNumOfHorizontalTiles(int numOfHorizontalTiles) {
        TileMap.numOfHorizontalTiles = numOfHorizontalTiles;
    }

    public static int getNumOfVerticalTiles() {
        return numOfVerticalTiles;
    }

    public static void setNumOfVerticalTiles(int numOfVerticalTiles) {
        TileMap.numOfVerticalTiles = numOfVerticalTiles;
    }

    public static void drawTile(int i, int j, int k, int x, int y, double scale) {
        if (0 <= i && i < TileMap.getArrayOfTiles().length && 0 <= j && j < TileMap.getArrayOfTiles()[0].length) {
            drawTile(TileMap.getArrayOfTiles()[i][j].getLayerValue(k), x, y, scale, false);
        }
    }

    public static void drawTile(int tileType, int x, int y, double scale) {
        drawTile(tileType, x, y, scale, 1f, false);
    }

    public static void drawTile(int tileType, int x, int y, double scale, boolean isCameraCoordinates) {
        drawTile(tileType, x, y, scale, 1f, isCameraCoordinates);
    }

    public static void drawTile(int tileType, int x, int y, double scale, float lightIntensity, boolean isCameraCoordinates) {
        drawTile(tileType, x, y, scale, lightIntensity, lightIntensity, lightIntensity, isCameraCoordinates);
    }

    public static void drawTile(int tileType, double x, double y, double scale, float r, float g, float b, boolean isCameraCoordinates) {
        Coordinates cameraCoordinates = new Coordinates(x, y);
        if (!isCameraCoordinates) cameraCoordinates = cameraCoordinates.toCameraCoordinates();

        int[] tileFromTileSet = getTile(tileType);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];

        float u = ((1f / (float) getTileSet().TILES_IN_TILESET_X_AXIS)) * tileFromTileSetX;
        float v = ((1f / (float) getTileSet().TILES_IN_TILESET_Y_AXIS)) * tileFromTileSetY;
        float u2 = u + (1f / (float) getTileSet().TILES_IN_TILESET_X_AXIS);
        float v2 = v + (1f / (float) getTileSet().TILES_IN_TILESET_Y_AXIS);

        int width = (int) (TILE_WIDTH * scale);
        int height = (int) (TILE_HEIGHT * scale);

        // To prevent gaps between tiles when the zoom is not an integer multiple of 2
        if (Camera.getZoom() % 2 != 0) {
            width++;
            height++;
        }

        OpenGLManager.drawTexture((int) cameraCoordinates.x, (int) cameraCoordinates.y, u, v2, u2, v, width, height, r, g, b);
    }

    public static int[] getTile(int tile) {
        int x = getTileSet().TILES_IN_TILESET_X_AXIS;
        int y = getTileSet().TILES_IN_TILESET_Y_AXIS;
        tile %= (x * y);
        return new int[]{tile % x, y - 1 - (tile / x)};
    }

    public static void setTile(int i, int j, int k, byte value) {
        int x = getTileSet().TILES_IN_TILESET_X_AXIS;
        int y = getTileSet().TILES_IN_TILESET_Y_AXIS;
        value %= (x * y);
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length) {
            arrayOfTiles[i][j].setLayerValue(k, value);
        }
    }

    public static boolean checkCollisionWithTile(int x, int y) {
        Coordinates tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
        int i = (int) tileCoordinates.x, j = (int) tileCoordinates.y;
        if (0 < i && i < getArrayOfTiles().length && 0 < j && j < getArrayOfTiles()[0].length && getArrayOfTiles()[i][j] != null) {
            return getArrayOfTiles()[i][j].isCollidable();
        } else {
            return false;
        }
    }
}

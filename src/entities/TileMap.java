package entities;

import main.*;

public class TileMap {
    /** TILES **/
    private static Tile[][] arrayOfTiles;
    private static int numOfHorizontalTiles = 1000;
    private static int numOfVerticalTiles = 1000;
    private static Texture tileSet;

    public static int TILE_WIDTH = 16;
    public static int TILE_HEIGHT = 16;
    public static int TILES_IN_TILESET_X_AXIS;
    public static int TILES_IN_TILESET_Y_AXIS;

    public static void loadMap() {
        arrayOfTiles = WorldLoader.loadWorld();
        if (arrayOfTiles == null || arrayOfTiles.length == 0) {
            arrayOfTiles = new Tile[numOfHorizontalTiles][numOfVerticalTiles];
            for (int i = 0; i < numOfHorizontalTiles; i++) {
                for (int j = 0; j < numOfVerticalTiles; j++) {
                    arrayOfTiles[i][j] = new Tile();
                    arrayOfTiles[i][j].setLayerValue(0, (byte) (((Math.random() * 100) % 4) + 1));
                }
            }
        }
    }

    public static void loadSprites() {
        String path;
        path = "res/sprites/tiles/tileset.png";
        tileSet = Texture.loadTexture(path);
        TILES_IN_TILESET_X_AXIS = tileSet.getWidth() / TILE_WIDTH;
        TILES_IN_TILESET_Y_AXIS = tileSet.getHeight() / TILE_HEIGHT;
    }

    public static void bindTileSetTexture() {
        tileSet.bind();
    }

    public static Tile[][] getArrayOfTiles() {
        return arrayOfTiles;
    }

    public static int getNumOfHorizontalTiles() {
        return numOfHorizontalTiles;
    }

    public static int getNumOfVerticalTiles() {
        return numOfVerticalTiles;
    }

    public static void drawTile(int i, int j, int k, int x, int y, double scale, float distanceFactor) {
        if (0 < i && i < TileMap.getArrayOfTiles().length && 0 < j && j < TileMap.getArrayOfTiles()[0].length) {
            if (GameMode.getGameMode() == GameMode.Mode.CREATIVE && TileMap.getArrayOfTiles()[i][j].isCollidable()) { // COLLISION Tile
                drawTile(TileMap.getArrayOfTiles()[i][j].getLayerValue(k), x, y, scale, 1f, 0.5f, 0.5f, false); // Draw the tile more red
            } else {
                drawTile(TileMap.getArrayOfTiles()[i][j].getLayerValue(k), x, y, scale, distanceFactor, false);
            }
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

    public static void drawTile(int tileType, int x, int y, double scale, float r, float g, float b, boolean isCameraCoordinates) {
        double[] cameraCoordinates = new double[]{x, y};
        if (!isCameraCoordinates) cameraCoordinates = (new Coordinates(x, y)).toCameraCoordinates();

        int[] tileFromTileSet = getTile(tileType);
        int tileFromTileSetX = tileFromTileSet[0];
        int tileFromTileSetY = tileFromTileSet[1];

        double u = ((1.0 / (double) TILES_IN_TILESET_X_AXIS)) * tileFromTileSetX;
        double v = ((1.0 / (double) TILES_IN_TILESET_Y_AXIS)) * tileFromTileSetY;
        double u2 = u + (1.0 / (double) TILES_IN_TILESET_X_AXIS);
        double v2 = v + (1.0 / (double) TILES_IN_TILESET_Y_AXIS);

        int width = (int) (TILE_WIDTH * scale);
        int height = (int) (TILE_HEIGHT * scale);

        // To prevent gaps between tiles when the zoom is not an integer multiple of 2
        if (Camera.getZoom() % 2 != 0) {
            width++;
            height++;
        }

        MyOpenGL.drawTexture((int) cameraCoordinates[0], (int) cameraCoordinates[1], u, v2, u2, v, width, height, r, g, b);
    }

    public static int[] getTile(int tile) {
        int x = TILES_IN_TILESET_X_AXIS;
        int y = TILES_IN_TILESET_Y_AXIS;
        tile %= (x * y);
        return new int[]{tile % x, y - 1 - (tile / x)};
    }

    public static void setTile(int i, int j, int k, byte value) {
        int x = TILES_IN_TILESET_X_AXIS;
        int y = TILES_IN_TILESET_Y_AXIS;
        value %= (x * y);
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length) {
            arrayOfTiles[i][j].setLayerValue(k, value);
        }
    }

    public static boolean checkCollisionWithTile(int x, int y) {
        int[] tileCoordinates = Coordinates.worldCoordinatesToTileCoordinates(x, y);
        int i = tileCoordinates[0];
        int j = tileCoordinates[1];
        Tile[][] arrayOfTiles = getArrayOfTiles();
        if (0 < i && i < arrayOfTiles.length && 0 < j && j < arrayOfTiles[0].length && arrayOfTiles[i][j] != null) {
            return arrayOfTiles[i][j].isCollidable();
        } else {
            return false;
        }
    }
}

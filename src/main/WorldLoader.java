package main;

import scene.Tile;
import scene.TileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldLoader {

    public static void saveWorld() {
        if (saveWorld(TileMap.getArrayOfTiles())) {
            System.out.println("World saved successfully");
        } else {
            System.err.println("World could not be saved");
        }
    }

    public static boolean saveWorld(Tile[][] arrayOfTiles) {
        // The array of Tiles is stored into a 1-Dimensional byte array
        byte[] data = new byte[TileMap.getNumOfHorizontalTiles() * TileMap.getNumOfVerticalTiles() * (Tile.getNumOfLayers() + 1)];
        int dataIterator = 0;
        for (int i = 0; i < TileMap.getNumOfHorizontalTiles(); i++) {
            for (int j = 0; j < TileMap.getNumOfVerticalTiles(); j++) {
                for (int k = 0; k < Tile.getNumOfLayers(); k++) {
                    data[dataIterator] = arrayOfTiles[i][j].getLayerValue(k);
                    dataIterator++;
                }
                data[dataIterator] = arrayOfTiles[i][j].isCollidable() ? (byte) 1 : (byte) 0;
                dataIterator++;
            }
        }

        FileOutputStream dataOutput;
        try {
            dataOutput = new FileOutputStream("world");
            dataOutput.write(data);
            dataOutput.close();
            return true;   // Success
        } catch (IOException e) {
            e.printStackTrace();
            return false;   // Something went wrong
        }
    }

    public static Tile[][] loadWorld() {
        Tile[][] arrayOfTiles = null;
        try {
            arrayOfTiles = WorldLoader.loadWorld("world");
            System.out.println("World loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading World. Reason: " + e);
        }
        return arrayOfTiles;
    }

    public static Tile[][] loadWorld(String worldFile) {
        File file = new File(worldFile);
        FileInputStream fileInputStream;
        byte[] fileData = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // The 1-Dimensional byte array is loaded into a 3-Dimensional array of Tiles
        Tile[][] arrayOfTiles = new Tile[TileMap.getNumOfHorizontalTiles()][TileMap.getNumOfVerticalTiles()];
        for (int i = 0; i < fileData.length; i += 4) {
            int x = (i / (Tile.getNumOfLayers() + 1)) / TileMap.getNumOfVerticalTiles();
            int y = (i / (Tile.getNumOfLayers() + 1)) % TileMap.getNumOfVerticalTiles();
            arrayOfTiles[x][y] = new Tile();
            for (int j = 0; j < Tile.getNumOfLayers(); j++) {
                arrayOfTiles[x][y].setLayerValue(j, fileData[i + j]);
            }
            arrayOfTiles[x][y].setCollidable(fileData[i + Tile.getNumOfLayers()] == (byte) 1);
        }

        return arrayOfTiles;
    }
}
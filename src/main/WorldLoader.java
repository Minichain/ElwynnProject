package main;

import entities.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldLoader {

    public static void saveWorld(byte[][][] arrayOfTiles) {
        // The array of Tiles is stored into a 1-Dimensional byte array
        byte[] data = new byte[Scene.getNumOfHorizontalTiles() * Scene.getNumOfVerticalTiles() * Scene.getNumOfTileLayers()];
        int dataIterator = 0;
        for (int i = 0; i < Scene.getNumOfHorizontalTiles(); i++) {
            for (int j = 0; j < Scene.getNumOfVerticalTiles(); j++) {
                for (int k = 0; k < Scene.getNumOfTileLayers(); k++) {
                    data[dataIterator] = arrayOfTiles[i][j][k];
                    dataIterator++;
                }
            }
        }

        FileOutputStream dataOutput;
        try {
            dataOutput = new FileOutputStream("world");
            dataOutput.write(data);
            dataOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[][][] loadWorld(String worldFile) {
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
        byte[][][] arrayOfTiles = new byte[Scene.getNumOfHorizontalTiles()][Scene.getNumOfVerticalTiles()][Scene.getNumOfTileLayers()];
        for (int i = 0; i < fileData.length; i++) {
            int x = (i / Scene.getNumOfTileLayers()) / Scene.getNumOfVerticalTiles();
            int y = (i / Scene.getNumOfTileLayers()) % Scene.getNumOfVerticalTiles();
            int k = i % Scene.getNumOfTileLayers();
            arrayOfTiles[x][y][k] = fileData[i];
        }

        return arrayOfTiles;
    }
}
package main;

import entities.*;
import scene.Scene;
import scene.Tile;
import scene.TileMap;
import utils.Utils;

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
        int dataArraySize = TileMap.getNumOfHorizontalTiles() * TileMap.getNumOfVerticalTiles() * (Tile.getNumOfLayers() + 1);
        dataArraySize += Scene.getInstance().getListOfStaticEntities().size() * (Double.BYTES * 2 + 1);
        byte[] data = new byte[dataArraySize];
        int dataIterator = 0;

        /** TILES DATA **/
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

        /** ENTITIES DATA **/
        for (int i = 0; i < Scene.getInstance().getListOfStaticEntities().size(); i++) {
            GraphicEntity graphicEntity = Scene.getInstance().getListOfStaticEntities().get(i);
            data[dataIterator] = graphicEntity.getEntityCode();
            System.out.println("Loading Entity "+ dataIterator + " at x: " + (int) graphicEntity.getWorldCoordinates().x + ", y: " + (int) graphicEntity.getWorldCoordinates().y + ", EntityCode: " + data[dataIterator]);
            dataIterator++;
            byte[] xCoordinate = Utils.doubleToBytes(graphicEntity.getWorldCoordinates().x);
            for (int j = 0; j < xCoordinate.length; j++) {
                data[dataIterator] = xCoordinate[j];
                dataIterator++;
            }
            byte[] yCoordinate = Utils.doubleToBytes(graphicEntity.getWorldCoordinates().y);
            for (int j = 0; j < xCoordinate.length; j++) {
                data[dataIterator] = yCoordinate[j];
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

        /** TILES DATA **/
        // The 1-Dimensional byte array is loaded into a 3-Dimensional array of Tiles
        Tile[][] arrayOfTiles = new Tile[TileMap.getNumOfHorizontalTiles()][TileMap.getNumOfVerticalTiles()];
        for (int i = 0; i < TileMap.getNumOfHorizontalTiles() * TileMap.getNumOfVerticalTiles() * (Tile.getNumOfLayers() + 1); i += 4) {
            int x = (i / (Tile.getNumOfLayers() + 1)) / TileMap.getNumOfVerticalTiles();
            int y = (i / (Tile.getNumOfLayers() + 1)) % TileMap.getNumOfVerticalTiles();
            arrayOfTiles[x][y] = new Tile();
            for (int j = 0; j < Tile.getNumOfLayers(); j++) {
                arrayOfTiles[x][y].setLayerValue(j, fileData[i + j]);
            }
            arrayOfTiles[x][y].setCollidable(fileData[i + Tile.getNumOfLayers()] == (byte) 1);
        }

        /** ENTITIES DATA **/
        int i = TileMap.getNumOfHorizontalTiles() * TileMap.getNumOfVerticalTiles() * (Tile.getNumOfLayers() + 1);
        while (i <= fileData.length - (Double.BYTES * 2 + 1)) {
            byte entityCode = fileData[i];

            byte[] xCoordinate = new byte[Double.BYTES];
            for (int j = 0; j < Double.BYTES; j++) {
                i++;
                xCoordinate[j] = fileData[i];
            }
            byte[] yCoordinate = new byte[Double.BYTES];
            for (int j = 0; j < Double.BYTES; j++) {
                i++;
                yCoordinate[j] = fileData[i];
            }

            System.out.println("Loading Entity "+ (i - Double.BYTES - Double.BYTES) + " at x: " + (int) Utils.byteArrayToDouble(xCoordinate) + ", y: " + (int) Utils.byteArrayToDouble(yCoordinate) + ", EntityCode: " + entityCode);

            if (entityCode == Tree01.ENTITY_CODE) {
                new Tree01((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Tree02.ENTITY_CODE) {
                new Tree02((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Tree03.ENTITY_CODE) {
                new Tree03((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Fence01.ENTITY_CODE) {
                new Fence01((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Fence02.ENTITY_CODE) {
                new Fence02((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Fence03.ENTITY_CODE) {
                new Fence03((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            } else if (entityCode == Building01.ENTITY_CODE) {
                new Building01((int) Utils.byteArrayToDouble(xCoordinate), (int) Utils.byteArrayToDouble(yCoordinate));
            }
            i++;
        }

        return arrayOfTiles;
    }
}
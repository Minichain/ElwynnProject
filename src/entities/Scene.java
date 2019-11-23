package entities;

import main.Coordinates;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static Scene instance = null;
    private static List<Entity> listOfEntities = new ArrayList<>();
    private static byte[][] arrayOfTiles;
    private int sceneX;
    private int sceneY;
    private static BufferedImage grass00;
    private static BufferedImage grass01;
    private static BufferedImage grass02;
    private static BufferedImage grass03;
    private static Coordinates center;

    private Scene() {
        center = new Coordinates(0, 0);
        sceneX = 1000;
        sceneY = 1000;
        arrayOfTiles = new byte[sceneX][sceneY];
        for (int i = 0; i < sceneX; i++) {
            for (int j = 0; j < sceneY; j++) {
                arrayOfTiles[i][j] = (byte)((Math.random() * 10) % 3);
            }
        }
        try {
            loadSprites();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }

    private void loadSprites() throws IOException {
        String path;
        path = "res/sprites/tiles/grass_00.png";
        grass00 = ImageIO.read(new File(path));
        path = "res/sprites/tiles/grass_01.png";
        grass01 = ImageIO.read(new File(path));
        path = "res/sprites/tiles/grass_02.png";
        grass02 = ImageIO.read(new File(path));
        path = "res/sprites/tiles/grass_03.png";
        grass03 = ImageIO.read(new File(path));
    }

    public static BufferedImage getTile(int tileNum) {
        switch(tileNum) {
            case 0:
                return grass00;
            case 1:
                return grass01;
            case 2:
                return grass02;
            case 3:
                return grass03;
        }
        return null;
    }

    public byte[][] getArrayOfTiles() {
        return arrayOfTiles;
    }

    public void setTile(int x, int y, byte value) {
        arrayOfTiles[x][y] = value;
    }

    public int getSceneX() {
        return sceneX;
    }

    public int getSceneY() {
        return sceneY;
    }

    public static Coordinates getCenter() {
        return center;
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
                if (listOfEntities.get(j).getCoordinates().getyCoordinate() < minDepth) {
                    minDepth = listOfEntities.get(j).getCoordinates().getyCoordinate();
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
}

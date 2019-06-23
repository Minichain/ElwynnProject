package entities;

import main.Coordinates;
import main.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static Scene instance = null;

    private static Coordinates coordinates;
    private BufferedImage sprite;
    private static BufferedImage collisionsMap;
    private static int spriteWidth;
    private static int spriteHeight;
    private static float scale = 2;

    private static List<Entity> listOfEntities = new ArrayList<>();

    private Scene() {
        coordinates = new Coordinates(0, 0);

        try {
            loadSprite();
//            loadCollisionsMap();
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

    private void loadSprite() throws IOException {
        String path = "res/sprites/background_01.png";
        sprite = ImageIO.read(new File(path));
        sprite = Utils.resizeImage(sprite, (int)(sprite.getWidth() * scale), (int)(sprite.getHeight() * scale));
        spriteWidth = sprite.getWidth();
        spriteHeight = sprite.getHeight();
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    private void loadCollisionsMap() throws IOException {
        String path = "res/sprites/background_collisions_01.png";
        collisionsMap = ImageIO.read(new File(path));
        collisionsMap = Utils.resizeImage(collisionsMap, (int)(collisionsMap.getWidth() * scale), (int)(collisionsMap.getHeight() * scale));
    }

    public static Coordinates getCoordinates() {
        return coordinates;
    }

    public BufferedImage getCollisionsMap() {
        return collisionsMap;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public float getScale() {
        return scale;
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
        Scene.getInstance().getListOfEntities().add(Character.getInstance());
    }
}

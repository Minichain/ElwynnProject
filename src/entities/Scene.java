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
    private static BufferedImage sprite;
    private static BufferedImage collisionsMap;
    private static int spriteWidth;
    private static int spriteHeight;
    private static float scale = 2;

    private static List<Tree> listOfTrees = new ArrayList<>();

    private Scene() {
        coordinates = new Coordinates(0, 0);

        try {
            loadSprite();
            loadCollisionsMap();
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

    public List<Tree> getListOfTrees() {
        return listOfTrees;
    }
}

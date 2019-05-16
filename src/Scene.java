import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Scene {
    private static Scene instance = null;

    private static BufferedImage sprite;
    private static BufferedImage collisionsMap;
    private static int spriteWidth;
    private static int spriteHeight;
    private static float scale = 2;

    private Scene() {
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
        String path = "res/Sprites/background_01.png";
        sprite = ImageIO.read(new File(path));
        sprite = Utils.resizeImage(sprite, (int)(sprite.getWidth() * scale), (int)(sprite.getHeight() * scale));
        spriteWidth = sprite.getWidth();
        spriteHeight = sprite.getHeight();
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    private void loadCollisionsMap() throws IOException {
        String path = "res/Sprites/background_collisions_01.png";
        collisionsMap = ImageIO.read(new File(path));
        collisionsMap = Utils.resizeImage(collisionsMap, (int)(collisionsMap.getWidth() * scale), (int)(collisionsMap.getHeight() * scale));
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
}

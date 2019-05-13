import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Scene {
    private static Scene instance = null;

    private BufferedImage sprite;
    private int spriteWidth;
    private int spriteHeight;
    private float scale = 2;

    private Scene() {

        try {
            loadSprite();
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
        spriteWidth = sprite.getWidth();
        spriteHeight = sprite.getHeight();
    }

    public BufferedImage getSprite() {
        return sprite;
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

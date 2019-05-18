package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tree extends StaticEntity {
    private BufferedImage sprite;

    public Tree(int x, int y) {
        super(x, y);
        try {
            loadSprite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSprite() throws IOException {
        String path;
        path = "res/sprites/tree_01.png";
        sprite = ImageIO.read(new File(path));
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}

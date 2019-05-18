package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tree extends StaticEntity {
    private BufferedImage sprite;
    private int typesOfTrees = 6;

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
        int random = (int)((Math.random() * 100) % typesOfTrees) + 1;
        path = "res/sprites/tree_0" + random + ".png";
        sprite = ImageIO.read(new File(path));
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}
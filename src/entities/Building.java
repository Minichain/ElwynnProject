package entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Building extends StaticEntity {
    private BufferedImage sprite;
    private int typesOfTrees = 1;

    public Building(int x, int y) {
        super(x, y);
        try {
            loadSprite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSprite() throws IOException {
        String path;

        //Generate a random tree
        int random = (int)((Math.random() * 100) % typesOfTrees) + 1;
        path = "res/sprites/building_0" + random + ".png";

        sprite = ImageIO.read(new File(path));
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}
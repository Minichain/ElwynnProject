package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Building extends StaticEntity {
    private Texture spriteSheet;
    private TextureRegion sprite;
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

        spriteSheet = new Texture(Gdx.files.internal(path));
    }

    public TextureRegion getSprite() {
        return sprite;
    }
}
package entities;

import main.Texture;

public class Building extends StaticEntity {
    private Texture spriteSheet;
    private int typesOfTrees = 1;

    public Building(int x, int y) {
        super(x, y);
        loadSprite();
    }

    @Override
    public void drawSprite(int x, int y) {

    }

    private void loadSprite() {

    }
}
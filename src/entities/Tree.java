package entities;

import main.Texture;

public class Tree extends StaticEntity {
    private Texture texture;

    public Tree(int x, int y) {
        super(x, y);
        loadSprite();
    }

    @Override
    public void drawSprite(int x, int y) {

    }

    private void loadSprite() {

    }
}
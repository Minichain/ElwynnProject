package entities;

import main.Texture;

public class Notch extends NonPlayerCharacter {
    public static byte ENTITY_CODE = 81;

    public Notch(int x, int y) {
        super(x, y);
        init();
    }

    private void init() {
        setSprite(SpriteManager.getInstance().NOTCH);
    }

    @Override
    public void update(long timeElapsed) {

    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {

    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void onDying() {

    }

    @Override
    public void hurt(float damage) {

    }
}

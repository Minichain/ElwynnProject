package entities;

import main.Texture;
import text.TextRendering;

public class InteractionEntity extends GraphicEntity {

    public InteractionEntity(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        if (true) { //TODO
            setSprite(SpriteManager.getInstance().F_KEYBOARD_KEY);
        } else {
            setSprite(SpriteManager.getInstance().A_CONTROLLER_BUTTON);
        }
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
        TextRendering.renderText(x + 25, y - 25, "Talk", 2f);
    }

    @Override
    public byte getEntityCode() {
        return 0;
    }

    @Override
    public void update(long timeElapsed) {

    }
}

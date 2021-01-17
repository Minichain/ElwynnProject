package entities;

import listeners.InputListenerManager;
import main.Parameters;
import main.Strings;
import main.Texture;
import text.TextRendering;

public class InteractionEntity extends GraphicEntity {

    public InteractionEntity(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        if (InputListenerManager.isUsingKeyboardAndMouse()) {
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
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                1f, 3.0 * Parameters.getResolutionFactor());
        TextRendering.renderText(x + 25 * Parameters.getResolutionFactor(), y - 23 * Parameters.getResolutionFactor(),
                Strings.getString("ui_interact_npc"), 2f * Parameters.getResolutionFactor());
    }

    @Override
    public String getEntityCode() {
        return null;
    }

    @Override
    public void update(long timeElapsed) {

    }
}

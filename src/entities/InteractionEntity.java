package entities;

import listeners.InputListenerManager;
import main.OpenGLManager;
import main.Parameters;
import main.Strings;
import main.Texture;
import scene.Camera;
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

    @Override
    public void drawHitBox() {
        int width = (int) (getSprite().SPRITE_WIDTH * Camera.getZoom());
        int height = (int) ((-1) * getSprite().SPRITE_HEIGHT * Camera.getZoom());
        OpenGLManager.drawRectangleOutline((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, height);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, 0, 1,0f, 0f, 1f);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, 0, height, 1,0f, 1f, 0f);
    }
}

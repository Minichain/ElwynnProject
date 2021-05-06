package entities;

import listeners.ActionManager;
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
            setSprite(SpriteManager.getKeySprite(ActionManager.Action.INTERACT.getActionKey()[0]));
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
                1f, 2.0 * Parameters.getHeightResolutionFactor());
        TextRendering.renderText(x + 35 * Parameters.getHeightResolutionFactor(), y - 25 * Parameters.getHeightResolutionFactor(),
                Strings.getString("ui_interact_npc"), 2f * Parameters.getHeightResolutionFactor());
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

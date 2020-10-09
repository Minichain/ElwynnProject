package entities;

import main.Coordinates;
import main.Texture;
import scene.Scene;

public class Notch extends NonPlayerCharacter {
    public static byte ENTITY_CODE = 81;
    private static final double interactionDistance = 25;

    public Notch(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        setSprite(SpriteManager.getInstance().NOTCH);
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfNonPlayerCharacters().add(this);
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
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
        InteractionEntity interactionEntity = getInteractionEntity();
        if (interactionEntity != null) {
            interactionEntity.drawSprite((int) interactionEntity.getCameraCoordinates().x, (int) interactionEntity.getCameraCoordinates().y);
        }
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

    @Override
    public double getInteractionDistance() {
        return interactionDistance;
    }
}

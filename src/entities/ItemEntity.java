package entities;

import items.*;
import main.Coordinates;
import main.Texture;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class ItemEntity extends DynamicGraphicEntity {
    private float interactionDistance = 25f;
    private ItemType itemType;

    public ItemEntity(int x, int y, ItemType itemType) {
        super(x, y);
        init(x, y, itemType);
    }

    private void init(int x, int y, ItemType itemType) {
        this.itemType = itemType;
        setWorldCoordinates(new Coordinates(x, y));
        setSprite(itemType.getSprite());
        Scene.getInstance().getListOfGraphicEntities().add(this);
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                1f, Camera.getZoom(), true);
    }

    @Override
    public byte getEntityCode() {
        return -1;
    }

    @Override
    public void update(long timeElapsed) {
        if (MathUtils.module(getWorldCoordinates(), Player.getInstance().getWorldCoordinates()) < interactionDistance) {
            onPickedUp();
        }
    }

    private void onPickedUp() {
        Item item = itemType.getItem();
        if (Player.getInstance().getInventory().isFreeSpace(item)) {
            itemType.playPickUpSound();
            Player.getInstance().getInventory().storeItem(item);
            setDead(true);
        }
    }
}

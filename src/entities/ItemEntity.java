package entities;

import audio.OpenALManager;
import items.*;
import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Camera;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

import java.awt.*;

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
        Item item;
        switch (itemType) {
            case GOLD_COIN:
            default:
                item = new GoldCoin();
                break;
            case HEALTH_POTION:
                item = new HealthPotion();
                break;
            case MANA_POTION:
                item = new ManaPotion();
                break;
            case HASTE_POTION:
                item = new HastePotion();
                break;
            case WOOD:
                item = new Wood();
                break;
        }
        if (Player.getInstance().getInventory().isFreeSpace(item)) {
            OpenALManager.playSound(OpenALManager.SOUND_GOLD_COIN_PICKED_UP_01);
            Player.getInstance().getInventory().storeItem(item);
            setDead(true);
        }
    }
}

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
        switch (itemType) {
            case GOLD_COIN:
            default:
                Player.getInstance().setAmountOfGoldCoins(Player.getInstance().getAmountOfGoldCoins() + 1);
                Log.l("Gold coin picked up. Current amount of gold coins: " + Player.getInstance().getAmountOfGoldCoins());
                OpenALManager.playSound(OpenALManager.SOUND_GOLD_COIN_PICKED_UP_01);
                new FloatingTextEntity(this.getCenterOfMassWorldCoordinates().x, this.getCenterOfMassWorldCoordinates().y, "+1",
                        new Color(1f, 0.9f, 0f), 1.25, new double[]{0, -1});
                setDead(true);
                break;
            case HEALTH_POTION:
                if (Player.getInstance().getInventory().isFreeSlot()) {
                    Player.getInstance().getInventory().storeItem(new HealthPotion());
                    setDead(true);
                }
                break;
            case MANA_POTION:
                if (Player.getInstance().getInventory().isFreeSlot()) {
                    Player.getInstance().getInventory().storeItem(new ManaPotion());
                    setDead(true);
                }
                break;
            case HASTE_POTION:
                if (Player.getInstance().getInventory().isFreeSlot()) {
                    Player.getInstance().getInventory().storeItem(new HastePotion());
                    setDead(true);
                }
                break;
            case WOOD:
                if (Player.getInstance().getInventory().isFreeSlot()) {
                    Player.getInstance().getInventory().storeItem(new Wood());
                    setDead(true);
                }
                break;
        }
    }
}

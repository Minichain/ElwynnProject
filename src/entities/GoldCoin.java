package entities;

import audio.OpenALManager;
import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Camera;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

import java.awt.*;

public class GoldCoin extends DynamicGraphicEntity {
    public static byte ENTITY_CODE = 10;
    private float interactionDistance = 25f;

    public GoldCoin(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        setSprite(SpriteManager.getInstance().GOLD_COIN);
        LightSource lightSource = new LightSource(getCenterOfMassWorldCoordinates(), 7.5f, new Color(1f, 0.9f, 0f));
        getLightSources().add(lightSource);
        Scene.getInstance().getListOfLightSources().add(lightSource);
        Scene.getInstance().getListOfEntities().add(this);
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
        return ENTITY_CODE;
    }

    @Override
    public void update(long timeElapsed) {
        if (MathUtils.module(getWorldCoordinates(), Player.getInstance().getWorldCoordinates()) < interactionDistance) {
            onPickedUp();
        }
    }

    private void onPickedUp() {
        Player.getInstance().setAmountOfGoldCoins(Player.getInstance().getAmountOfGoldCoins() + 1);
        Log.l("Gold coin picked up. Current amount of gold coins: " + Player.getInstance().getAmountOfGoldCoins());
        OpenALManager.playSound(OpenALManager.SOUND_GOLD_COIN_PICKED_UP_01);
        new FloatingTextEntity(this.getCenterOfMassWorldCoordinates().x, this.getCenterOfMassWorldCoordinates().y, "+1",
                new Color(1f, 0.9f, 0f), 1.25, new double[]{0, -1});
        onDestroy();
    }

    private void onDestroy() {
        Scene.getInstance().getListOfEntities().remove(this);
        for (int i = 0; i < getLightSources().size(); i++) {
            Scene.getInstance().getListOfLightSources().remove(getLightSources().get(i));
        }
    }
}

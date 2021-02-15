package entities;

import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class LargeWarp extends Warp {
    public static String ENTITY_CODE = "warp";

    private String warpToScene;
    private Coordinates warpToCoordinates;
    private double oscillation;

    public enum WarpType {
        WARP01(0), WARP02(1), WARP03(2), WARP04(3);

        public int value;

        WarpType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case WARP01:
                default:
                    return SpriteManager.getInstance().WARP_UP_LARGE;
                case WARP02:
                    return SpriteManager.getInstance().WARP_RIGHT_LARGE;
                case WARP03:
                    return SpriteManager.getInstance().WARP_DOWN_LARGE;
                case WARP04:
                    return SpriteManager.getInstance().WARP_LEFT_LARGE;
            }
        }
    }

    public LargeWarp(int x, int y, int t, String warpToScene, Coordinates warpToCoordinates) {
        super(x, y);
        init(t, warpToScene, warpToCoordinates);
    }

    private void init(int t, String warpToScene, Coordinates warpToCoordinates) {
        this.type = t;
        this.warpToScene = warpToScene;
        this.warpToCoordinates = warpToCoordinates;
        this.oscillation = 0;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates(getTileCoordinates()[0], getTileCoordinates()[1]));
        WarpType warpType = WarpType.values()[type];
        setSprite(warpType.getSprite());
        setCollision(null);
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        if (Player.getInstance().canTakeWarp() && MathUtils.module(Player.getInstance().getCenterOfMassWorldCoordinates(), getCenterOfMassWorldCoordinates()) < 25.0) {
            Log.l("Portal to " + warpToScene + " taken!");
            Player.getInstance().takeWarp();
            Scene.getInstance().setSceneName(warpToScene);
            Scene.getInstance().init();
            Camera.getInstance().setCoordinates(warpToCoordinates);
            Player.getInstance().setWorldCoordinates(warpToCoordinates);
        }
        oscillation += (timeElapsed / 100.0) % 1.0;
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public String getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void updateCoordinates() {
        super.updateCoordinates();
        setCameraCoordinates(new Coordinates(getCameraCoordinates().x, getCameraCoordinates().y + 0.5 * Math.sin(oscillation) * Camera.getZoom()));
    }

    @Override
    public String getWarpToScene() {
        return warpToScene;
    }

    @Override
    public void setWarpToScene(String warpToScene) {
        this.warpToScene = warpToScene;
    }

    @Override
    public Coordinates getWarpToCoordinates() {
        return warpToCoordinates;
    }

    @Override
    public void setWarpToScene(Coordinates warpToCoordinates) {
        this.warpToCoordinates = warpToCoordinates;
    }
}

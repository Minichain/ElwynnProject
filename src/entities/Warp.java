package entities;

import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class Warp extends StaticGraphicEntity {
    public static String ENTITY_CODE = "warp";

    private String warpToScene;
    private Coordinates warpToCoordinates;

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

    public Warp(int x, int y, int t, String warpToScene, Coordinates warpToCoordinates) {
        super(x, y);
        init(t, warpToScene, warpToCoordinates);
    }

    private void init(int t, String warpToScene, Coordinates warpToCoordinates) {
        this.type = t;
        this.warpToScene = warpToScene;
        this.warpToCoordinates = warpToCoordinates;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates((int) getTileCoordinates().x, (int) getTileCoordinates().y));
        WarpType warpType = WarpType.values()[type];
        setSprite(warpType.getSprite());
        setCollision(null);
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        if (MathUtils.module(Player.getInstance().getCenterOfMassWorldCoordinates(), getCenterOfMassWorldCoordinates()) < 20.0) {
            Log.l("Portal taken!");
            Scene.getInstance().setSceneName(warpToScene);
            Scene.getInstance().reset();
            Camera.getInstance().setCoordinates(warpToCoordinates);
            Player.getInstance().setWorldCoordinates(warpToCoordinates);
        }
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public String getEntityCode() {
        return ENTITY_CODE;
    }

    public String getWarpToScene() {
        return warpToScene;
    }

    public void setWarpToScene(String warpToScene) {
        this.warpToScene = warpToScene;
    }

    public Coordinates getWarpToCoordinates() {
        return warpToCoordinates;
    }

    public void setWarpToScene(Coordinates warpToCoordinates) {
        this.warpToCoordinates = warpToCoordinates;
    }
}

package entities;

import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class SmallWarp extends Warp {
    public static String ENTITY_CODE = "small_warp";

    private String warpToScene;
    private Coordinates warpToCoordinates;
    private double oscillation;

    public enum SmallWarpType {
        SMALLWARP01(0), SMALLWARP02(1), SMALLWARP03(2), SMALLWARP04(3);

        public int value;

        SmallWarpType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case SMALLWARP01:
                default:
                    return SpriteManager.getInstance().WARP_UP_SMALL;
                case SMALLWARP02:
                    return SpriteManager.getInstance().WARP_RIGHT_SMALL;
                case SMALLWARP03:
                    return SpriteManager.getInstance().WARP_DOWN_SMALL;
                case SMALLWARP04:
                    return SpriteManager.getInstance().WARP_LEFT_SMALL;
            }
        }
    }

    public SmallWarp(int x, int y, int t, String warpToScene, Coordinates warpToCoordinates) {
        super(x, y);
        init(t, warpToScene, warpToCoordinates);
    }

    private void init(int t, String warpToScene, Coordinates warpToCoordinates) {
        this.type = t;
        this.warpToScene = warpToScene;
        this.warpToCoordinates = warpToCoordinates;
        this.oscillation = 0;
        setWorldCoordinates(Coordinates.tileCoordinatesToWorldCoordinates(getTileCoordinates()[0], getTileCoordinates()[1]));
        SmallWarpType smallwarpType = SmallWarpType.values()[type];
        setSprite(smallwarpType.getSprite());
        setCollision(null);
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfStaticEntities().add(this);
    }

    @Override
    public void update(long timeElapsed) {
        if (MathUtils.module(Player.getInstance().getCenterOfMassWorldCoordinates(), getCenterOfMassWorldCoordinates()) < 15.0) {
            Log.l("Portal taken!");
            Scene.getInstance().setSceneName(warpToScene);
            Scene.getInstance().reset();
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
        setCameraCoordinates(new Coordinates(getCameraCoordinates().x, getCameraCoordinates().y + 0.25 * Math.sin(oscillation) * Camera.getZoom()));
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

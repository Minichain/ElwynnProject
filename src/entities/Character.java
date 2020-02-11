package entities;

import utils.Utils;
import listeners.MyInputListener;
import main.*;

public class Character extends DynamicEntity {
    private static Texture spriteSheet;
    private static Character instance = null;
    private static Status characterStatus;
    private static Utils.DirectionFacing directionFacing;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    private Character() {
        super((int) Scene.getInitialCoordinates().x,
                (int) Scene.getInitialCoordinates().y,
                (int) Scene.getInitialCoordinates().x,
                (int) Scene.getInitialCoordinates().y);
        initCharacter();
    }

    public void resetCharacter() {
        initCharacter();
    }

    private void initCharacter() {
        getCurrentCoordinates().x = Scene.getInitialCoordinates().x;
        getCurrentCoordinates().y = Scene.getInitialCoordinates().y;
        HEALTH = 100f;
        SPEED = 0.125;
        characterStatus = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
    }

    public static Character getInstance() {
        if (instance == null) {
            instance = new Character();
            Scene.getInstance().getListOfEntities().add(instance);
        }
        return instance;
    }

    @Override
    public void loadSprite() {
        String path = "res/sprites/characters/link.png";
        if (spriteSheet == null) spriteSheet = Texture.loadTexture(path);
        SPRITE_WIDTH = 32;
        SPRITE_HEIGHT = 32;
        IDLE_FRAMES = 1;
        RUNNING_FRAMES = 8;
        DYING_FRAMES = 1;
        DEAD_FRAMES = 1;
    }

    @Override
    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    @Override
    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        characterStatus = Status.IDLE;

        double[] movement = new double[]{0, 0};
        if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            movement = MyInputListener.computeMovementVector(timeElapsed, SPEED);
        }

        int distanceFactor = 4;
        if (!Scene.checkCollisionWithTile((int)(getCurrentCoordinates().x + movement[0] * distanceFactor), (int)(getCurrentCoordinates().y + movement[1] * distanceFactor))) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
            getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
        }

        DISPLACEMENT_VECTOR = new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y};

        if (DISPLACEMENT_VECTOR[0] != 0 || DISPLACEMENT_VECTOR[1] != 0) { //If character is moving
            directionFacing = Utils.checkDirectionFacing(DISPLACEMENT_VECTOR);
            characterStatus = Status.RUNNING;
        }

        switch (characterStatus) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % RUNNING_FRAMES);
                break;
            case JUMPING:
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (characterStatus) {
            default:
            case IDLE:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(0);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromSpriteSheetY(3);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(1);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(2);
                }
                break;
            case RUNNING:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(4);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromSpriteSheetY(7);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(5);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(6);
                }
                break;
            case JUMPING:
                break;
            case DYING:
                setSpriteCoordinateFromSpriteSheetY(8);
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetY(9);
                break;
        }
    }

    public Status getStatus() {
        return characterStatus;
    }
}

package entities;

import utils.Utils;
import listeners.MyInputListener;
import main.*;

public class Character extends DynamicEntity {
    private static Character instance = null;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    private static Status characterStatus;
    private static Utils.DirectionFacing directionFacing;


    private Character() {
        super((int) Parameters.getInstance().getStartingCoordinates().x,
                (int) Parameters.getInstance().getStartingCoordinates().y,
                (int) Parameters.getInstance().getStartingCoordinates().x,
                (int) Parameters.getInstance().getStartingCoordinates().y);
        initCharacter();
        loadSprite();
    }

    public void resetCharacter() {
        initCharacter();
    }

    private void initCharacter() {
        getCurrentCoordinates().x = Parameters.getInstance().getStartingCoordinates().x;
        getCurrentCoordinates().y = Parameters.getInstance().getStartingCoordinates().y;
        HEALTH = 100f;
        SPEED = 0.25;
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

    private void loadSprite() {
        String path = "res/sprites/characters/link.png";
        setSpriteSheet(Texture.loadTexture(path));
        SPRITE_WIDTH = 32;
        SPRITE_HEIGHT = 32;
        IDLE_FRAMES = 1;
        RUNNING_FRAMES = 8;
        DYING_FRAMES = 1;
        DEAD_FRAMES = 1;
        X_SPRITES = 8;
        Y_SPRITES = 10;
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

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        characterStatus = Status.IDLE;

        double[] movement = new double[]{0, 0};
        if (GameMode.getInstance().getGameMode() == GameMode.Mode.NORMAL) {
            movement = MyInputListener.computeMovementVector(timeElapsed, SPEED);
        }

        if (!checkCollision((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))
                && !checkCollisionWithEntities((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))) {
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

    private boolean checkCollisionWithEntities(int x, int y) {
        //TODO
        return false;
    }

    public boolean checkCollision(int x, int y) {
        //TODO
        return false;
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }
}

package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.Coordinates;
import main.Parameters;
import main.Utils;

public class Character extends DynamicEntity {
    private static Character instance = null;
    private static double speed;
    private static double[] displacementVector;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    private static Status characterStatus;
    private static Utils.DirectionFacing characterFacing;

    private static Texture spriteSheet;
    private static TextureRegion sprite;
    private static double spriteFrame;
    public static int spriteWidth;
    public static int spriteHeight;
    private static int idleFrames;
    private static int runningFrames;
    private static int specialAnimationFrames;

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
        speed = 0.0625;
        characterStatus = Status.IDLE;
        characterFacing = Utils.DirectionFacing.RIGHT;
        displacementVector = new double[2];
    }

    public static Character getInstance() {
        if (instance == null) {
            instance = new Character();
            Scene.getInstance().getListOfEntities().add(instance);
        }
        return instance;
    }

    private void loadSprite() {
        String path;
        path = "res/sprites/characters/bardo_character_02.png";
        spriteSheet = new Texture(Gdx.files.internal(path));
        spriteWidth = 16;
        spriteHeight = 22;
        idleFrames = 1;
        runningFrames = 3;
        specialAnimationFrames = 4;
    }

    public TextureRegion getSprite() {
        int animation;

        switch (characterStatus) {
            default:
            case IDLE:
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    animation= 7;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    animation= 5;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
                    animation= 6;
                } else {
                    animation= 4;
                }
                break;
            case RUNNING:
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    animation= 3;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    animation= 1;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
                    animation= 2;
                } else {
                    animation= 0;
                }
                break;
            case JUMPING:
                animation= 8;
                break;
        }
        sprite = new TextureRegion(spriteSheet, (int) spriteFrame * spriteWidth, animation * spriteHeight, spriteWidth - 1, spriteHeight - 1);
        return sprite;
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        if (characterStatus != Status.JUMPING) {
            characterStatus = Status.IDLE;
        }

        double[] movement = new double[2];
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement[1] = movement[1] - timeElapsed * speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement[0] = movement[0] - timeElapsed * speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement[1] = movement[1] + timeElapsed * speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement[0] = movement[0] + timeElapsed * speed;
        }

        //Normalize movement. The module of the movement vector must stay close to 1.
        if (movement[0] != 0 && movement[1] != 0) {
            movement[0] *= 0.75;
            movement[1] *= 0.75;
        }

        if (!checkCollision((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))
                && !checkCollisionWithEntities((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
            getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
        }

        displacementVector[0] = getCurrentCoordinates().x - getPreviousCoordinates().x;
        displacementVector[1] = getCurrentCoordinates().y - getPreviousCoordinates().y;

        if (isRunning() && characterStatus != Status.JUMPING) {
            characterFacing = Utils.checkDirectionFacing(displacementVector);
            characterStatus = Status.RUNNING;
        }

        switch(characterStatus) {
            case IDLE:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % idleFrames;
                break;
            case RUNNING:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % runningFrames;
                break;
            case JUMPING:
                if (spriteFrame >= (specialAnimationFrames - 1)) {
                    characterStatus = Status.IDLE;
                } else {
                    spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % specialAnimationFrames;
                }
                break;
        }
    }

    private boolean checkCollisionWithEntities(int x, int y) {
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        double distanceToEntity;
        for (int i = 0; i < listOfEntities.size(); i++) {
            if (listOfEntities.get(i) != this) {    //Do not check collision with yourself!
                distanceToEntity = Utils.module(listOfEntities.get(i).getCoordinates(), new Coordinates(x, y));
                if (distanceToEntity < 50) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkCollision(int x, int y) {
        //TODO
        return false;
    }

    public boolean isRunning() {
        return (displacementVector[0] != 0 || displacementVector[1] != 0);
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public void performJump() {
        spriteFrame = 0;
        characterStatus = Status.JUMPING;
    }
}

package entities;

import listeners.MyInputListener;
import main.*;

import static org.lwjgl.opengl.GL11.*;

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

    @Override
    public void drawSprite(int x, int y) {
        getSpriteSheet().bind();

        float u = ((1f / X_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetX());
        float v = 1f - ((1f / Y_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetY());
        float u2 = u + (1f / X_SPRITES);
        float v2 = v - (1f / Y_SPRITES);
        double scale = Scene.getZoom();

        glBegin(GL_QUADS);
        x -= (SPRITE_WIDTH / 2) * (int) scale;
        y -= (SPRITE_HEIGHT / 2) * (int) scale;
        MyOpenGL.drawTexture(x, y , u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale));
        glEnd();
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        characterStatus = Status.IDLE;

        double[] movement = new double[2];
        if (MyInputListener.sKeyPressed) {
            movement[1] = movement[1] + timeElapsed * SPEED;
        }
        if (MyInputListener.aKeyPressed) {
            movement[0] = movement[0] - timeElapsed * SPEED;
        }
        if (MyInputListener.wKeyPressed) {
            movement[1] = movement[1] - timeElapsed * SPEED;
        }
        if (MyInputListener.dKeyPressed) {
            movement[0] = movement[0] + timeElapsed * SPEED;
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
//        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
//        double distanceToEntity;
//        for (int i = 0; i < listOfEntities.size(); i++) {
//            if (listOfEntities.get(i) != this) {    //Do not check collision with yourself!
//                distanceToEntity = Utils.module(listOfEntities.get(i).getCoordinates(), new Coordinates(x, y));
//                if (distanceToEntity < 50) {
//                    return true;
//                }
//            }
//        }
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

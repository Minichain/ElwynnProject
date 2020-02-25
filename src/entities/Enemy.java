package entities;

import main.MyOpenAL;
import utils.MathUtils;
import main.Texture;
import utils.Utils;

public class Enemy extends DynamicEntity {
    private static Texture spriteSheet;
    private Utils.DirectionFacing directionFacing;
    private Status status;
    private static int attackPeriod = 500;
    private int attackCoolDown = 0;
    private float attackPower = 30f;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    public Enemy(int x, int y) {
        super(x, y, x, y);
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {
        getCurrentCoordinates().x = x;
        getCurrentCoordinates().y = y;
        health = 1000f;
        speed = 0.075;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        Scene.getInstance().getListOfEntities().add(this);
    }

    @Override
    public void loadSprite() {
        String path = "res/sprites/characters/enemy.png";
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
        if (health > 0) {
            status = Status.IDLE;
            double[] movement = new double[2];
            movement[0] = (Character.getInstance().getCurrentCoordinates().x - getCurrentCoordinates().x);
            movement[1] = (Character.getInstance().getCurrentCoordinates().y - getCurrentCoordinates().y);
            boolean closeToPlayer = !(MathUtils.module(movement) > 25 && MathUtils.module(movement) < 2000);
            boolean chasing = (status != Status.DYING && status != Status.DEAD && !closeToPlayer);

            if (closeToPlayer && Character.getInstance().getStatus() != Character.Status.DEAD) {
                attack(timeElapsed);
            }

            movement = MathUtils.normalizeVector(movement);
            movement[0] *= timeElapsed * speed;
            movement[1] *= timeElapsed * speed;

            int distanceFactor = 2;
            if (!TileMap.checkCollisionWithTile((int)(getCurrentCoordinates().x + movement[0] * distanceFactor), (int)(getCurrentCoordinates().y + movement[1] * distanceFactor))
                    && chasing) {
                getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
                getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
            }

            displacementVector = new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y};

            if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If character is moving
                directionFacing = Utils.checkDirectionFacing(displacementVector);
                status = Status.RUNNING;
            }
        } else if (status != Status.DEAD) {
            status = Status.DYING;
        }

        switch (status) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % RUNNING_FRAMES);
                break;
            case JUMPING:
                break;
            case DYING:
                double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame > 1) {
                    status = Status.DEAD;
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % DYING_FRAMES);
                }
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % DEAD_FRAMES);
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (status) {
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
            case DYING:
                setSpriteCoordinateFromSpriteSheetY(8);
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetY(9);
                break;
        }
    }

    public Status getStatus() {
        return status;
    }

    private void attack(long timeElapsed) {
        if (attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }
        float damage = (float) (attackPower + (Math.random() * 10));
        String text = String.valueOf((int) damage);
        double[] entityCameraCoordinates = Character.getInstance().getCurrentCoordinates().toCameraCoordinates();
        int x = (int) entityCameraCoordinates[0];
        int y = (int) entityCameraCoordinates[1];
        FloatingTextEntity textEntity = new FloatingTextEntity(x, y, text, true, false, true);
        Character.getInstance().setHealth(Character.getInstance().getHealth() - damage);
        MyOpenAL.playSound(MyOpenAL.SOUND_LINK_HURT);
        attackCoolDown = attackPeriod;
    }
}

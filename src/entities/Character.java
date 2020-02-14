package entities;

import utils.MathUtils;
import utils.Utils;
import listeners.MyInputListener;
import main.*;

public class Character extends DynamicEntity {
    private static Texture spriteSheet;
    private static Character instance = null;
    private static Status characterStatus;
    private static Utils.DirectionFacing directionFacing;

    /** ATTACK **/
    private boolean attacking = false;
    private static int attackPeriod = 100;
    private int attackCoolDown = 0;
    private float attackPower = 2f;
    private ConeAttack coneAttack;

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
        health = 5000f;
        speed = 0.125;
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
        if (health > 0)  {
            characterStatus = Status.IDLE;

            attacking = (GameMode.getGameMode() == GameMode.Mode.NORMAL && MyInputListener.leftMouseButtonPressed);
            attack(timeElapsed);

            double[] movement = new double[]{0, 0};
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                movement = MyInputListener.computeMovementVector(timeElapsed, speed);
            }

            int distanceFactor = 4;
            if (!TileMap.checkCollisionWithTile((int)(getCurrentCoordinates().x + movement[0] * distanceFactor), (int)(getCurrentCoordinates().y + movement[1] * distanceFactor))) {
                getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
                getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
            }

            displacementVector = new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y};

            if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If character is moving
                directionFacing = Utils.checkDirectionFacing(displacementVector);
                characterStatus = Status.RUNNING;
            }
        } else if (characterStatus != Status.DEAD) {
            characterStatus = Status.DYING;
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
            case DYING:
                double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame > 1) {
                    characterStatus = Status.DEAD;
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

    private void attack(long timeElapsed) {
        double[] mouseWorldCoordinates = new Coordinates(MyInputListener.getMousePositionX(), MyInputListener.getMousePositionY()).toWorldCoordinates();
        double[] pointingVector = new double[]{mouseWorldCoordinates[0] - Character.getInstance().getCurrentCoordinates().x,
                mouseWorldCoordinates[1] - Character.getInstance().getCurrentCoordinates().y};
        coneAttack = new ConeAttack(pointingVector, 50, 200);

        if (!attacking || attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }

        Entity entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            entity = Scene.getInstance().getListOfEntities().get(i);
            double[] entityCameraCoords = entity.getCoordinates().toCameraCoordinates();
            if (entity instanceof Enemy
                    && ((Enemy) entity).getStatus() != Enemy.Status.DEAD
                    && MathUtils.isPointInsideTriangle(new double[]{entityCameraCoords[0], entityCameraCoords[1]}, coneAttack.getVertex1(), coneAttack.getVertex2(), coneAttack.getVertex3())) {
                float damage = (float) (attackPower * timeElapsed + Math.random() % 100);
                ((Enemy) entity).setHealth(((Enemy) entity).getHealth() - damage);
                String text = String.valueOf((int) damage);
                double[] entityCameraCoordinates = entity.getCoordinates().toCameraCoordinates();
                int x = (int) entityCameraCoordinates[0];
                int y = (int) entityCameraCoordinates[1];
                new FloatingTextEntity(x, y, text, true, false, false);
            }
        }

        attackCoolDown = attackPeriod;
    }

    public void drawAttackFX() {
        if (attacking && GameMode.getGameMode() == GameMode.Mode.NORMAL) {
            if (coneAttack != null) coneAttack.render();
        }
    }
}

package entities;

import audio.OpenALManager;
import utils.MathUtils;
import utils.Utils;
import listeners.MyInputListener;
import main.*;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends DynamicEntity {
    private static Texture spriteSheet;
    private static Player instance = null;
    private static Status playerStatus;
    private static Utils.DirectionFacing directionFacing;

    /** ATTACK **/
    private boolean attacking = false;
    private int attackPeriod = 250;
    private int attackCoolDown = 0;
    private float attackPower = 100f;
    private ConeAttack coneAttack;
    private float coneAttackLength = 200f;

    private CircleAttack circleAttack;
    private int circleAttackPeriod = 10000;
    private int circleAttackCoolDown = 0;
    private float circleAttackPower = 100f;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    private Player() {
        super((int) Scene.getInitialCoordinates().x,
                (int) Scene.getInitialCoordinates().y,
                (int) Scene.getInitialCoordinates().x,
                (int) Scene.getInitialCoordinates().y);
        init();
    }

    public void reset() {
        init();
    }

    private void init() {
        getCurrentCoordinates().x = Scene.getInitialCoordinates().x;
        getCurrentCoordinates().y = Scene.getInitialCoordinates().y;
        health = 5000f;
        speed = 0.125;
        playerStatus = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
    }

    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
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
            playerStatus = Status.IDLE;

            attacking = (GameMode.getGameMode() == GameMode.Mode.NORMAL && MyInputListener.leftMouseButtonPressed);
            attack(timeElapsed);

            double[] movement = new double[]{0, 0};
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                movement = computeMovementVector(timeElapsed, speed);
            }

            int distanceFactor = 4;
            boolean horizontalCollision = TileMap.checkCollisionWithTile((int)(getCurrentCoordinates().x + movement[0] * distanceFactor), (int)(getCurrentCoordinates().y));
            boolean verticalCollision = TileMap.checkCollisionWithTile((int)(getCurrentCoordinates().x), (int)(getCurrentCoordinates().y + movement[1] * distanceFactor));
            if (!horizontalCollision) {
                getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
            }
            if (!verticalCollision) {
                getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
            }

            displacementVector = new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y};
            facingVector = null;
            if (attacking) {
                double[] playerCameraCoordinates = getCurrentCoordinates().toCameraCoordinates();
                facingVector = new double[]{MyInputListener.getMouseCameraCoordinates()[0] - playerCameraCoordinates[0],
                        MyInputListener.getMouseCameraCoordinates()[1] - playerCameraCoordinates[1]};
                directionFacing = Utils.checkDirectionFacing(facingVector);
            } else if (displacementVector[0] != 0 || displacementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(displacementVector);
            }

            if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If player is moving
                playerStatus = Status.RUNNING;
            }
        } else if (playerStatus != Status.DEAD && playerStatus != Status.DYING) {
            OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
            playerStatus = Status.DYING;
        } else {
            attacking = false;
        }

        switch (playerStatus) {
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
                    playerStatus = Status.DEAD;
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

    public double[] computeMovementVector(long timeElapsed, double speed) {
        double[] movement = new double[2];
        if (MyInputListener.isKeyPressed(GLFW_KEY_S)) {
            movement[1] = 1;
        }
        if (MyInputListener.isKeyPressed(GLFW_KEY_A)) {
            movement[0] = -1;
        }
        if (MyInputListener.isKeyPressed(GLFW_KEY_W)) {
            movement[1] = -1;
        }
        if (MyInputListener.isKeyPressed(GLFW_KEY_D)) {
            movement[0] = 1;
        }

        movement = MathUtils.normalizeVector(movement);
        if (attacking) {
            speed *= 0.5;
        }
        movement[0] *= timeElapsed * speed;
        movement[1] *= timeElapsed * speed;

        return movement;
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (playerStatus) {
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
        return playerStatus;
    }

    private void attack(long timeElapsed) {
        double[] mouseWorldCoordinates = new Coordinates(MyInputListener.getMouseCameraCoordinates()[0], MyInputListener.getMouseCameraCoordinates()[1]).toWorldCoordinates();

        /** CONE ATTACK **/
        double[] pointingVector = new double[]{mouseWorldCoordinates[0] - Player.getInstance().getCurrentCoordinates().x,
                mouseWorldCoordinates[1] - Player.getInstance().getCurrentCoordinates().y};

        attacking = attacking && playerStatus != Player.Status.DEAD;

        if (coneAttack == null) {
            coneAttack = new ConeAttack(getCurrentCoordinates(), pointingVector, Math.PI / 6.0, coneAttackLength, attackPeriod, attackCoolDown, attackPower, false, attacking);
        } else {
            coneAttack.update(getCurrentCoordinates(), pointingVector, timeElapsed, attacking);
        }

        /** CIRCLE ATTACK **/
        if (MyInputListener.rightMouseButtonPressed) {
            if (circleAttackCoolDown <= 0) {
                circleAttack = new CircleAttack(new Coordinates(mouseWorldCoordinates[0], mouseWorldCoordinates[1]),
                        100, 500, circleAttackPower, false, true);
                Scene.listOfCircleAttacks.add(circleAttack);
                circleAttackCoolDown = circleAttackPeriod;
            }
        }
        if (circleAttackCoolDown > 0) {
            circleAttackCoolDown -= timeElapsed;
        }
    }

    public void drawAttackFX() {
        if (coneAttack != null && attacking) {
            coneAttack.render();
        }
    }
}

package entities;

import audio.OpenALManager;
import scene.Scene;
import scene.TileMap;
import utils.MathUtils;
import utils.Utils;
import listeners.InputListenerManager;
import main.*;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends DynamicGraphicEntity {
    public static byte ENTITY_CODE = 41;
    private static Player instance = null;
    private static Status playerStatus;
    private static Utils.DirectionFacing directionFacing;

    public static float MAX_HEALTH = 5000f;
    public static float HEALTH_REGENERATION = 0.01f;
    public static float MAX_MANA = 100f;
    public static float MANA_REGENERATION = 0.0003f;
    private float mana = 100f;
    public static float MAX_STAMINA = 100f;
    public static float STAMINA_REGENERATION = 0.01f;
    private float stamina = 100f;

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
    private float circleAttackManaCost = 25f;

    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD;
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
        setWorldCoordinates(Scene.getInitialCoordinates());
        health = 5000f;
        mana = 100f;
        speed = 0.125;
        playerStatus = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        setSprite(SpriteManager.getInstance().PLAYER);
    }

    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
            Scene.getInstance().getListOfEntities().add(instance);
        }
        return instance;
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1.0);
    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void update(long timeElapsed) {
        setPreviousWorldCoordinates(getWorldCoordinates());
        if (health > 0)  {
            if (playerStatus != Status.ROLLING) {
                playerStatus = Status.IDLE;
            }
            if (mana < MAX_MANA) {
                mana += (MANA_REGENERATION * timeElapsed);
            } else if (mana > MAX_MANA) {
                mana = MAX_MANA;
            }
            if (health < MAX_HEALTH) {
                health += (HEALTH_REGENERATION * timeElapsed);
            } else if (health > MAX_HEALTH) {
                health = MAX_HEALTH;
            }
            if (stamina < MAX_STAMINA) {
                stamina += (STAMINA_REGENERATION * timeElapsed);
            } else if (stamina > MAX_STAMINA) {
                stamina = MAX_STAMINA;
            }

            attacking = (GameMode.getGameMode() == GameMode.Mode.NORMAL && InputListenerManager.leftMouseButtonPressed);
            attack(timeElapsed);

            double[] movement = new double[]{0, 0};
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (playerStatus != Status.ROLLING) {
                    movement = computeMovementVector(timeElapsed, speed);
                } else {
                    movement = displacementVector;
                }
            }

            double distanceFactor = 4;
            boolean horizontalCollision = checkHorizontalCollision(movement, distanceFactor);
            boolean verticalCollision = checkVerticalCollision(movement, distanceFactor);
            if (!horizontalCollision) {
                getWorldCoordinates().x += movement[0];
            }
            if (!verticalCollision) {
                getWorldCoordinates().y += movement[1];
            }

            displacementVector = new double[]{getWorldCoordinates().x - getPreviousWorldCoordinates().x, getWorldCoordinates().y - getPreviousWorldCoordinates().y};
            facingVector = null;
            if (attacking) {
                facingVector = new double[]{InputListenerManager.getMouseCameraCoordinates().x - getCameraCoordinates().x,
                        InputListenerManager.getMouseCameraCoordinates().y - getCameraCoordinates().y};
                directionFacing = Utils.checkDirectionFacing(facingVector);
            } else if (displacementVector[0] != 0 || displacementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(displacementVector);
            }

            if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If player is moving
                if (playerStatus != Status.ROLLING) {
                    playerStatus = Status.RUNNING;
                }
            }
        } else if (playerStatus != Status.DEAD && playerStatus != Status.DYING) {
            OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
            playerStatus = Status.DYING;
        } else {
            attacking = false;
        }

        double frame;
        frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
        switch (playerStatus) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().RUNNING_FRAMES);
                break;
            case ROLLING:
                if (frame >= getSprite().JUMPING_FRAMES) {
                    playerStatus = Status.IDLE;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().JUMPING_FRAMES);
                }
                break;
            case DYING:
                if (frame >= getSprite().DYING_FRAMES) {
                    playerStatus = Status.DEAD;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DYING_FRAMES);
                }
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DEAD_FRAMES);
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    private boolean checkHorizontalCollision(double[] movement, double distanceFactor) {
        Coordinates collisionCoordinates = new Coordinates(getCenterOfMassWorldCoordinates().x + movement[0] * distanceFactor, getCenterOfMassWorldCoordinates().y);
        boolean tileCollision = TileMap.checkCollisionWithTile((int) collisionCoordinates.x, (int) collisionCoordinates.y);

        return Scene.getInstance().checkCollisionWithEntities(collisionCoordinates) || tileCollision;
    }

    private boolean checkVerticalCollision(double[] movement, double distanceFactor) {
        Coordinates collisionCoordinates = new Coordinates(getCenterOfMassWorldCoordinates().x, getCenterOfMassWorldCoordinates().y + movement[1] * distanceFactor);
        boolean tileCollision = TileMap.checkCollisionWithTile((int) collisionCoordinates.x, (int) collisionCoordinates.y);

        return Scene.getInstance().checkCollisionWithEntities(collisionCoordinates) || tileCollision;
    }

    public double[] computeMovementVector(long timeElapsed, double speed) {
        double[] movement = new double[2];
        if (InputListenerManager.isKeyPressed(GLFW_KEY_S)) {
            movement[1] = 1;
        }
        if (InputListenerManager.isKeyPressed(GLFW_KEY_A)) {
            movement[0] = -1;
        }
        if (InputListenerManager.isKeyPressed(GLFW_KEY_W)) {
            movement[1] = -1;
        }
        if (InputListenerManager.isKeyPressed(GLFW_KEY_D)) {
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
            case ROLLING:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(10);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromSpriteSheetY(13);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(11);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(12);
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
        return playerStatus;
    }

    private void attack(long timeElapsed) {
        /** CONE ATTACK **/
        double[] pointingVector = new double[]{InputListenerManager.getMouseWorldCoordinates().x - Player.getInstance().getCenterOfMassWorldCoordinates().x,
                InputListenerManager.getMouseWorldCoordinates().y - Player.getInstance().getCenterOfMassWorldCoordinates().y};

        attacking = attacking && playerStatus != Player.Status.DEAD;

        if (coneAttack == null) {
            coneAttack = new ConeAttack(getCenterOfMassWorldCoordinates(), pointingVector, Math.PI / 6.0, coneAttackLength, attackPeriod, attackCoolDown, attackPower, false, attacking);
        } else {
            coneAttack.update(getCenterOfMassWorldCoordinates(), pointingVector, timeElapsed, attacking);
        }

        /** CIRCLE ATTACK **/
        if (InputListenerManager.rightMouseButtonPressed) {
            if (mana >= circleAttackManaCost && circleAttackCoolDown <= 0) {
                circleAttack = new CircleAttack(new Coordinates(InputListenerManager.getMouseWorldCoordinates().x, InputListenerManager.getMouseWorldCoordinates().y),
                        100, 500, circleAttackPower, false, true);
                Scene.listOfCircleAttacks.add(circleAttack);
                circleAttackCoolDown = circleAttackPeriod;
                mana -= circleAttackManaCost;
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

    public float getMana() {
        return mana;
    }

    public float getStamina() {
        return stamina;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public void roll() {
        if (stamina >= 25f && playerStatus == Status.RUNNING && !attacking) {
            playerStatus = Status.ROLLING;
            setSpriteCoordinateFromSpriteSheetX(0);
            stamina -= 25f;
        }
    }
}

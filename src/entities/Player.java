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
    public static float STAMINA_REGENERATION = 0.018f;
    public static float STAMINA_REGENERATION_WHEN_ATTACKING = 0.009f;
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

    private AttackMode attackMode;

    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD;
    }

    boolean footstep = true;

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
        attackMode = AttackMode.MODE_01;
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
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void update(long timeElapsed) {
        setPreviousWorldCoordinates(getWorldCoordinates());

        if (health > 0)  {  //Player is alive
            /** UPDATE MANA, HEALTH AND STAMINA **/
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
                if (attacking) {
                    stamina += (STAMINA_REGENERATION_WHEN_ATTACKING * timeElapsed);
                } else {
                    stamina += (STAMINA_REGENERATION * timeElapsed);
                }
            } else if (stamina > MAX_STAMINA) {
                stamina = MAX_STAMINA;
            }

            /** UPDATE ATTACKS **/
            attacking = (GameMode.getGameMode() == GameMode.Mode.NORMAL && (InputListenerManager.leftMouseButtonPressed || InputListenerManager.getRightTriggerValue() > 0f));
            attack(timeElapsed);

            /** UPDATE MOVEMENT VECTOR **/
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (playerStatus != Status.ROLLING) {
                    movementVector = computeMovementVector(timeElapsed);
                }
            }

            /** CHECK COLLISIONS **/
            double distanceFactor = timeElapsed / 32.0;
            boolean horizontalCollision = checkHorizontalCollision(movementVector, distanceFactor);
            boolean verticalCollision = checkVerticalCollision(movementVector, distanceFactor);

            /** MOVE ENTITY **/
            double speed = 0.0;
            if (attacking) {
                speed = this.speed * 0.5;
            } else if (playerStatus == Status.RUNNING) {
                speed = this.speed;
            } else if (playerStatus == Status.ROLLING) {
                speed = this.speed * 1.5;
            }

            if (!horizontalCollision) {
                getWorldCoordinates().x += movementVector[0] * speed;
            }
            if (!verticalCollision) {
                getWorldCoordinates().y += movementVector[1] * speed;
            }

            /** WHERE IS IT FACING? **/
            facingVector = null;
            if (attacking) {
                facingVector = new double[]{InputListenerManager.getMouseCameraCoordinates().x - getCameraCoordinates().x,
                        InputListenerManager.getMouseCameraCoordinates().y - getCameraCoordinates().y};
                directionFacing = Utils.checkDirectionFacing(facingVector);
            } else if (movementVector[0] != 0 || movementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(movementVector);
            }
        } else if (playerStatus != Status.DEAD) {   //Player is dying
            OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
            playerStatus = Status.DYING;
        } else {    //Player is dead
            attacking = false;
        }

        double frame;
        frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
        switch (playerStatus) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
                if (footstep && (int) frame % 4 == 0) {
                    footstep = false;
                    if (Math.random() < 0.5) {
                        OpenALManager.playSound(OpenALManager.SOUND_FOOTSTEP_01);
                    } else {
                        OpenALManager.playSound(OpenALManager.SOUND_FOOTSTEP_02);
                    }
                } else if ((int) frame % 4 != 0) {
                    footstep = true;
                }
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

    public double[] computeMovementVector(long timeElapsed) {
        double[] movement = new double[2];
        boolean playerMoving = false;
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

        movement[0] += (double) InputListenerManager.getLeftJoystickAxes()[0];
        movement[1] += (double) InputListenerManager.getLeftJoystickAxes()[1];

        if (Math.abs(movement[0]) > 0 || Math.abs(movement[1]) > 0) {
            playerMoving = true;
        }

        if (playerMoving) {
            playerStatus = Status.RUNNING;
        } else {
            playerStatus = Status.IDLE;
        }

        movement = MathUtils.normalizeVector(movement);
        movement[0] *= timeElapsed;
        movement[1] *= timeElapsed;

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

    private double[] pointingVector = new double[]{1.0, 1.0};

    private void attack(long timeElapsed) {
        /** CONE ATTACK **/
        if (InputListenerManager.isUsingKeyboardAndMouse()) {
            pointingVector = new double[]{InputListenerManager.getMouseWorldCoordinates().x - Player.getInstance().getCenterOfMassWorldCoordinates().x,
                    InputListenerManager.getMouseWorldCoordinates().y - Player.getInstance().getCenterOfMassWorldCoordinates().y};
        } else {
            if (InputListenerManager.getRightJoystickAxes()[0] != 0f || InputListenerManager.getRightJoystickAxes()[1] != 0) {
                pointingVector = new double[]{(double) InputListenerManager.getRightJoystickAxes()[0], (double) InputListenerManager.getRightJoystickAxes()[1]};
            }
        }

        attacking = attacking && playerStatus != Player.Status.DEAD;

        if (coneAttack == null) {
            coneAttack = new ConeAttack(getCenterOfMassWorldCoordinates(), pointingVector, Math.PI / 6.0, coneAttackLength, attackPeriod, attackCoolDown, attackPower, false, attacking);
        } else {
            coneAttack.update(getCenterOfMassWorldCoordinates(), pointingVector, timeElapsed, attacking, attackMode);
        }

        /** CIRCLE ATTACK **/
        if (InputListenerManager.rightMouseButtonPressed) {
            if (mana >= circleAttackManaCost && circleAttackCoolDown <= 0) {
                circleAttack = new CircleAttack(new Coordinates(InputListenerManager.getMouseWorldCoordinates().x, InputListenerManager.getMouseWorldCoordinates().y),
                        100, 500, circleAttackPower, false, true, attackMode);
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
            OpenALManager.playSound(OpenALManager.SOUND_ROLLING_01);
            stamina -= 25f;
        }
    }

    public AttackMode getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(int attackMode) {
        switch (attackMode % AttackMode.numOfAttackModes) {
            case 0:
                this.attackMode = AttackMode.MODE_01;
                break;
            case 1:
                this.attackMode = AttackMode.MODE_02;
                break;
            case 2:
            default:
                this.attackMode = AttackMode.MODE_03;
                break;
        }
    }
}

package entities;

import audio.OpenALManager;
import main.*;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import utils.MathUtils;
import utils.Utils;

import static org.lwjgl.opengl.GL11.*;

public class Enemy extends DynamicGraphicEntity {
    public static byte ENTITY_CODE = 51;
    private Utils.DirectionFacing directionFacing;
    private Status status;

    /** ATTACK **/
    private boolean attacking = false;

    private ConeAttack coneAttack;
    private int coneAttackPeriod = 500;
    private int coneAttackCoolDown = 0;
    private float coneAttackPower = 100f;
    private float coneAttackLength = 75;

    private CircleAttack circleAttack;
    private int circleAttackPeriod = 10000;
    private int circleAttackCoolDown = 0;
    private float circleAttackPower = 100f;

    /** PATH FINDING **/
    boolean useDijkstraAlgorithm = true;
    private PathFindingAlgorithm pathFindingAlgorithm;
    private double distanceToPlayer;
    private int computePathPeriod = 600;
    private int computePathCoolDown = 0;

    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD;
    }

    public Enemy(int x, int y) {
        super(x, y, x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        health = 2500f;
        speed = Math.random() * 0.06 + 0.04;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        int numOfEnemyTypes = 3;
        int randomEnemy = (int) (Math.random() * Integer.MAX_VALUE) % numOfEnemyTypes;
        switch (randomEnemy) {
            case 0:
                setSprite(SpriteManager.getInstance().ENEMY01);
                break;
            case 1:
                setSprite(SpriteManager.getInstance().ENEMY02);
                break;
            case 2:
            default:
                setSprite(SpriteManager.getInstance().ENEMY03);
                break;
        }
        Scene.getInstance().getListOfEntities().add(this);
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

        if (health > 0) {   //Enemy is alive
            distanceToPlayer = MathUtils.module(getWorldCoordinates(), Player.getInstance().getWorldCoordinates());
            attacking = distanceToPlayer < coneAttackLength && Player.getInstance().getStatus() != Player.Status.DEAD;

            movementVector = computeMovementVector(timeElapsed);
            attack(timeElapsed);

            /** CHECK COLLISIONS **/
            double distanceFactor = timeElapsed / 32.0;
            boolean horizontalCollision = checkHorizontalCollision(movementVector, distanceFactor);
            boolean verticalCollision = checkVerticalCollision(movementVector, distanceFactor);

            /** MOVE ENTITY **/
            double speed = 0.0;
            if (attacking) {
                speed = this.speed * 0.5;
            } else if (status == Status.RUNNING) {
                speed = this.speed;
            } else if (status == Status.ROLLING) {
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
            if (movementVector[0] != 0 || movementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(movementVector);
            }
        } else if (status != Status.DEAD) {   //Enemy is dying
            OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
            status = Status.DYING;
        } else {    //Enemy is dead
            attacking = false;
        }

        switch (status) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % getSprite().RUNNING_FRAMES);
                break;
            case ROLLING:
                break;
            case DYING:
                double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame > getSprite().DYING_FRAMES) {
                    status = Status.DEAD;
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DYING_FRAMES);
                }
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % getSprite().DEAD_FRAMES);
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
        boolean chasing = status != Status.DYING && status != Status.DEAD && distanceToPlayer > 25 && distanceToPlayer < 2000;

        if (!chasing) {
            return new double[]{0 ,0};
        }

        double[] movement = new double[2];
        status = Status.RUNNING;

        if (useDijkstraAlgorithm) {
            if (computePathCoolDown <= 0) {
                computePath();
                computePathCoolDown = computePathPeriod;
            }
            int[] step = pathFindingAlgorithm.getNextStep(getCenterOfMassWorldCoordinates());
            Coordinates stepWorldCoordinates = Coordinates.tileCoordinatesToWorldCoordinates(step[0], step[1]);
            movement = new double[]{
                    stepWorldCoordinates.x - getCenterOfMassWorldCoordinates().x + (TileMap.TILE_WIDTH / 2),
                    stepWorldCoordinates.y - getCenterOfMassWorldCoordinates().y + (TileMap.TILE_HEIGHT / 2)};
            computePathCoolDown -= timeElapsed;
        } else {
            movement[0] = (Player.getInstance().getWorldCoordinates().x - getWorldCoordinates().x);
            movement[1] = (Player.getInstance().getWorldCoordinates().y - getWorldCoordinates().y);
        }

        movement = MathUtils.normalizeVector(movement);

        movement[0] *= timeElapsed;
        movement[1] *= timeElapsed;

        return movement;
    }

    private void computePath() {
        pathFindingAlgorithm = new PathFindingAlgorithm(getCenterOfMassWorldCoordinates(), Player.getInstance().getCenterOfMassWorldCoordinates());
        pathFindingAlgorithm.computeBestPath();
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
        /** CONE ATTACK **/
        double[] pointingVector = new double[]{Player.getInstance().getCenterOfMassWorldCoordinates().x - getCenterOfMassWorldCoordinates().x,
                Player.getInstance().getCenterOfMassWorldCoordinates().y - getCenterOfMassWorldCoordinates().y};

        if (coneAttack == null) {
            coneAttack = new ConeAttack(getCenterOfMassWorldCoordinates(), pointingVector, Math.PI / 6.0,
                    coneAttackLength, coneAttackPeriod, coneAttackCoolDown, coneAttackPower, true, attacking);
        } else {
            coneAttack.update(getCenterOfMassWorldCoordinates(), pointingVector, timeElapsed, attacking);
        }

        /** CIRCLE ATTACK **/
        if (circleAttackCoolDown <= 0) {
            circleAttack = new CircleAttack(new Coordinates(getWorldCoordinates().x - 100 + Math.random() * 200, getWorldCoordinates().y - 100 + Math.random() * 200),
                    50, 500, circleAttackPower, true, true);
            Scene.listOfCircleAttacks.add(circleAttack);
            circleAttackCoolDown = circleAttackPeriod;
        }
        if (circleAttackCoolDown > 0) {
            circleAttackCoolDown -= timeElapsed;
        }
    }

    public void drawAttackFX() {
        if (coneAttack != null && attacking) {
            coneAttack.render();
        }

        /** PATH RENDERING **/
        if (Parameters.isDebugMode() && status != Status.DEAD
                && pathFindingAlgorithm != null
                && pathFindingAlgorithm.getPath() != null
                && pathFindingAlgorithm.getPath().size() > 0) {

            int start = pathFindingAlgorithm.getPath().size() - 1;
            Coordinates cameraCoordinates1;
            Coordinates cameraCoordinates2;

            glDisable(GL_TEXTURE_2D);
            glColor4f(1f, 1f, 1f, 0.5f);
            OpenGLManager.glBegin(GL_LINES);

            //FIXME: We should check why "y" tile is not correct, therefore we have to add "+ 1"
            cameraCoordinates1 = Coordinates.tileCoordinatesToWorldCoordinates(
                    pathFindingAlgorithm.getPath().get(start)[0],
                    pathFindingAlgorithm.getPath().get(start)[1] + 1).toCameraCoordinates(); //FIXME: <-- Here
            for (int i = start - 1; i >= 0; i--) {
                cameraCoordinates2 = Coordinates.tileCoordinatesToWorldCoordinates(
                        pathFindingAlgorithm.getPath().get(i)[0],
                        pathFindingAlgorithm.getPath().get(i)[1] + 1).toCameraCoordinates(); //FIXME: <-- Here
                glVertex2d(cameraCoordinates1.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates1.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());
                glVertex2d(cameraCoordinates2.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates2.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());
                cameraCoordinates1 = cameraCoordinates2;
            }

            glEnd();
            glEnable(GL_TEXTURE_2D);
        }
    }
}

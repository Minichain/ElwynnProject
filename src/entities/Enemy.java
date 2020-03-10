package entities;

import main.*;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import utils.MathUtils;
import utils.Utils;

import static org.lwjgl.opengl.GL11.*;

public class Enemy extends DynamicGraphicEntity {
    private static Texture spriteSheet;
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
    private int computePathPeriod = 500;
    private int computePathCoolDown = 0;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    public Enemy(int x, int y) {
        super(x, y, x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        health = 2500f;
        speed = 0.075;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        Scene.getInstance().getListOfEntities().add(this);
    }

    @Override
    public Sprite getSprite() {
        return SpriteManager.getInstance().ENEMY;
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), true);
    }

    @Override
    public void update(long timeElapsed) {
        setPreviousWorldCoordinates(getWorldCoordinates());
        if (health > 0) {
            status = Status.IDLE;
            distanceToPlayer = MathUtils.module(getWorldCoordinates(), Player.getInstance().getWorldCoordinates());
            attacking = distanceToPlayer < coneAttackLength && Player.getInstance().getStatus() != Player.Status.DEAD;

            double[] movement = computeMovementVector(timeElapsed, speed);
            attack(timeElapsed);

            int distanceFactor = 4;
            boolean horizontalCollision = TileMap.checkCollisionWithTile((int)(getWorldCoordinates().x + movement[0] * distanceFactor), (int)(getWorldCoordinates().y));
            boolean verticalCollision = TileMap.checkCollisionWithTile((int)(getWorldCoordinates().x), (int)(getWorldCoordinates().y + movement[1] * distanceFactor));
            if (!horizontalCollision) {
                getWorldCoordinates().x += movement[0];
            }
            if (!verticalCollision) {
                getWorldCoordinates().y += movement[1];
            }

            displacementVector = new double[]{getWorldCoordinates().x - getPreviousWorldCoordinates().x, getWorldCoordinates().y - getPreviousWorldCoordinates().y};

            if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If Player is moving
                directionFacing = Utils.checkDirectionFacing(displacementVector);
                status = Status.RUNNING;
            }
        } else if (status != Status.DEAD) {
            status = Status.DYING;
        } else {
            attacking = false;
        }

        switch (status) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % getSprite().RUNNING_FRAMES);
                break;
            case JUMPING:
                break;
            case DYING:
                double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame > 1) {
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

    public double[] computeMovementVector(long timeElapsed, double speed) {
        boolean chasing = status != Status.DYING && status != Status.DEAD && distanceToPlayer > 25 && distanceToPlayer < 2000;

        if (!chasing) {
            return new double[]{0 ,0};
        }

        double[] movement = new double[2];

        if (useDijkstraAlgorithm) {
            if (computePathCoolDown <= 0) {
                computePath();
                computePathCoolDown = computePathPeriod;
            }
            int[] step = pathFindingAlgorithm.getNextStep();
            movement = new double[]{step[0], step[1]};
            computePathCoolDown -= timeElapsed;
        } else {
            movement[0] = (Player.getInstance().getWorldCoordinates().x - getWorldCoordinates().x);
            movement[1] = (Player.getInstance().getWorldCoordinates().y - getWorldCoordinates().y);
        }

        movement = MathUtils.normalizeVector(movement);

        if (attacking) {
            speed *= 0.5;
        }

        movement[0] *= timeElapsed * speed;
        movement[1] *= timeElapsed * speed;

        return movement;
    }


    private void computePath() {
        pathFindingAlgorithm = new PathFindingAlgorithm(getWorldCoordinates(), Player.getInstance().getWorldCoordinates());
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
        double[] pointingVector = new double[]{Player.getInstance().getWorldCoordinates().x - getWorldCoordinates().x,
                Player.getInstance().getWorldCoordinates().y - getWorldCoordinates().y};

        if (coneAttack == null) {
            coneAttack = new ConeAttack(getWorldCoordinates(), pointingVector, Math.PI / 6.0, coneAttackLength, coneAttackPeriod, coneAttackCoolDown, coneAttackPower, true, attacking);
        } else {
            coneAttack.update(getWorldCoordinates(), pointingVector, timeElapsed, attacking);
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
        if (Parameters.isDebugMode() && status != Status.DEAD && pathFindingAlgorithm != null && pathFindingAlgorithm.getPath() != null) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(1f, 1f, 1f, 0.5f);
            OpenGLManager.glBegin(GL_LINES);

            Coordinates startingCoordinates = pathFindingAlgorithm.initialCoordinates.toCameraCoordinates();
            int start = pathFindingAlgorithm.getPath().size() - 1;
            for (int i = start; i >= 0; i--) {
                glVertex2d(startingCoordinates.x, startingCoordinates.y);
                startingCoordinates.x += pathFindingAlgorithm.getPath().get(i)[0] * TileMap.TILE_WIDTH * Camera.getZoom();
                startingCoordinates.y += pathFindingAlgorithm.getPath().get(i)[1] * TileMap.TILE_HEIGHT * Camera.getZoom();
                glVertex2d(startingCoordinates.x, startingCoordinates.y);
            }

            glEnd();

            glEnable(GL_TEXTURE_2D);
        }
    }
}

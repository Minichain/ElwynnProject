package entities;

import audio.OpenALManager;
import items.ItemType;
import main.*;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import text.FloatingTextEntity;
import utils.ArrayUtils;
import utils.MathUtils;
import utils.Utils;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Enemy extends LivingDynamicGraphicEntity {
    public static String ENTITY_CODE = "enemy";

    private Utils.DirectionFacing directionFacing;
    private Status status;
    public enum Status {
        IDLE, RUNNING, DYING, DEAD, ATTACKING, CHASING;
    }

    /** ATTACK **/
    private int attack01Period = 500;
    private int attack01CoolDown = 0;
    private float attack01Power = 300f;
    private float attack01ManaCost = 0.1f;

    private static float attackRange = 10f;

    private float hurtPeriod = 100f;
    private float hurtCoolDown = 0f;

    /** PATH FINDING **/
    private enum ChasingMode {
        STRAIGHT_LINE, DIJKSTRA
    }
    private ChasingMode chasingMode;
    private PathFindingAlgorithm pathFindingAlgorithm;
    private double distanceToGoal;
    private int computePathPeriod = 500;
    private int computePathCoolDown = 0;
    private double chasingRange = 150;

    private final int runningPeriod = 2000;
    private int runningCoolDown = 0;
    private double[] runningVector;

    MusicalMode musicalMode;

    Coordinates goalCoordinates;

    public Enemy(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        health = 2500f;
        speed = Math.random() * 0.025 + 0.015;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        chasingMode = ChasingMode.STRAIGHT_LINE;
        setSprite(SpriteManager.getInstance().ZOMBIE01);
        Scene.getInstance().getListOfGraphicEntities().add(this);
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
    public String getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void onDying() {
        OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
        int numOfCoinsToDrop = (int) (MathUtils.random(1, 4));
        float areaOfDrop = 25f;
        for (int i = 0; i < numOfCoinsToDrop; i++) {
            new ItemEntity((int) ((getWorldCoordinates().x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    (int) ((getWorldCoordinates().y - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    ItemType.GOLD_COIN);
        }

        if (MathUtils.random(0, 10) < 2) {
            new ItemEntity((int) ((getWorldCoordinates().x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    (int) ((getWorldCoordinates().y - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    ItemType.HEALTH_POTION);
        }

        if (MathUtils.random(0, 10) < 2) {
            new ItemEntity((int) ((getWorldCoordinates().x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    (int) ((getWorldCoordinates().y - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    ItemType.MANA_POTION);
        }

        if (MathUtils.random(0, 10) < 2) {
            new ItemEntity((int) ((getWorldCoordinates().x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    (int) ((getWorldCoordinates().y - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    ItemType.HASTE_POTION);
        }

        if (MathUtils.random(0, 10) < 2) {
            new ItemEntity((int) ((getWorldCoordinates().x - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    (int) ((getWorldCoordinates().y - areaOfDrop / 2) + (Math.random() * areaOfDrop)),
                    ItemType.WOOD);
        }
    }

    @Override
    public void hurt(float damage) {
        if (hurtCoolDown >= 0f) {
            return;
        }
        hurtCoolDown = hurtPeriod;

        OpenALManager.playSound(OpenALManager.SOUND_ENEMY_HURT_01);
        float previousHealth = getHealth();
        setHealth(previousHealth - damage);
        if (getHealth() <= 0 && previousHealth > 0) onDying();
        String text = String.valueOf((int) damage);
        float scale;
        Color color;
        if (damage < 200f) {
            scale = 2f;
            color = new Color(1f, 1f, 1f);
        } else {
            scale = 4f;
            color = new Color(1f, 0.75f, 0.5f);
        }
        new FloatingTextEntity(this.getWorldCoordinates().x, this.getWorldCoordinates().y, text, color, 1.25, new double[]{0, -1}, scale);
    }

    @Override
    public void update(long timeElapsed) {
        if (health > 0) {   //Enemy is alive
            if (!Player.getInstance().isDead()) {
                goalCoordinates = Player.getInstance().getCenterOfMassWorldCoordinates();
                distanceToGoal = MathUtils.module(getCenterOfMassWorldCoordinates(), goalCoordinates);
            } else {
                goalCoordinates = new Coordinates(-1, -1);
                distanceToGoal = Double.MAX_VALUE;
            }

            if (distanceToGoal <= attackRange) {
                status = Status.ATTACKING;
            } else if (distanceToGoal < chasingRange) {
                status = Status.CHASING;
            } else if (runningCoolDown > 0) {
                status = Status.RUNNING;
                runningCoolDown -= timeElapsed;
            } else {
                status = Status.IDLE;
            }

            if (runningCoolDown <= 0 && status == Status.IDLE && Math.random() < 0.01) {
                status = Status.RUNNING;
                runningVector = new double[]{Math.random() * 2 - 1, Math.random() * 2 - 1};
                runningCoolDown = runningPeriod;
            }

//            Log.l("status: " + status);
//            Log.l("distanceToGoal: " + distanceToGoal);
            if (status != Status.IDLE) {
                if (status == Status.CHASING) {
                    if (checkObstacles(timeElapsed)) {
                        chasingMode = ChasingMode.DIJKSTRA;
                    } else {
                        chasingMode = ChasingMode.STRAIGHT_LINE;
                    }
                }

                computeMovementVector(timeElapsed);

                movementVector[0] = movementVectorNormalized[0] * timeElapsed;
                movementVector[1] = movementVectorNormalized[1] * timeElapsed;

                updateAttack(timeElapsed);

                /** CHECK COLLISIONS **/
                boolean horizontalCollision = checkHorizontalCollision(movementVectorNormalized, 6);
                boolean verticalCollision = checkVerticalCollision(movementVectorNormalized, 6);

                /** MOVE ENTITY **/
                double speed = 0.0;
                if (status == Status.RUNNING) {
                    speed = this.speed * 0.5;
                } else if (status == Status.CHASING) {
                    speed = this.speed;
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
                if (hurtCoolDown >= 0) hurtCoolDown -= timeElapsed;
            }
        } else if (status != Status.DEAD) {   //Enemy is dying
            status = Status.DYING;
        } else {
            //Enemy is dead
        }

        double frame;
        switch (status) {
            case IDLE:
            default:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
            case CHASING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().RUNNING_FRAMES);
                break;
            case ATTACKING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.0115)) % getSprite().ATTACKING_FRAMES;
                setSpriteCoordinateFromSpriteSheetX(frame);
                break;
            case DYING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.0075));
                if (frame > getSprite().DYING_FRAMES) {
                    status = Status.DEAD;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DYING_FRAMES);
                }
                break;
            case DEAD:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DEAD_FRAMES);
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    private int checkObstaclesPeriod = 500;
    private int checkObstaclesCoolDown = 0;
    private boolean obstacleDetected;

    /**
     * Throws an ray from the enemy entity to its goal and checks if there is any obstacle in its way.
     * @return true if any obstacle has been detected.
     **/
    private boolean checkObstacles(long timeElapsed) {
        checkObstaclesCoolDown -= timeElapsed;
        if (checkObstaclesCoolDown > 0) {
            return obstacleDetected;
        }

        obstacleDetected = false;
        int numberOfStepsToCheck = (int) Math.ceil(distanceToGoal / TileMap.TILE_WIDTH);
        if (numberOfStepsToCheck < 10) numberOfStepsToCheck = 10;
        double stepDistance = distanceToGoal / (double) numberOfStepsToCheck;
        double[] vector = new double[]{goalCoordinates.x - getWorldCoordinates().x, goalCoordinates.y - getWorldCoordinates().y};
        vector = MathUtils.normalizeVector(vector);
        for (int i = 0; i < numberOfStepsToCheck; i++) {
            Coordinates coordinatesToCheck = new Coordinates(getWorldCoordinates().x + vector[0] * stepDistance * i, getWorldCoordinates().y + vector[1] * stepDistance * i);
            if (TileMap.checkCollisionWithTile((int) coordinatesToCheck.x, (int) coordinatesToCheck.y) || Scene.getInstance().checkCollisionWithEntities(coordinatesToCheck)) {
                obstacleDetected = true;
                break;
            }
        }
        checkObstaclesCoolDown = checkObstaclesPeriod;
        return obstacleDetected;
    }

    private boolean checkHorizontalCollision(double[] movement, double distanceFactor) {
        Coordinates coordinatesToCheck = new Coordinates(getCenterOfMassWorldCoordinates().x + movement[0] * distanceFactor, getCenterOfMassWorldCoordinates().y);
        return TileMap.checkCollisionWithTile((int) coordinatesToCheck.x, (int) coordinatesToCheck.y) || Scene.getInstance().checkCollisionWithEntities(coordinatesToCheck);
    }

    private boolean checkVerticalCollision(double[] movement, double distanceFactor) {
        Coordinates coordinatesToCheck = new Coordinates(getCenterOfMassWorldCoordinates().x, getCenterOfMassWorldCoordinates().y + movement[1] * distanceFactor);
        return TileMap.checkCollisionWithTile((int) coordinatesToCheck.x, (int) coordinatesToCheck.y) || Scene.getInstance().checkCollisionWithEntities(coordinatesToCheck);
    }

    public void computeMovementVector(long timeElapsed) {
        movementVector = new double[]{0, 0};
        movementVectorNormalized = new double[]{0, 0};

        if (status == Status.CHASING) {
            if (chasingMode == ChasingMode.DIJKSTRA) {
                if (computePathCoolDown <= 0) {
                    computePath();
                    computePathCoolDown = computePathPeriod;
                }
                computePathCoolDown -= timeElapsed;

                if (pathFindingAlgorithm.getPath() != null && pathFindingAlgorithm.getPath().size() > 0) {
                    int[] step = pathFindingAlgorithm.getNextStep(getCenterOfMassWorldCoordinates());
                    Coordinates stepWorldCoordinates = Coordinates.tileCoordinatesToWorldCoordinates(step[0], step[1]);
                    movementVector = new double[]{
                            stepWorldCoordinates.x - getCenterOfMassWorldCoordinates().x + (TileMap.TILE_WIDTH / 2.0),
                            stepWorldCoordinates.y - getCenterOfMassWorldCoordinates().y + (TileMap.TILE_HEIGHT / 2.0)};
                } else {
                    status = Status.IDLE;
                }
            } else if (chasingMode == ChasingMode.STRAIGHT_LINE) {
                movementVector[0] = (Player.getInstance().getWorldCoordinates().x - getWorldCoordinates().x);
                movementVector[1] = (Player.getInstance().getWorldCoordinates().y - getWorldCoordinates().y);
            }
        } else if (status == Status.RUNNING) {
            movementVector = runningVector;
        }

        movementVectorNormalized = MathUtils.normalizeVector(movementVector);
    }

    private void computePath() {
        //Computing Path Finding could take from 0.5 milliseconds to several milliseconds (> 5 ms) depending on the complexity of the path
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
            case ATTACKING:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(14);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(15);
                } else if (directionFacing == Utils.DirectionFacing.UP) {
                    setSpriteCoordinateFromSpriteSheetY(16);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(17);
                }
                break;
            case RUNNING:
            case CHASING:
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

    private void updateAttack(long timeElapsed) {
        /** MUSICAL NOTE ATTACK **/
        if (status == Status.ATTACKING) {
            if (attack01CoolDown <= 0) {
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), Player.getInstance().getCenterOfMassWorldCoordinates()) < attackRange) {
                    Player.getInstance().hurt(100f);
                }
                attack01CoolDown = attack01Period;
            }
        }
        if (attack01CoolDown > 0) {
            attack01CoolDown -= timeElapsed;
        }
    }

    public void drawAttackFX() {
        /** PATH RENDERING **/
        if (Parameters.isDebugMode() && status == Status.CHASING) {
            if (chasingMode == ChasingMode.DIJKSTRA
                    && pathFindingAlgorithm != null
                    && pathFindingAlgorithm.getPath() != null
                    && pathFindingAlgorithm.getPath().size() > 0) {

                int start = pathFindingAlgorithm.getPath().size() - 1;
                Coordinates cameraCoordinates1;
                Coordinates cameraCoordinates2;

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
            } else if (chasingMode == ChasingMode.STRAIGHT_LINE && goalCoordinates != null) {
                glColor4f(1f, 1f, 1f, 0.5f);
                OpenGLManager.glBegin(GL_LINES);

                Coordinates cameraCoordinates1 = getCenterOfMassCameraCoordinates();
                Coordinates cameraCoordinates2 = goalCoordinates.toCameraCoordinates();

                glVertex2d(cameraCoordinates1.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates1.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());
                glVertex2d(cameraCoordinates2.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates2.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());

                glEnd();
            }
        }
    }

    public float getWeakness(MusicalMode musicalMode) {
        return 1f / (ArrayUtils.compare(this.musicalMode.getNotes(), musicalMode.getNotes()) + 1);
    }

    public float getWeakness(MusicalNote musicalNote) {
        if (!ArrayUtils.contains(this.musicalMode.getNotes(), musicalNote)) {
            return 1f;
        } else {
            return 0.15f;
        }
    }
}

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
        IDLE, RUNNING, ROLLING, DYING, DEAD, ATTACKING, CHASING;
    }

    /** ATTACK **/
    private int attack01Period = 500;
    private int attack01CoolDown = 0;
    private float attack01Power = 300f;
    private float attack01ManaCost = 0.1f;

    private int circleAttackPeriod = 10000;
    private int circleAttackCoolDown = 0;
    private float circleAttackPower = 100f;

    /** PATH FINDING **/
    private enum ChasingMode {
        STRAIGHT_LINE, DIJKSTRA
    }
    private ChasingMode chasingMode;
    private PathFindingAlgorithm pathFindingAlgorithm;
    private double distanceToGoal;
    private int computePathPeriod = 500;
    private int computePathCoolDown = 0;

    private int checkObstaclesPeriod = 500;
    private int checkObstaclesCoolDown = 0;
    private boolean obstacleDetected;

    MusicalMode musicalMode;

    Coordinates goalCoordinates;

    public Enemy(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        health = 2500f;
        speed = Math.random() * 0.05 + 0.035;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        chasingMode = ChasingMode.STRAIGHT_LINE;
        int enemyType = (int) (MathUtils.random(0, Integer.MAX_VALUE)) % MusicalMode.values().length;
        switch (enemyType) {
            case 0:
                musicalMode = MusicalMode.IONIAN;
                setSprite(SpriteManager.getInstance().GHOST_IONIAN);
                break;
            case 1:
                musicalMode = MusicalMode.DORIAN;
                setSprite(SpriteManager.getInstance().GHOST_DORIAN);
                break;
            case 2:
                musicalMode = MusicalMode.PHRYGIAN;
                setSprite(SpriteManager.getInstance().GHOST_PHRYGIAN);
                break;
            case 3:
                musicalMode = MusicalMode.LYDIAN;
                setSprite(SpriteManager.getInstance().GHOST_LYDIAN);
                break;
            case 4:
                musicalMode = MusicalMode.MIXOLYDIAN;
                setSprite(SpriteManager.getInstance().GHOST_MIXOLYDIAN);
                break;
            case 5:
                musicalMode = MusicalMode.AEOLIAN;
                setSprite(SpriteManager.getInstance().GHOST_AEOLIAN);
                break;
            case 6:
            default:
                musicalMode = MusicalMode.LOCRIAN;
                setSprite(SpriteManager.getInstance().GHOST_LOCRIAN);
                break;
        }
        Scene.getInstance().getListOfGraphicEntities().add(this);
    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 0.5f);
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
            goalCoordinates = Player.getInstance().getCenterOfMassWorldCoordinates();
            distanceToGoal = MathUtils.module(getCenterOfMassWorldCoordinates(), goalCoordinates);

            if (status != Status.ROLLING && distanceToGoal < 2000.0) {
                status = Status.CHASING;
            }

            if (status == Status.IDLE) {
                return;
            }

//            Log.l("status: " + status);
//            Log.l("distanceToGoal: " + distanceToGoal);

            if (status != Status.ROLLING) {
                checkObstacles(timeElapsed);
                if (obstacleDetected) {
                    chasingMode = ChasingMode.DIJKSTRA;
                    if (status == Status.ATTACKING) {
                        status = Status.CHASING;
                    }
                } else {
                    chasingMode = ChasingMode.STRAIGHT_LINE;
                    if (status == Status.CHASING && distanceToGoal <= 125) {
                        status = Status.ATTACKING;
                    }
                }
            }

            if ((status == Status.CHASING || status == Status.RUNNING) && Math.random() < 0.01) {
                roll();
            }

            if (status != Status.ROLLING) {
                computeMovementVector(timeElapsed);
            }
            movementVector[0] = movementVectorNormalized[0] * timeElapsed;
            movementVector[1] = movementVectorNormalized[1] * timeElapsed;

            updateAttack(timeElapsed);

            /** CHECK COLLISIONS **/
            boolean horizontalCollision = checkHorizontalCollision(movementVectorNormalized, 6);
            boolean verticalCollision = checkVerticalCollision(movementVectorNormalized, 6);

            /** MOVE ENTITY **/
            double speed = 0.0;
            if (status == Status.ATTACKING) {
                speed = this.speed * 0.5;
            } else if (status == Status.RUNNING || status == Status.CHASING) {
                speed = this.speed;
            } else if (status == Status.ROLLING) {
                speed = this.speed * 3.0;
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
            case ROLLING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.005));
                if (frame >= getSprite().ROLLING_FRAMES) {
                    status = Status.IDLE;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().ROLLING_FRAMES);
                }
                break;
            case ATTACKING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().ATTACKING_FRAMES);
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
        if (status != Status.DYING
                && status != Status.DEAD
                && status != Status.ATTACKING
                && distanceToGoal > 125
                && distanceToGoal < 2000) {
            status = Status.CHASING;
        }

        movementVector = new double[]{0, 0};
        movementVectorNormalized = new double[]{0, 0};

        if (status != Status.CHASING) {
            return;
        }

        if (chasingMode == ChasingMode.DIJKSTRA) {
            if (computePathCoolDown <= 0) {
                computePath();
                computePathCoolDown = computePathPeriod;
            }
            int[] step = pathFindingAlgorithm.getNextStep(getCenterOfMassWorldCoordinates());
            Coordinates stepWorldCoordinates = Coordinates.tileCoordinatesToWorldCoordinates(step[0], step[1]);
            movementVector = new double[]{
                    stepWorldCoordinates.x - getCenterOfMassWorldCoordinates().x + (TileMap.TILE_WIDTH / 2),
                    stepWorldCoordinates.y - getCenterOfMassWorldCoordinates().y + (TileMap.TILE_HEIGHT / 2)};
            computePathCoolDown -= timeElapsed;
        } else if (chasingMode == ChasingMode.STRAIGHT_LINE) {
            movementVector[0] = (Player.getInstance().getWorldCoordinates().x - getWorldCoordinates().x);
            movementVector[1] = (Player.getInstance().getWorldCoordinates().y - getWorldCoordinates().y);
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
                setSpriteCoordinateFromSpriteSheetY(0);
                break;
            case ROLLING:
                setSpriteCoordinateFromSpriteSheetY(5);
                break;
            case ATTACKING:
                setSpriteCoordinateFromSpriteSheetY(6);
                break;
            case RUNNING:
            case CHASING:
                if (movementVectorNormalized[0] < 0) {
                    setSpriteCoordinateFromSpriteSheetY(2);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(1);
                }
                break;
            case DYING:
                setSpriteCoordinateFromSpriteSheetY(3);
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetY(4);
                break;
        }
    }

    public Status getStatus() {
        return status;
    }

    private void updateAttack(long timeElapsed) {
        /** MUSICAL NOTE ATTACK **/
        double[] pointingVector = new double[]{Player.getInstance().getCenterOfMassWorldCoordinates().x - getCenterOfMassWorldCoordinates().x,
                Player.getInstance().getCenterOfMassWorldCoordinates().y - getCenterOfMassWorldCoordinates().y};

        if (status == Status.ATTACKING) {
            if (attack01CoolDown <= 0) {
                attack01CoolDown = attack01Period;
            }
        }
        if (attack01CoolDown > 0) {
            attack01CoolDown -= timeElapsed;
        }
    }

    public void drawAttackFX() {
        /** PATH RENDERING **/
        if (Parameters.isDebugMode() && status != Status.DEAD) {
            if (chasingMode == ChasingMode.DIJKSTRA
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
            } else if (chasingMode == ChasingMode.STRAIGHT_LINE && goalCoordinates != null) {
                glDisable(GL_TEXTURE_2D);
                glColor4f(1f, 1f, 1f, 0.5f);
                OpenGLManager.glBegin(GL_LINES);

                Coordinates cameraCoordinates1 = getCenterOfMassCameraCoordinates();
                Coordinates cameraCoordinates2 = goalCoordinates.toCameraCoordinates();

                glVertex2d(cameraCoordinates1.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates1.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());
                glVertex2d(cameraCoordinates2.x + (TileMap.TILE_WIDTH / 2.0) * Camera.getZoom(), cameraCoordinates2.y - (TileMap.TILE_HEIGHT / 2.0) * Camera.getZoom());

                glEnd();
                glEnable(GL_TEXTURE_2D);
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

    public void roll() {
        if (status != Status.ATTACKING && MathUtils.module(getWorldCoordinates(), Player.getInstance().getWorldCoordinates()) < 150f) {
            status = Status.ROLLING;
            setSpriteCoordinateFromSpriteSheetX(0);
            OpenALManager.playSound(OpenALManager.SOUND_ROLLING_01);
        }
    }
}

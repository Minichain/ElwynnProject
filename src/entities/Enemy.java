package entities;

import audio.OpenALManager;
import main.*;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import text.FloatingTextEntity;
import utils.MathUtils;
import utils.Utils;

import static entities.MusicalMode.IONIAN;
import static org.lwjgl.opengl.GL11.*;

public class Enemy extends LivingDynamicGraphicEntity {
    public static byte ENTITY_CODE = 51;
    private Utils.DirectionFacing directionFacing;
    private Status status;
    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD, ATTACKING, CHASING;
    }

    /** ATTACK **/
    private int attack01Period = 500;
    private int attack01CoolDown = 0;
    private float attack01Power = 125f;
    private float attack01ManaCost = 0.1f;

    private CircleAttack circleAttack;
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
    private int computePathPeriod = 600;
    private int computePathCoolDown = 0;

    private int checkObstaclesPeriod = 250;
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
        speed = Math.random() * 0.06 + 0.04;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        chasingMode = ChasingMode.STRAIGHT_LINE;
        int enemyType = (int) (Math.random() * Integer.MAX_VALUE) % MusicalMode.values().length;
        switch (enemyType) {
            case 0:
            default:
                musicalMode = IONIAN;
                setSprite(SpriteManager.getInstance().ENEMY01);
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
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void hurt(float damage) {
        OpenALManager.playSound(OpenALManager.SOUND_ENEMY_HURT_01);
        setHealth(getHealth() - damage);
        String text = String.valueOf((int) damage);
        new FloatingTextEntity(this.getWorldCoordinates().x, this.getWorldCoordinates().y, text, true, true, false);
    }

    @Override
    public void update(long timeElapsed) {
        if (health > 0) {   //Enemy is alive
            goalCoordinates = Player.getInstance().getCenterOfMassWorldCoordinates();
            distanceToGoal = MathUtils.module(getCenterOfMassWorldCoordinates(), goalCoordinates);

            if (distanceToGoal < 2000.0) {
                status = Status.CHASING;
            }

            if (status == Status.IDLE) {
                return;
            }

//            System.out.println("status: " + status);
//            System.out.println("distanceToGoal: " + distanceToGoal);

            checkObstacles(timeElapsed);

            if (obstacleDetected) {
                chasingMode = ChasingMode.DIJKSTRA;
                if (status == Status.ATTACKING) {
                    status = Status.CHASING;
                }
            } else {
                chasingMode = ChasingMode.STRAIGHT_LINE;
                if (status == Status.CHASING && distanceToGoal <= 250) {
                    status = Status.ATTACKING;
                }
            }

            movementVector = computeMovementVector(timeElapsed);
            updateAttack(timeElapsed);

            /** CHECK COLLISIONS **/
            double distanceFactor = timeElapsed / 32.0;
            boolean horizontalCollision = checkHorizontalCollision(movementVector, distanceFactor);
            boolean verticalCollision = checkVerticalCollision(movementVector, distanceFactor);

            /** MOVE ENTITY **/
            double speed = 0.0;
            if (status == Status.ATTACKING) {
                speed = this.speed * 0.5;
            } else if (status == Status.RUNNING || status == Status.CHASING) {
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
        } else {
            //Enemy is dead
        }

        double frame;
        switch (status) {
            case IDLE:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
            case CHASING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().RUNNING_FRAMES);
                break;
            case ROLLING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                if (frame >= getSprite().ROLLING_FRAMES) {
                    status = Status.IDLE;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().ROLLING_FRAMES);
                }
                break;
            case DYING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                if (frame >= getSprite().DYING_FRAMES) {
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
            case ATTACKING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame >= getSprite().ATTACKING_FRAMES) {
                    status = Status.IDLE;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().ATTACKING_FRAMES);
                }
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
        int numberOfStepsToCheck = 25;
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

    public double[] computeMovementVector(long timeElapsed) {
        if (status != Status.DYING
                && status != Status.DEAD
                && status != Status.ATTACKING
                && distanceToGoal > 250
                && distanceToGoal < 2000) {
            status = Status.CHASING;
        }

        if (status != Status.CHASING) {
            return new double[]{0 ,0};
        }

        double[] movement = new double[2];

        if (chasingMode == ChasingMode.DIJKSTRA) {
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
        } else if (chasingMode == ChasingMode.STRAIGHT_LINE) {
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
                MusicalNoteGraphicEntity musicalNoteGraphicEntity = new MusicalNoteGraphicEntity(getCenterOfMassWorldCoordinates(), pointingVector,
                        0.3, musicalMode, attack01Power, 1500.0, true);
                Scene.getInstance().getListOfMusicalNoteGraphicEntities().add(musicalNoteGraphicEntity);
                attack01CoolDown = attack01Period;
            }
        }
        if (attack01CoolDown > 0) {
            attack01CoolDown -= timeElapsed;
        }

        /** CIRCLE ATTACK **/
        if (status == Status.ATTACKING) {
            if (circleAttackCoolDown <= 0) {
                circleAttack = new CircleAttack(new Coordinates(getWorldCoordinates().x - 100 + Math.random() * 200, getWorldCoordinates().y - 100 + Math.random() * 200),
                        50, 500, circleAttackPower, true, true, musicalMode);
                Scene.listOfCircleAttacks.add(circleAttack);
                circleAttackCoolDown = circleAttackPeriod;
            }
        }
        if (circleAttackCoolDown > 0) {
            circleAttackCoolDown -= timeElapsed;
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
        if (this.musicalMode == musicalMode) {
            return 1f;
        } else {
            return 0.15f;
        }
    }
}

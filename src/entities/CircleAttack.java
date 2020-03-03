package entities;

import audio.OpenALManager;
import main.Coordinates;
import main.Parameters;
import utils.MathUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class CircleAttack {
    private Coordinates center;
    private double radius;
    private int numberOfVertices = 16;
    private double angleStep = 2.0 * Math.PI / (double) numberOfVertices;
    private boolean attacking;
    private int attackPeriod;
    private int attackCoolDown;
    private float attackPower;
    private boolean enemyAttack;
    private double timeLiving = 0;
    private double timeToLive = 5000;

    public CircleAttack(Coordinates center, double radius, int attackPeriod, int attackCoolDown, float attackPower, boolean enemyAttack, boolean attacking) {
        this.center = center;
        this.radius = radius;
        this.enemyAttack = enemyAttack;
        this.attackPeriod = attackPeriod;
        this.attackCoolDown = attackCoolDown;
        this.attackPower = attackPower;
        update(0, attacking);
    }

    public void update(long timeElapsed, boolean attacking) {
        timeLiving += timeElapsed;
        this.attacking = attacking;
        Particle particle;
        double[] velocityVector;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates particleCoordinates;
            double amountOfParticles = 1;
            double randomAngle;
            double[] generationVector;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = Math.random() * 2.0 * Math.PI;
                generationVector = new double[]{radius * Math.random(), 0};
                generationVector = MathUtils.rotateVector(generationVector, randomAngle);
                velocityVector = new double[]{0, -0.1};
                particleCoordinates = new Coordinates(this.center.x + generationVector[0], this.center.y + generationVector[1]);
                if (enemyAttack) {
                    particle = new Particle(particleCoordinates, velocityVector, (int) (4 * Camera.getZoom()), 1f, 0f, 0f);
                } else {
                    particle = new Particle(particleCoordinates, velocityVector, (int) (4 * Camera.getZoom()), 1f, 1f, 1f);
                }
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        /** DEAL DAMAGE **/
        if (!attacking || attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }

        Entity entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            entity = Scene.getInstance().getListOfEntities().get(i);
            double[] entityCameraCoords = entity.getCoordinates().toCameraCoordinates();
            float damage = (float) (attackPower + (Math.random() * 10));
            double radius = this.radius * Camera.getZoom();
            if (entity instanceof Enemy && !enemyAttack) {
                if (((Enemy) entity).getStatus() != Enemy.Status.DEAD
                        && MathUtils.isPointInsideCircle(new double[]{entityCameraCoords[0], entityCameraCoords[1]}, this.center.toCameraCoordinates(), radius)) {
                    ((Enemy) entity).setHealth(((Enemy) entity).getHealth() - damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCoordinates().x, entity.getCoordinates().y, text, true, true, false);
                }
            } else if (entity instanceof Player && enemyAttack) {
                if (((Player) entity).getStatus() != Player.Status.DEAD
                        && MathUtils.isPointInsideCircle(new double[]{entityCameraCoords[0], entityCameraCoords[1]}, this.center.toCameraCoordinates(), radius)) {
                    ((Player) entity).setHealth(((Player) entity).getHealth() - damage);
                    OpenALManager.playSound(OpenALManager.SOUND_PLAYER_HURT_01);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCoordinates().x, entity.getCoordinates().y, text, true, true, true);
                }
            }
        }

        attackCoolDown = attackPeriod;
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);

        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_BLEND);
            glBegin(GL_LINES);
            glLineWidth(4);
            if (enemyAttack) {
                glColor4f(1.0f, 0f, 0f, 1.0f);
            } else {
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }

            /** CIRCLE OUTLINE **/
            double angle = 0;
            double[] centerCameraCoordinates = center.toCameraCoordinates();
            for (int i = 0; i < numberOfVertices; i++) {
                double radius = this.radius * Camera.getZoom();
                double x1 = (Math.cos(angle) * radius) + centerCameraCoordinates[0];
                double y1 = (Math.sin(angle) * radius) + centerCameraCoordinates[1];
                double x2 = (Math.cos(angle + angleStep) * radius) + centerCameraCoordinates[0];
                double y2 = (Math.sin(angle + angleStep) * radius) + centerCameraCoordinates[1];
                glVertex2d(x1, y1);
                glVertex2d(x2, y2);
                angle += angleStep;
            }

            glEnd();
            glEnable(GL_BLEND);
        }
    }

    public boolean isDead() {
        return timeLiving > timeToLive;
    }
}


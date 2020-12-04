package entities;

import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

import java.awt.*;

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
    private MusicalMode musicalMode;
    private LightSource lightSource;
    private float lightSourceIntensity = 50f;

    public CircleAttack(Coordinates center, double radius, int attackPeriod, float attackPower, boolean enemyAttack, boolean attacking, MusicalMode musicalMode) {
        this.center = center;
        this.radius = radius;
        this.enemyAttack = enemyAttack;
        this.attackPeriod = attackPeriod;
        this.attackPower = attackPower;
        this.attackCoolDown = 0;
        this.musicalMode = musicalMode;
        this.lightSource = new LightSource(center, lightSourceIntensity, musicalMode.getColor());
        Scene.getInstance().getListOfLightSources().add(lightSource);
        update(0, attacking);
    }

    public void update(long timeElapsed, boolean attacking) {
        timeLiving += timeElapsed;
        this.attacking = attacking;

        if (isDead()) {
            onDestroy();
            return;
        }

        lightSource.setIntensity(lightSourceIntensity * MathUtils.customExponentialFunction(1f - 1f * (float) (timeLiving / timeToLive)));

        Particle particle;
        double[] velocityVector;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates particleCoordinates;
            double amountOfParticles = 0.01;
            double randomAngle;
            double[] generationVector;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = MathUtils.random(0, 2.0 * Math.PI);
                generationVector = new double[]{radius * Math.random(), 0};
                generationVector = MathUtils.rotateVector(generationVector, randomAngle);
                velocityVector = new double[]{0, -0.1};
                particleCoordinates = new Coordinates(this.center.x + generationVector[0], this.center.y + generationVector[1]);
                if (enemyAttack) {
                    particle = new Particle(particleCoordinates, velocityVector, 0.25, 2, new Color(255, 0, 0), false);
                } else {
                    particle = new Particle(particleCoordinates, velocityVector, 0.25, 2, musicalMode.getColor(), false);
                }
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        /** DEAL DAMAGE **/
        if (!attacking || attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }

        GraphicEntity entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            entity = Scene.getInstance().getListOfEntities().get(i);
            float damage = (float) (attackPower + (MathUtils.random(0, 10)));
            double radius = this.radius * Camera.getZoom();
            if (entity instanceof Enemy && !enemyAttack) {
                if (((Enemy) entity).getStatus() != Enemy.Status.DEAD
                        && MathUtils.isPointInsideCircle(entity.getCenterOfMassCameraCoordinates(), this.center.toCameraCoordinates(), radius)) {
                    damage *= ((Enemy) entity).getWeakness(musicalMode);
                    ((Enemy) entity).hurt(damage);
                }
            } else if (entity instanceof Player && enemyAttack) {
                if (((Player) entity).getStatus() != Player.Status.DEAD
                        && ((Player) entity).getStatus() != Player.Status.ROLLING
                        && MathUtils.isPointInsideCircle(entity.getCenterOfMassCameraCoordinates(), this.center.toCameraCoordinates(), radius)) {
                    ((Player) entity).hurt(damage);
                }
            }
        }

        attackCoolDown = attackPeriod;
    }

    private void onDestroy() {
        Scene.getInstance().getListOfCircleAttacks().remove(this);
        Scene.getInstance().getListOfLightSources().remove(lightSource);
    }

    public void render() {
        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            OpenGLManager.glBegin(GL_LINES);
            Color color;
            if (enemyAttack) {
                color = new Color(255, 0, 0);
            } else {
                color = musicalMode.getColor();
            }
            glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);

            /** CIRCLE OUTLINE **/
            double angle = 0;
            Coordinates centerCameraCoordinates = center.toCameraCoordinates();
            for (int i = 0; i < numberOfVertices; i++) {
                double radius = this.radius * Camera.getZoom();
                double x1 = (Math.cos(angle) * radius) + centerCameraCoordinates.x;
                double y1 = (Math.sin(angle) * radius) + centerCameraCoordinates.y;
                double x2 = (Math.cos(angle + angleStep) * radius) + centerCameraCoordinates.x;
                double y2 = (Math.sin(angle + angleStep) * radius) + centerCameraCoordinates.y;
                glVertex2d(x1, y1);
                glVertex2d(x2, y2);
                angle += angleStep;
            }

            glEnd();
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
        }
    }

    public boolean isDead() {
        return timeLiving > timeToLive;
    }
}


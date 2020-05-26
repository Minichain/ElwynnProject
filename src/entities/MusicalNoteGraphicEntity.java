package entities;

import audio.OpenALManager;
import main.Coordinates;
import main.Texture;
import particles.Particle;
import particles.ParticleManager;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

public class MusicalNoteGraphicEntity extends DynamicGraphicEntity {
    public static byte ENTITY_CODE = 61;
    private double timeLiving = 0;
    private boolean dead = false;
    private double timeToLive = 1000;
    private LightSource lightSource;
    private AttackMode attackMode;
    private float damage;
    private float lightIntensity = 50f;

    public MusicalNoteGraphicEntity(Coordinates worldCoordinates, double[] movementVector, double speed, AttackMode attackMode, float damage) {
        super((int) worldCoordinates.x, (int) worldCoordinates.y);
        init(movementVector, speed, attackMode, damage);
    }

    public void init(double[] movementVector, double speed, AttackMode attackMode, float damage) {
        int random = (int) (Math.random() * 4);
        switch (random) {
            case 0:
                setSprite(SpriteManager.getInstance().G_CLEF);
                break;
            case 1:
                setSprite(SpriteManager.getInstance().QUARTER_NOTE);
                break;
            case 2:
                setSprite(SpriteManager.getInstance().EIGHTH_NOTE);
                break;
            case 3:
            default:
                setSprite(SpriteManager.getInstance().DOUBLE_EIGHTH_NOTE);
                break;
        }
        this.movementVector = movementVector;
        this.speed = speed;
        this.timeLiving = 0;
        this.attackMode = attackMode;
        this.lightSource = new LightSource(getCenterOfMassWorldCoordinates(), lightIntensity, this.attackMode.getColor());
        this.damage = damage;
        Scene.getInstance().getListOfLightSources().add(this.lightSource);
    }

    public void update(long timeElapsed) {
        Coordinates newWorldCoordinates = new Coordinates(getWorldCoordinates().x + movementVector[0] * speed * timeElapsed, getWorldCoordinates().y + movementVector[1] * speed * timeElapsed);
        setWorldCoordinates(newWorldCoordinates);
        this.lightSource.setWorldCoordinates(getCenterOfMassWorldCoordinates());
        this.lightSource.setIntensity(lightIntensity - (float) (timeLiving / timeToLive) * lightIntensity);
        this.lightSource.update(timeElapsed);
        this.timeLiving += timeElapsed;
        if (timeLiving >= timeToLive) {
            this.dead = true;
        }
        Enemy enemy;
        for (GraphicEntity graphicEntity : Scene.getInstance().getListOfEntitiesToUpdate()) {
            if (graphicEntity instanceof Enemy) {
                enemy = (Enemy) graphicEntity;
                if (enemy.getStatus() == Enemy.Status.DEAD) {
                    continue;
                }
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), enemy.getCenterOfMassWorldCoordinates()) < 30) {
                    damage *= enemy.getWeakness(attackMode);
                    enemy.setHealth(enemy.getHealth() - damage);
                    OpenALManager.playSound(OpenALManager.SOUND_PLAYER_ATTACK_01);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(enemy.getCenterOfMassWorldCoordinates().x, enemy.getCenterOfMassWorldCoordinates().y, text, true, true, false);
                    Particle particle;
//                    for (int i = 0; i < 2; i++) {
//                        double[] velocityVector = MathUtils.rotateVector(new double[]{0.1, 0}, Math.random() * 2.0 * Math.PI);
//                        particle = new Particle(getCenterOfMassWorldCoordinates(), velocityVector, 1,
//                                this.attackMode.getColor()[0], this.attackMode.getColor()[1], this.attackMode.getColor()[2], 300, true, 0.01f);
//                        ParticleManager.getInstance().addParticle(particle);
//                    }

                    particle = new Particle(getCenterOfMassWorldCoordinates(), new double[]{1, 0.1}, 1,
                            this.attackMode.getColor()[0], this.attackMode.getColor()[1], this.attackMode.getColor()[2], 500, true, 0.01f);
                    ParticleManager.getInstance().addParticle(particle);

                    particle = new Particle(getCenterOfMassWorldCoordinates(), new double[]{-1, 0.7}, 1,
                            this.attackMode.getColor()[0], this.attackMode.getColor()[1], this.attackMode.getColor()[2], 500, true, 0.01f);
                    ParticleManager.getInstance().addParticle(particle);


                    this.dead = true;
                }
            }
        }
    }

    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    public boolean isDead() {
        return dead;
    }

    public LightSource getLightSource() {
        return lightSource;
    }
}

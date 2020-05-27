package entities;

import audio.OpenALManager;
import main.Coordinates;
import main.GameStatus;
import main.Texture;
import particles.ParticleManager;
import scene.Camera;
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
    private float lightIntensity = 30f;
    private float intensityFactor;

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

        random = (int) (Math.random() * 7);
        switch (random) {
            case 0:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_A_01);
                break;
            case 1:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_B_01);
                break;
            case 2:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_C_SHARP_01);
                break;
            case 3:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_D_01);
                break;
            case 4:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_E_01);
                break;
            case 5:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_F_SHARP_01);
                break;
            case 6:
            default:
                OpenALManager.playSound(OpenALManager.SOUND_NOTE_G_SHARP_01);
                break;
        }
    }

    public void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }
        Coordinates newWorldCoordinates = new Coordinates(getWorldCoordinates().x + movementVector[0] * speed * timeElapsed, getWorldCoordinates().y + movementVector[1] * speed * timeElapsed);
        setWorldCoordinates(newWorldCoordinates);
        this.intensityFactor = (float) MathUtils.cubicFunction(timeLiving / timeToLive);
        this.lightSource.setWorldCoordinates(getCenterOfMassWorldCoordinates());
        this.lightSource.setIntensity(lightIntensity - this.intensityFactor * lightIntensity);
        this.lightSource.update(timeElapsed);
        this.timeLiving += timeElapsed;
        if (timeLiving >= timeToLive) {
            ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, attackMode.getColor());
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
                    enemy.hurt(damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(enemy.getWorldCoordinates().x, enemy.getWorldCoordinates().y, text, true, true, false);
                    this.dead = true;
                    ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, attackMode.getColor());
                }
            }
        }
    }

    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                1f - this.intensityFactor, Camera.getZoom() * (1f - this.intensityFactor));
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

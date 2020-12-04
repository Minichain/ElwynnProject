package entities;

import main.Coordinates;
import main.GameStatus;
import main.Texture;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

import java.awt.*;

public class MusicalNoteGraphicEntity extends DynamicGraphicEntity {
    public static byte ENTITY_CODE = 8;
    private double timeLiving = 0;
    private boolean dead = false;
    private double timeToLive;
    private MusicalMode musicalMode;
    private Color color;
    private float damage;
    private float lightIntensity = 30f;
    private float intensityFactor;
    private boolean enemyAttack;

    public MusicalNoteGraphicEntity(Coordinates worldCoordinates, double[] movementVector, double speed, MusicalMode musicalMode, float damage, double timeToLive, boolean enemyAttack) {
        super((int) worldCoordinates.x, (int) worldCoordinates.y);
        this.movementVector = movementVector;
        this.movementVectorNormalized = MathUtils.normalizeVector(movementVector);
        init(speed, musicalMode, damage, timeToLive, enemyAttack);
    }

    public void init(double speed, MusicalMode musicalMode, float damage, double timeToLive, boolean enemyAttack) {
        Sprite randomSprite = getMusicalRandomSprite();
        setSprite(randomSprite);
        getWorldCoordinates().translate(movementVectorNormalized[0] * 10, movementVectorNormalized[1] * 10);
        this.speed = speed;
        this.timeLiving = 0;
        this.timeToLive = timeToLive;
        this.musicalMode = musicalMode;
        if (enemyAttack) {
            this.color = new Color(255, 0, 0);
        } else {
            this.color = this.musicalMode.getColor();
        }
        this.damage = damage;
        this.musicalMode.getRandomNote(MusicalNote.A).play();
        this.enemyAttack = enemyAttack;
        LightSource lightSource = new LightSource(getCenterOfMassWorldCoordinates(), lightIntensity, color);
        getLightSources().add(lightSource);
        Scene.getInstance().getListOfLightSources().add(lightSource);
        Scene.getInstance().getListOfEntities().add(this);
    }

    private Sprite getMusicalRandomSprite() {
        int random = (int) (Math.random() * 4);
        switch (random) {
            case 0:
                return SpriteManager.getInstance().G_CLEF;
            case 1:
                return SpriteManager.getInstance().QUARTER_NOTE;
            case 2:
                return SpriteManager.getInstance().EIGHTH_NOTE;
            case 3:
            default:
                return SpriteManager.getInstance().DOUBLE_EIGHTH_NOTE;
        }
    }

    public void update(long timeElapsed) {
        updateCoordinates();

        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }

        if (isDead()) {
            for (LightSource lightSource : getLightSources()) {
                Scene.getInstance().getListOfLightSources().remove(lightSource);
            }
            Scene.getInstance().getListOfEntities().remove(this);
            return;
        }

        getWorldCoordinates().translate(movementVectorNormalized[0] * speed * timeElapsed, movementVectorNormalized[1] * speed * timeElapsed);
        this.intensityFactor = (float) MathUtils.cubicFunction(timeLiving / timeToLive);

        updateLightSources(timeElapsed);

        this.timeLiving += timeElapsed;

        if (timeLiving >= timeToLive) {
            explode();
        }

        Enemy entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            GraphicEntity graphicEntity = Scene.getInstance().getListOfEntities().get(i);
            if (graphicEntity instanceof Player && enemyAttack) {
                if (Player.getInstance().getStatus() == Player.Status.DEAD || Player.getInstance().getStatus() == Player.Status.ROLLING) {
                    continue;
                }
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), graphicEntity.getCenterOfMassWorldCoordinates()) < 15) {
                    Player.getInstance().hurt(damage);
                    explode();
                }
            } else if (graphicEntity instanceof Enemy && !enemyAttack) {
                entity = (Enemy) graphicEntity;
                if (entity.getStatus() == Enemy.Status.DEAD || entity.getStatus() == Enemy.Status.ROLLING) {
                    continue;
                }
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), graphicEntity.getCenterOfMassWorldCoordinates()) < 15) {
                    damage *= entity.getWeakness(musicalMode);
                    entity.hurt(damage);
                    explode();
                }
            } else if (graphicEntity instanceof StaticGraphicEntity) {
                if (((StaticGraphicEntity) graphicEntity).getCollision().isColliding(getCenterOfMassWorldCoordinates())) {
                    explode();
                }
            }
        }
    }

    private void updateLightSources(Long timeElapsed) {
        for (LightSource lightSource : getLightSources()) {
            lightSource.setWorldCoordinates(getWorldCoordinates());
            lightSource.setIntensity(lightIntensity - this.intensityFactor * lightIntensity);
            lightSource.update(timeElapsed);
        }
    }

    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
                1f - this.intensityFactor, Camera.getZoom() * (1f - this.intensityFactor), true);
    }

    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    public boolean isDead() {
        return dead;
    }

    public void explode() {
        this.dead = true;
        ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, color);
    }
}

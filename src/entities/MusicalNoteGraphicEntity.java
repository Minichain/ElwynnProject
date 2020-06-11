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
    public static byte ENTITY_CODE = 61;
    private double timeLiving = 0;
    private boolean dead = false;
    private double timeToLive;
    private LightSource lightSource;
    private MusicalMode musicalMode;
    private Color color;
    private float damage;
    private float lightIntensity = 30f;
    private float intensityFactor;
    private boolean enemyAttack;

    public MusicalNoteGraphicEntity(Coordinates worldCoordinates, double[] movementVector, double speed, MusicalMode musicalMode, float damage, double timeToLive, boolean enemyAttack) {
        super((int) worldCoordinates.x, (int) worldCoordinates.y);
        movementVector = MathUtils.normalizeVector(movementVector);
        init(movementVector, speed, musicalMode, damage, timeToLive, enemyAttack);
    }

    public void init(double[] movementVector, double speed, MusicalMode musicalMode, float damage, double timeToLive, boolean enemyAttack) {
        Sprite randomSprite = getMusicalRandomSprite();
        setSprite(randomSprite);
        this.movementVector = movementVector;
        getWorldCoordinates().translate(movementVector[0] * 10, movementVector[1] * 10);
        this.speed = speed;
        this.timeLiving = 0;
        this.timeToLive = timeToLive;
        this.musicalMode = musicalMode;
        if (enemyAttack) {
            this.color = new Color(255, 0, 0);
        } else {
            this.color = this.musicalMode.getColor();
        }
        this.lightSource = new LightSource(getCenterOfMassWorldCoordinates(), lightIntensity, color);
        this.damage = damage;
        Scene.getInstance().getListOfLightSources().add(this.lightSource);
        this.musicalMode.getRandomNote(MusicalNote.A).play();
        this.enemyAttack = enemyAttack;
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
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }
        getWorldCoordinates().translate(movementVector[0] * speed * timeElapsed, movementVector[1] * speed * timeElapsed);
        this.intensityFactor = (float) MathUtils.cubicFunction(timeLiving / timeToLive);
        this.lightSource.setWorldCoordinates(getCenterOfMassWorldCoordinates());
        this.lightSource.setIntensity(lightIntensity - this.intensityFactor * lightIntensity);
        this.lightSource.update(timeElapsed);
        this.timeLiving += timeElapsed;

        if (timeLiving >= timeToLive) {
            explode();
        }

        Enemy entity;
        for (GraphicEntity graphicEntity : Scene.getInstance().getListOfEntitiesToUpdate()) {
            if (graphicEntity instanceof Player && enemyAttack) {
                if (Player.getInstance().getStatus() == Player.Status.DEAD) {
                    continue;
                }
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), graphicEntity.getCenterOfMassWorldCoordinates()) < 15) {
                    Player.getInstance().hurt(damage);
                    explode();
                }
            } else if (graphicEntity instanceof Enemy && !enemyAttack) {
                entity = (Enemy) graphicEntity;
                if (entity.getStatus() == Enemy.Status.DEAD) {
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

    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x - getSprite().SPRITE_WIDTH / 2, y - getSprite().SPRITE_HEIGHT / 2, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(),
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

    public void explode() {
        this.dead = true;
        ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, color);
    }
}

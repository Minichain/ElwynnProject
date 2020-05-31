package entities;

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
    private MusicalMode musicalMode;
    private float damage;
    private float lightIntensity = 30f;
    private float intensityFactor;

    public MusicalNoteGraphicEntity(Coordinates worldCoordinates, double[] movementVector, double speed, MusicalMode musicalMode, float damage) {
        super((int) worldCoordinates.x, (int) worldCoordinates.y);
        init(movementVector, speed, musicalMode, damage);
    }

    public void init(double[] movementVector, double speed, MusicalMode musicalMode, float damage) {
        Sprite randomSprite = getMusicalRandomSprite();
        setSprite(randomSprite);
        this.movementVector = movementVector;
//        getWorldCoordinates().translate(movementVector[0] * 25, movementVector[1] * 25);
        this.speed = speed;
        this.timeLiving = 0;
        this.musicalMode = musicalMode;
        this.lightSource = new LightSource(getCenterOfMassWorldCoordinates(), lightIntensity, this.musicalMode.getColor());
        this.damage = damage;
        Scene.getInstance().getListOfLightSources().add(this.lightSource);
        this.musicalMode.getRandomNote(MusicalNote.A).play();
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
        Coordinates newWorldCoordinates = new Coordinates(getWorldCoordinates().x + movementVector[0] * speed * timeElapsed, getWorldCoordinates().y + movementVector[1] * speed * timeElapsed);
        setWorldCoordinates(newWorldCoordinates);
        this.intensityFactor = (float) MathUtils.cubicFunction(timeLiving / timeToLive);
        this.lightSource.setWorldCoordinates(getCenterOfMassWorldCoordinates());
        this.lightSource.setIntensity(lightIntensity - this.intensityFactor * lightIntensity);
        this.lightSource.update(timeElapsed);
        this.timeLiving += timeElapsed;

        if (timeLiving >= timeToLive) {
            ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, musicalMode.getColor());
            this.dead = true;
        }

        Enemy entity;
        for (GraphicEntity graphicEntity : Scene.getInstance().getListOfEntitiesToUpdate()) {
            if (graphicEntity instanceof Player) {
                continue;
            } else if (graphicEntity instanceof Enemy) {
                if (MathUtils.module(getCenterOfMassWorldCoordinates(), graphicEntity.getCenterOfMassWorldCoordinates()) < 30) {
                    this.dead = true;
                    ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, musicalMode.getColor());

                    entity = (Enemy) graphicEntity;
                    if (entity.getStatus() == Enemy.Status.DEAD) {
                        continue;
                    }
                    damage *= entity.getWeakness(musicalMode);
                    entity.hurt(damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getWorldCoordinates().x, entity.getWorldCoordinates().y, text, true, true, false);
                }
            } else if (graphicEntity instanceof StaticGraphicEntity) {
                if (((StaticGraphicEntity) graphicEntity).getCollision().isColliding(getCenterOfMassWorldCoordinates())) {
                    this.dead = true;
                    ParticleManager.particlesExplosion(getCenterOfMassWorldCoordinates(), 10, musicalMode.getColor());
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

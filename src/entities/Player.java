package entities;

import audio.OpenALManager;
import board.FretBoard;
import inventory.Inventory;
import items.*;
import listeners.ActionManager;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import scene.TileMap;
import text.FloatingTextEntity;
import utils.ArrayUtils;
import utils.MathUtils;
import utils.Utils;
import listeners.InputListenerManager;
import main.*;

import java.awt.*;
import java.util.ArrayList;

public class Player extends LivingDynamicGraphicEntity {
    public static String ENTITY_CODE = "player";
    private static Player instance = null;
    private static Utils.DirectionFacing directionFacing;

    public static float MAX_HEALTH = 5000f;
    public static float HEALTH_REGENERATION = 0.02f;
    public static float MAX_MANA = 100f;
    public static float MANA_REGENERATION = 0.001f;
    private float mana = 100f;
    public static float MAX_STAMINA = 100f;
    public static float STAMINA_REGENERATION = 0.018f;
    public static float STAMINA_REGENERATION_WHEN_ATTACKING = 0.009f;
    private float stamina = 100f;

    private float hurtPeriod = 100f;
    private float hurtCoolDown = 0f;

    private MusicalMode musicalMode;
    private boolean choosingMusicalMode;
    private float choosingMusicalModeManaCost = 0.01f;
    private int changeMusicalModePeriod = 500;
    private int changeMusicalModeCoolDown;
    private float changeMusicalModeManaCost = 2.5f;

    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD, PLAYING_MUSIC
    }

    private static Status status;

    public enum StatusEffect {
        NONE, HASTE
    }

    private static boolean hasteEffect;
    private static int hasteEffectDuration;

    private boolean footstep = true;

    private Inventory inventory;

    private NonPlayerCharacter interactiveNPC = null;

    private double warpTakenPeriod = 1000;  //1 second until you can take a warp again
    private double warpTakenCoolDown;

    /** RUNNING PARTICLES **/
    private int runningParticlePeriod = 200;
    private int runningParticleCoolDown;

    private Player() {
        super(0, 0);
        init(new Coordinates(0, 0));
    }

    public void init(Coordinates coordinates) {
        Log.l("init player!");
        setWorldCoordinates(coordinates);
        inventory = new Inventory();
        health = 5000f;
        mana = 100f;
        speed = 0.08;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        musicalMode = MusicalMode.IONIAN;
        runningParticleCoolDown = 0;
        changeMusicalModeCoolDown = 0;
        setSprite(SpriteManager.getInstance().PLAYER);
        getInventory().storeItem(new GoldCoin(), 10);
    }

    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
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
    }

    @Override
    public void hurt(float damage) {
        if (hurtCoolDown >= 0f) {
            return;
        }
        hurtCoolDown = hurtPeriod;

        OpenALManager.playSound(OpenALManager.SOUND_PLAYER_HURT_01);
        float previousHealth = getHealth();
        setHealth(previousHealth - damage);
        if (getHealth() <= 0 && previousHealth > 0) onDying();
        String text = String.valueOf((int) damage);
        float scale;
        Color color;
        if (damage < 200f) {
            scale = 2f;
            color = new Color(1f, 0.1f, 0.1f);
        } else {
            scale = 4f;
            color = new Color(1f, 0.75f, 0.5f);
        }
        new FloatingTextEntity(this.getWorldCoordinates().x, this.getWorldCoordinates().y, text, color, 1.25, new double[]{0, -1}, scale);
        Camera.getInstance().shake(100, 1f);
    }

    @Override
    public void update(long timeElapsed) {
//        Log.l("Updating player. TimeElapsed: " + timeElapsed);
        if (health > 0)  {  //Player is alive
            setDead(false);
            /** UPDATE MANA, HEALTH AND STAMINA **/
            if (mana < MAX_MANA) {
                mana += (MANA_REGENERATION * timeElapsed);
            } else if (mana > MAX_MANA) {
                mana = MAX_MANA;
            }
            if (health < MAX_HEALTH) {
                health += (HEALTH_REGENERATION * timeElapsed);
            } else if (health > MAX_HEALTH) {
                health = MAX_HEALTH;
            }
            if (stamina < MAX_STAMINA) {
                if (status == Status.PLAYING_MUSIC) {
                    stamina += (STAMINA_REGENERATION_WHEN_ATTACKING * timeElapsed);
                } else {
                    stamina += (STAMINA_REGENERATION * timeElapsed);
                }
            } else if (stamina > MAX_STAMINA) {
                stamina = MAX_STAMINA;
            }

            if (choosingMusicalMode) {
                if (mana > 0) {
                    mana -= choosingMusicalModeManaCost * timeElapsed;
                } else {
                    setChoosingMusicalMode(false);
                }
            }

            /** UPDATE ATTACKS **/
            if (!choosingMusicalMode) {
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL
                        && (InputListenerManager.leftMouseButtonPressed || InputListenerManager.getRightTriggerValue() > 0.1f)) {
                    status = Status.PLAYING_MUSIC;
                }
                updateAttack(timeElapsed);
            }

            /** UPDATE MOVEMENT VECTOR **/
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                if (status != Status.ROLLING) {
                    computeMovementVector(timeElapsed);
                }
                movementVector[0] = movementVectorNormalized[0] * timeElapsed;
                movementVector[1] = movementVectorNormalized[1] * timeElapsed;
            }

            /** CHECK COLLISIONS **/
            boolean horizontalCollision = checkHorizontalCollision(movementVectorNormalized, 6);
            boolean verticalCollision = checkVerticalCollision(movementVectorNormalized, 6);

            /** MOVE ENTITY **/
            double speed = 0.0;
            if (status == Status.RUNNING) {
                speed = this.speed;
            } else if (status == Status.ROLLING) {
                speed = this.speed * 1.75;
            }

            if (speed > 0.0 && runningParticleCoolDown <= 0) {
                Coordinates coordinates = new Coordinates(getCenterOfMassWorldCoordinates().x + MathUtils.random(-2.5, 2.5),
                        getCenterOfMassWorldCoordinates().y + MathUtils.random(-2.5, 2.5) + 5);
                new Smoke((int) coordinates.x, (int) coordinates.y, 300);
                runningParticleCoolDown = runningParticlePeriod;
            }
            runningParticleCoolDown -= timeElapsed;

            if (!horizontalCollision) {
                getWorldCoordinates().x += movementVector[0] * speed;
            }
            if (!verticalCollision) {
                getWorldCoordinates().y += movementVector[1] * speed;
            }

            /** WHERE IS IT FACING? **/
            if (status == Status.PLAYING_MUSIC) {
                facingVector = new double[]{InputListenerManager.getMouseCameraCoordinates().x - getCenterOfMassCameraCoordinates().x,
                        InputListenerManager.getMouseCameraCoordinates().y - getCenterOfMassCameraCoordinates().y};
                facingVector = MathUtils.normalizeVector(facingVector);
                directionFacing = Utils.checkDirectionFacing(facingVector);
                if (mana >= 0.05f) {
                    mana -= 0.05f;
                }
            } else if (movementVector[0] != 0 || movementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(movementVector);
            }

            checkAnyInteractiveNPC();

            if (hasteEffect) {
                hasteEffectDuration -= timeElapsed;
                hasteEffect = hasteEffectDuration > 0;
            }

            if (changeMusicalModeCoolDown >= 0) changeMusicalModeCoolDown -= timeElapsed;
            if (warpTakenCoolDown >= 0) warpTakenCoolDown -= timeElapsed;
            if (hurtCoolDown >= 0) hurtCoolDown -= timeElapsed;
        } else if (status != Status.DEAD) {   //Player is dying
            status = Status.DYING;
        } else {    //Player is dead
            setDead(true);
            setPlayingMusic(false);
        }

        double frame;
        switch (status) {
            case IDLE:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().IDLE_FRAMES);
                break;
            case RUNNING:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                if (footstep && (int) frame % 4 == 0) {
                    footstep = false;
                    if (Math.random() < 0.5) {
                        OpenALManager.playSound(OpenALManager.SOUND_FOOTSTEP_01);
                    } else {
                        OpenALManager.playSound(OpenALManager.SOUND_FOOTSTEP_02);
                    }
                } else if ((int) frame % 4 != 0) {
                    footstep = true;
                }
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
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.0075));
                if (frame > getSprite().DYING_FRAMES) {
                    status = Status.DEAD;
                    setDead(true);
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DYING_FRAMES);
                }
                break;
            case DEAD:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.015));
                setSpriteCoordinateFromSpriteSheetX(frame % getSprite().DEAD_FRAMES);
                break;
            case PLAYING_MUSIC:
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.0115)) % getSprite().ATTACKING_FRAMES;
                if (frame > getSprite().ATTACKING_FRAMES) {
                    status = Status.IDLE;
                    setSpriteCoordinateFromSpriteSheetX(0);
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame);
                }
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    private boolean checkHorizontalCollision(double[] movement, double distanceFactor) {
        Coordinates collisionCoordinates = new Coordinates(getCenterOfMassWorldCoordinates().x + movement[0] * distanceFactor, getCenterOfMassWorldCoordinates().y);
        boolean tileCollision = TileMap.checkCollisionWithTile((int) collisionCoordinates.x, (int) collisionCoordinates.y);

        return Scene.getInstance().checkCollisionWithEntities(collisionCoordinates) || tileCollision;
    }

    private boolean checkVerticalCollision(double[] movement, double distanceFactor) {
        Coordinates collisionCoordinates = new Coordinates(getCenterOfMassWorldCoordinates().x, getCenterOfMassWorldCoordinates().y + movement[1] * distanceFactor);
        boolean tileCollision = TileMap.checkCollisionWithTile((int) collisionCoordinates.x, (int) collisionCoordinates.y);

        return Scene.getInstance().checkCollisionWithEntities(collisionCoordinates) || tileCollision;
    }

    public void computeMovementVector(long timeElapsed) {
        movementVector = new double[]{0, 0};
        movementVectorNormalized = new double[]{0, 0};
        boolean playerMoving = false;
        if (ActionManager.MOVING_DOWN) {
            movementVector[1] += 1;
        }
        if (ActionManager.MOVING_LEFT) {
            movementVector[0] += -1;
        }
        if (ActionManager.MOVING_UP) {
            movementVector[1] += -1;
        }
        if (ActionManager.MOVING_RIGHT) {
            movementVector[0] += 1;
        }

        movementVector[0] += InputListenerManager.getLeftJoystickAxes()[0];
        movementVector[1] += InputListenerManager.getLeftJoystickAxes()[1];

        if (Math.abs(movementVector[0]) > 0 || Math.abs(movementVector[1]) > 0) {
            playerMoving = true;
        }

        if (status != Status.PLAYING_MUSIC) {
            if (playerMoving) {
                status = Status.RUNNING;
            } else {
                status = Status.IDLE;
            }
        }

        movementVectorNormalized = MathUtils.normalizeVector(movementVector);
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
            case PLAYING_MUSIC:
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

    public void setPlayingMusic(boolean playingMusic) {
        if (!isDead()) status = playingMusic ? Status.PLAYING_MUSIC : Status.IDLE;
        FretBoard.getInstance().setPlayingMusic(playingMusic && !isDead());
    }

    public boolean isPlayingMusic() {
        return status == Status.PLAYING_MUSIC;
    }

    public void playNote() {
        if (!isDead() && isPlayingMusic()) {
            FretBoard.getInstance().playNote();
        }
    }

    private double[] pointingVector = new double[]{1.0, 1.0};

    private void updateAttack(long timeElapsed) {

    }

    public void drawAttackFX() {
    }

    public float getMana() {
        return mana;
    }

    public float getStamina() {
        return stamina;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public void roll() {
        if (stamina >= 25f && status == Status.RUNNING) {
            Log.l("Player rolling");
            status = Status.ROLLING;
            setSpriteCoordinateFromSpriteSheetX(0);
            OpenALManager.playSound(OpenALManager.SOUND_ROLLING_01);
            stamina -= 25f;
        }
    }

    public MusicalMode getMusicalMode() {
        return musicalMode;
    }

    public void setMusicalMode(int musicalMode) {
        if (musicalMode < 0) {
            musicalMode = MusicalMode.values().length + musicalMode;
        } else {
            musicalMode %= MusicalMode.values().length;
        }

        setMusicalMode(MusicalMode.values()[musicalMode]);
    }

    public void setMusicalMode(MusicalMode musicalMode) {
        if (musicalMode == this.musicalMode || mana < changeMusicalModeManaCost) return;

        Log.l("Set musical mode to " + musicalMode);
//        OpenALManager.stopMusicDependingOnMusicalMode(this.musicalMode);

        if (changeMusicalModeCoolDown <= 0) {
            switch (musicalMode) {
                case IONIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_IONIAN);
                    break;
                case DORIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_DORIAN);
                    break;
                case PHRYGIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_PHRYGIAN);
                    break;
                case LYDIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_LYDIAN);
                    break;
                case MIXOLYDIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_MIXOLYDIAN);
                    break;
                case AEOLIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_AEOLIAN);
                    break;
                case LOCRIAN:
                    OpenALManager.playSound(OpenALManager.SOUND_CHANGE_MUSICAL_MODE_LOCRIAN);
                    break;
                default:
                    break;
            }

            //Generate particles with the colour of the musical mode
            Coordinates particleCoordinates;
            double randomAngle;
            double[] generationVector;
            Particle particle;
            double[] velocityVector;
            int numberOfParticles = 4;
            for (int i = 0; i < numberOfParticles; i++) {
                randomAngle = MathUtils.random(0, 2.0 * Math.PI);
                generationVector = new double[]{15 * Math.random(), 0};
                generationVector = MathUtils.rotateVector(generationVector, randomAngle);
                velocityVector = new double[]{0, -0.1};
                particleCoordinates = new Coordinates(getCenterOfMassWorldCoordinates().x + generationVector[0], getCenterOfMassWorldCoordinates().y + generationVector[1]);
                particle = new Particle(particleCoordinates, velocityVector, 0.25, 1.5f, musicalMode.getColor(), true);
                ParticleManager.getInstance().addParticle(particle);
            }

            changeMusicalModeCoolDown = changeMusicalModePeriod;
            mana -= changeMusicalModeManaCost;
            this.musicalMode = musicalMode;
        }
    }

    private void checkAnyInteractiveNPC() {
        double smallestDistance = 25;
        interactiveNPC = null;

        //Check the closest NPC to interact with
        for (NonPlayerCharacter nonPlayerCharacter : Scene.getInstance().getListOfNonPlayerCharacters()) {
            nonPlayerCharacter.setInteractionEntity(null);
            double distance = MathUtils.module(getWorldCoordinates(), nonPlayerCharacter.getWorldCoordinates());

            if (distance < nonPlayerCharacter.getInteractionDistance() && distance < smallestDistance) {
//                Log.l("NPC close enough to interact");
                smallestDistance = distance;
                interactiveNPC = nonPlayerCharacter;
            }
        }

        //Stop interaction with any NPC if we are not interacting with them anymore
        for (NonPlayerCharacter nonPlayerCharacter : Scene.getInstance().getListOfNonPlayerCharacters()) {
            if ((interactiveNPC == null || !interactiveNPC.equals(nonPlayerCharacter)) && nonPlayerCharacter.isInteracting()) {
                nonPlayerCharacter.onStopInteraction();
            }
        }

        //Set an interaction entity if there is an NPC close enough to interact with
        if (interactiveNPC != null) {
            //NPC close! Interaction available.
            double distance = MathUtils.module(getWorldCoordinates(), interactiveNPC.getWorldCoordinates());
            if (distance < interactiveNPC.getInteractionDistance()) {
                interactiveNPC.setInteractionEntity(new InteractionEntity(
                        (int) interactiveNPC.getWorldCoordinates().x,
                        (int) interactiveNPC.getWorldCoordinates().y - 20));
            }
        }
    }

    public void interactWithNPC() {
        if (interactiveNPC != null && status != Status.DEAD) {   //If we are close to an NPC we can interact with...
            double distance = MathUtils.module(getWorldCoordinates(), interactiveNPC.getWorldCoordinates());
            if (distance < interactiveNPC.getInteractionDistance()) {
                interactiveNPC.onInteraction();
            }
        }
    }

    public NonPlayerCharacter getInteractiveNPC() {
        return interactiveNPC;
    }

    public Item hasHealthPotion() {
        return inventory.isItemStored(HealthPotion.class);
    }

    public Item hasManaPotion() {
        return inventory.isItemStored(ManaPotion.class);
    }

    public Item hasHastePotion() {
        return inventory.isItemStored(HastePotion.class);
    }

    public void useHealthPotion() {
        Item healthPotion = hasHealthPotion();
        if (healthPotion != null) useItem(healthPotion);
        else Log.l("No HEALTH POTIONS left!");
    }

    public void useManaPotion() {
        Item manaPotion = hasManaPotion();
        if (manaPotion != null) useItem(manaPotion);
        else Log.l("No MANA POTIONS left!");
    }

    public void useHastePotion() {
        Item hastePotion = hasHastePotion();
        if (hastePotion != null) useItem(hastePotion);
        else Log.l("No HASTE POTIONS left!");
    }

    public void useItem(Item item) {
        Log.l("Using " + item.getName());
        item.use();
        getInventory().removeItem(item.getClass());
    }

    public int getAmountOfGoldCoins() {
        return getAmountOfItemType(GoldCoin.class);
    }

    public int getAmountOfHealthPotions() {
        return getAmountOfItemType(HealthPotion.class);
    }

    public int getAmountOfManaPotions() {
        return getAmountOfItemType(ManaPotion.class);
    }

    public int getAmountOfHastePotions() {
        return getAmountOfItemType(HastePotion.class);
    }

    private int getAmountOfItemType(Class<?> type) {
        return getInventory().getAmountOfItemType(type);
    }

    public void setStatusEffect(StatusEffect statusEffect, int statusDuration) {
        switch (statusEffect) {
            case NONE:
            default:
                break;
            case HASTE:
                hasteEffect = true;
                hasteEffectDuration = statusDuration;
                break;
        }
    }

    public boolean isHasteEffect() {
        return hasteEffect;
    }

    public boolean isChoosingMusicalMode() {
        return choosingMusicalMode;
    }

    public void setChoosingMusicalMode(boolean choosingMusicalMode) {
        this.choosingMusicalMode = choosingMusicalMode && status != Status.DEAD;
        MusicalModeSelector.getInstance().setShowing(this.choosingMusicalMode);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ArrayList<Item> getListOfItems() {
        return getInventory().getListOfItems();
    }

    public ArrayList<Item> getListOfItemsExceptGoldCoins() {
        return getInventory().getListOfItemsExceptGoldCoins();
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

    public boolean canTakeWarp() {
        return warpTakenCoolDown < 0;
    }

    public void takeWarp() {
        warpTakenCoolDown = warpTakenPeriod;
    }
}

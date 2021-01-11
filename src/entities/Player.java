package entities;

import audio.OpenALManager;
import inventory.Inventory;
import items.HastePotion;
import items.HealthPotion;
import items.Item;
import items.ManaPotion;
import listeners.ActionManager;
import particles.Particle;
import particles.ParticleManager;
import scene.Scene;
import scene.TileMap;
import text.FloatingTextEntity;
import utils.MathUtils;
import utils.Utils;
import listeners.InputListenerManager;
import main.*;

import java.awt.*;
import java.util.ArrayList;

public class Player extends LivingDynamicGraphicEntity {
    public static byte ENTITY_CODE = 6;
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

    /** ATTACK **/
    private int attack01Period = 250;
    private int attack01CoolDown;
    private float attack01Power = 400f;
    private float attack01ManaCost = 0.25f;

    private CircleAttack circleAttack;
    private int circleAttackPeriod = 2500;
    private int circleAttackCoolDown;
    private float circleAttackPower = 200f;
    private float circleAttackManaCost = 10f;

    private MusicalMode musicalMode;
    private boolean choosingMusicalMode;
    private float choosingMusicalModeManaCost = 0.01f;
    private int changeMusicalModePeriod = 500;
    private int changeMusicalModeCoolDown;
    private float changeMusicalModeManaCost = 2.5f;

    public enum Status {
        IDLE, RUNNING, ROLLING, DYING, DEAD, ATTACKING
    }

    private static Status status;

    public enum StatusEffect {
        NONE, HASTE
    }

    private static boolean hasteEffect;
    private static int hasteEffectDuration;

    private boolean footstep = true;

    private Inventory inventory;
    private int amountOfGoldCoins;

    private NonPlayerCharacter interactiveNPC = null;

    /** RUNNING PARTICLES **/
    private int runningParticlePeriod = 200;
    private int runningParticleCoolDown;

    private Player() {
        super(Scene.getInitialCoordinates().x, Scene.getInitialCoordinates().y);
        init();
    }

    public void init() {
        Log.l("init player!");
        setWorldCoordinates(Scene.getInitialCoordinates());
        health = 5000f;
        mana = 100f;
        speed = 0.08;
        attack01CoolDown = 0;
        circleAttackCoolDown = 0;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        musicalMode = MusicalMode.IONIAN;
        amountOfGoldCoins = 0;
        runningParticleCoolDown = 0;
        changeMusicalModeCoolDown = 0;
        setSprite(SpriteManager.getInstance().PLAYER);
        setAmountOfGoldCoins(500);
        inventory = new Inventory();
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
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void onDying() {
        OpenALManager.playSound(OpenALManager.SOUND_PLAYER_DYING_01);
    }

    @Override
    public void hurt(float damage) {
        OpenALManager.playSound(OpenALManager.SOUND_PLAYER_HURT_01);
        float previousHealth = getHealth();
        setHealth(previousHealth - damage);
        if (getHealth() <= 0 && previousHealth > 0) onDying();
        String text = String.valueOf((int) damage);
        new FloatingTextEntity(this.getCenterOfMassWorldCoordinates().x, this.getCenterOfMassWorldCoordinates().y, text,
                new Color(1f, 0f, 0f), 1.25, new double[]{0, -1});
    }

    @Override
    public void update(long timeElapsed) {
//        Log.l("Updating player. TimeElapsed: " + timeElapsed);
        if (health > 0)  {  //Player is alive
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
                if (status == Status.ATTACKING) {
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
            if (GameMode.getGameMode() == GameMode.Mode.NORMAL
                    && (InputListenerManager.leftMouseButtonPressed || InputListenerManager.getRightTriggerValue() > 0.1f)) {
                status = Status.ATTACKING;
            }
            updateAttack(timeElapsed);

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
            if (status == Status.ATTACKING) {
                speed = this.speed * 0.5;
            } else if (status == Status.RUNNING) {
                speed = this.speed;
            } else if (status == Status.ROLLING) {
                speed = this.speed * 1.75;
            }

            if (speed > 0.0 && runningParticleCoolDown <= 0) {
                Coordinates coordinates = new Coordinates(getCenterOfMassWorldCoordinates().x + MathUtils.random(-2.5, 2.5),
                        getCenterOfMassWorldCoordinates().y + MathUtils.random(-2.5, 2.5) + 10);
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
            facingVector = null;
            if (status == Status.ATTACKING) {
                facingVector = new double[]{InputListenerManager.getMouseCameraCoordinates().x - getCameraCoordinates().x,
                        InputListenerManager.getMouseCameraCoordinates().y - getCameraCoordinates().y};
                directionFacing = Utils.checkDirectionFacing(facingVector);
            } else if (movementVector[0] != 0 || movementVector[1] != 0) {
                directionFacing = Utils.checkDirectionFacing(movementVector);
            }

            checkAnyInteractiveNPC();

            if (hasteEffect) {
                hasteEffectDuration -= timeElapsed;
                hasteEffect = hasteEffectDuration > 0;
            }

            if (changeMusicalModeCoolDown >= 0) changeMusicalModeCoolDown -= timeElapsed;

        } else if (status != Status.DEAD) {   //Player is dying
            status = Status.DYING;
        } else {    //Player is dead

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
                frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.0115));
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

        if (status != Status.ATTACKING) {
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

    private double[] pointingVector = new double[]{1.0, 1.0};

    private void updateAttack(long timeElapsed) {
        /** CONE ATTACK **/
        if (InputListenerManager.isUsingKeyboardAndMouse()) {
            pointingVector = new double[]{InputListenerManager.getMouseWorldCoordinates().x - Player.getInstance().getCenterOfMassWorldCoordinates().x,
                    InputListenerManager.getMouseWorldCoordinates().y - Player.getInstance().getCenterOfMassWorldCoordinates().y};
            pointingVector = MathUtils.normalizeVector(pointingVector);
        } else {
            if (InputListenerManager.getRightJoystickAxes()[0] != 0f || InputListenerManager.getRightJoystickAxes()[1] != 0) {
                pointingVector = new double[]{(double) InputListenerManager.getRightJoystickAxes()[0], (double) InputListenerManager.getRightJoystickAxes()[1]};
            }
        }

        if (InputListenerManager.leftMouseButtonPressed || InputListenerManager.getRightTriggerValue() > 0.1f) {
            if (mana >= attack01ManaCost && attack01CoolDown <= 0) {
                new MusicalNoteGraphicEntity(getCenterOfMassWorldCoordinates(), pointingVector,
                        0.2, musicalMode, attack01Power, 1000.0, false);
                if (hasteEffect) attack01CoolDown = (attack01Period / 2);
                else attack01CoolDown = attack01Period;
                mana -= attack01ManaCost;
            }
        }
        if (attack01CoolDown > 0) {
            attack01CoolDown -= timeElapsed;
        }

        /** CIRCLE ATTACK **/
        if (InputListenerManager.rightMouseButtonPressed) {
            if (mana >= circleAttackManaCost && circleAttackCoolDown <= 0) {
                circleAttack = new CircleAttack(new Coordinates(InputListenerManager.getMouseWorldCoordinates().x, InputListenerManager.getMouseWorldCoordinates().y),
                        50, 500, circleAttackPower, false, true, musicalMode);
                Scene.listOfCircleAttacks.add(circleAttack);
                circleAttackCoolDown = circleAttackPeriod;
                mana -= circleAttackManaCost;
            }
        }
        if (circleAttackCoolDown > 0) {
            circleAttackCoolDown -= timeElapsed;
        }
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

    public int getAmountOfGoldCoins() {
        return amountOfGoldCoins;
    }

    public void setAmountOfGoldCoins(int amountOfGoldCoins) {
        this.amountOfGoldCoins = amountOfGoldCoins;
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
                break;
            case HASTE:
                hasteEffect = true;
                hasteEffectDuration = statusDuration;
                break;
            default:
                break;
        }
    }

    public boolean isChoosingMusicalMode() {
        return choosingMusicalMode;
    }

    public void setChoosingMusicalMode(boolean choosingMusicalMode) {
        MusicalModeSelector.getInstance().setShowing(choosingMusicalMode);
        this.choosingMusicalMode = choosingMusicalMode;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ArrayList<Item> getListOfItems() {
        return getInventory().getListOfItems();
    }
}

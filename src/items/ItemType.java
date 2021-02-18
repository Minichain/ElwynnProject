package items;

import audio.OpenALManager;
import entities.Sprite;
import entities.SpriteManager;

public enum ItemType {
    GOLD_COIN (0), HEALTH_POTION (1), MANA_POTION (2), HASTE_POTION (3), WOOD (4);

    public final int value;

    ItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Sprite getSprite() {
        switch (this) {
            default:
            case GOLD_COIN:
                return SpriteManager.getInstance().GOLD_COIN;
            case HEALTH_POTION:
                return SpriteManager.getInstance().HEALTH_POTION;
            case MANA_POTION:
                return SpriteManager.getInstance().MANA_POTION;
            case HASTE_POTION:
                return SpriteManager.getInstance().HASTE_POTION;
            case WOOD:
                return SpriteManager.getInstance().WOOD;
        }
    }

    public Item getItem() {
        switch (this) {
            default:
            case GOLD_COIN:
                return new GoldCoin();
            case HEALTH_POTION:
                return new HealthPotion();
            case MANA_POTION:
                return new ManaPotion();
            case HASTE_POTION:
                return new HastePotion();
            case WOOD:
                return new Wood();
        }
    }

    public void playPickUpSound() {
        switch (this) {
            default:
            case GOLD_COIN:
                OpenALManager.playSound(OpenALManager.SOUND_GOLD_COIN_PICKED_UP_01);
                break;
            case HEALTH_POTION:
            case MANA_POTION:
            case HASTE_POTION:
            case WOOD:
                break;
        }
    }
}

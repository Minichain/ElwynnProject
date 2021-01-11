package items;

import entities.Sprite;
import entities.SpriteManager;

public enum ItemType {
    GOLD_COIN, HEALTH_POTION, MANA_POTION, HASTE_POTION;

    public Sprite getSprite() {
        switch (this) {
            default:
            case GOLD_COIN:
                return SpriteManager.getInstance().GOLD_COIN_INTERFACE;
            case HEALTH_POTION:
                return SpriteManager.getInstance().HEALTH_POTION;
            case MANA_POTION:
                return SpriteManager.getInstance().MANA_POTION;
            case HASTE_POTION:
                return SpriteManager.getInstance().HASTE_POTION;
        }
    }
}

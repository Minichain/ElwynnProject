package items;

import audio.OpenALManager;
import entities.Player;
import entities.Sprite;
import main.Strings;

public class HastePotion extends Item {
    private int cost;

    public HastePotion() {
        super();
        cost = 5;
        MAX_AMOUNT_PER_STACK = 5;
    }

    @Override
    public void use() {
        Player.getInstance().setStatusEffect(Player.StatusEffect.HASTE, 10000);
        OpenALManager.playSound(OpenALManager.SOUND_DRINK_01);
    }

    @Override
    public String getName() {
        return Strings.getString("item_haste_potion");
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public Sprite getSprite() {
        return ItemType.HASTE_POTION.getSprite();
    }
}

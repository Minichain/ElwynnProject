package items;

import audio.OpenALManager;
import entities.Player;

public class HastePotion implements Item {
    private String name = "Haste Potion";
    private int cost;

    public HastePotion() {
        super();
        cost = 25;
    }

    @Override
    public void use() {
        Player.getInstance().setStatusEffect(Player.StatusEffect.HASTE, 10000);
        OpenALManager.playSound(OpenALManager.SOUND_DRINK_01);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }
}

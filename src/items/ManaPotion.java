package items;

import audio.OpenALManager;
import entities.Player;
import main.Strings;

public class ManaPotion implements Item {
    private int cost;
    private float restoreValue;

    public ManaPotion() {
        super();
        cost = 15;
        restoreValue = 10f;
    }

    @Override
    public void use() {
        Player.getInstance().setMana(Player.getInstance().getMana() + restoreValue);
        OpenALManager.playSound(OpenALManager.SOUND_DRINK_01);
    }

    @Override
    public String getName() {
        return Strings.getString("item_mana_potion");
    }

    @Override
    public int getCost() {
        return cost;
    }
}

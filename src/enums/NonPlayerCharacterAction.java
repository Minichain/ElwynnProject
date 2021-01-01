package enums;

import main.Strings;

public enum NonPlayerCharacterAction {
    TALK, SELL, BUY, QUIT;

    public String toString() {
        switch(this) {
            case TALK:
                return Strings.getString("ui_talk_npc");
            case SELL:
                return Strings.getString("ui_buy_npc");
            case BUY:
                return Strings.getString("ui_sell_npc");
            case QUIT:
            default:
                return Strings.getString("ui_quit_npc");
        }
    }
}

package enums;

public enum NonPlayerCharacterAction {
    TALK, SELL, BUY, QUIT;

    public String toString() {
        switch(this) {
            case TALK:
                return "Talk";
            case SELL:
                return "Buy";
            case BUY:
                return "Sell";
            case QUIT:
            default:
                return "Quit";
        }
    }
}

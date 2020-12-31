package enums;

public enum Language {
    ENGLISH (0), SPANISH(1), CATALAN(2);

    public int value;

    Language(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        switch (this) {
            case ENGLISH:
            default:
                return "English";
            case SPANISH:
                return "Spanish";
            case CATALAN:
                return "Catalan";
        }
    }
}

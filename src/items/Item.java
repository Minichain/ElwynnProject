package items;

public abstract class Item {
    private boolean stored;

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public abstract void use();

    public abstract String getName();

    public abstract int getCost();
}

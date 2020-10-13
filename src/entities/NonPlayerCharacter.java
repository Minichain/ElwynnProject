package entities;

import java.util.ArrayList;

public abstract class NonPlayerCharacter extends LivingDynamicGraphicEntity {
    private InteractionEntity interactionEntity = null;

    public enum Interaction {
        TALKING, SELLING
    }

    public NonPlayerCharacter(int x, int y) {
        super(x, y);
    }

    public InteractionEntity getInteractionEntity() {
        return interactionEntity;
    }

    public void setInteractionEntity(InteractionEntity interactionEntity) {
        this.interactionEntity = interactionEntity;
    }

    public abstract double getInteractionDistance();

    public abstract void onInteraction(Interaction interaction);

    public abstract void onStopInteraction();

    public abstract boolean isInteracting();

    public abstract ArrayList<String> getTalkText();

    public abstract int getTalkTextPage();

    public abstract void setTalkTextPage(int ttp);
}
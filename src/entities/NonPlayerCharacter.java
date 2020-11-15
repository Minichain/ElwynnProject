package entities;

import enums.NonPlayerCharacterAction;
import enums.NonPlayerCharacterInteractionState;
import items.Item;

import java.util.ArrayList;

public abstract class NonPlayerCharacter extends LivingDynamicGraphicEntity {
    private InteractionEntity interactionEntity = null;

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

    public abstract void onInteraction();

    public abstract void onInteraction(NonPlayerCharacterInteractionState interaction);

    public abstract void onStopInteraction();

    public abstract boolean isInteracting();

    public abstract boolean isWaitingForInteractionSelection();

    public abstract boolean isTalking();

    public abstract boolean isSelling();

    public abstract boolean isBuying();

    public abstract ArrayList<String> getTalkText();

    public abstract int getTalkTextPage();

    public abstract void setTalkTextPage(int ttp);

    public abstract ArrayList<NonPlayerCharacterAction> getAvailableActions();

    public abstract int getSelectedItem();

    public abstract void setSelectedItem(int selectedItem);

    public abstract ArrayList<Item> getListOfItems();
}
package entities;

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
}
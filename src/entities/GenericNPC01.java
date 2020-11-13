package entities;

import audio.OpenALManager;
import enums.NonPlayerCharacterAction;
import enums.NonPlayerCharacterInteractionState;
import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Scene;
import text.FloatingTextEntity;

import java.awt.*;
import java.util.ArrayList;

public class GenericNPC01 extends NonPlayerCharacter {
    public static byte ENTITY_CODE = 81;
    private final double interactionDistance = 25;
    private ArrayList<String> talkText;
    private int talkTextPage;

    private NonPlayerCharacterInteractionState interactionState = NonPlayerCharacterInteractionState.NONE;
    private ArrayList<NonPlayerCharacterAction> availableActions = new ArrayList<>();
    private int selectedItem = 0;

    public ArrayList<Item> listOfItems = new ArrayList<>();

    public GenericNPC01(int x, int y) {
        super(x, y);
        init(x, y, false, false);
    }

    public GenericNPC01(int x, int y, boolean sells, boolean buys) {
        super(x, y);
        init(x, y, sells, buys);
    }

    private void init(int x, int y, boolean sells, boolean buys) {
        availableActions.add(NonPlayerCharacterAction.TALK);
        if (sells) availableActions.add(NonPlayerCharacterAction.SELL);
        if (buys) availableActions.add(NonPlayerCharacterAction.BUY);
        availableActions.add(NonPlayerCharacterAction.QUIT);
        setWorldCoordinates(new Coordinates(x, y));
        setSprite(SpriteManager.getInstance().NOTCH);
        talkText = new ArrayList<>();
        talkTextPage = 0;
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfNonPlayerCharacters().add(this);
        listOfItems = new ArrayList<>();
        listOfItems.add(new HealthPotion());
        listOfItems.add(new ManaPotion());
        listOfItems.add(new HastePotion());
    }

    @Override
    public void update(long timeElapsed) {

    }

    @Override
    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    @Override
    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    @Override
    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    @Override
    public void onDying() {

    }

    @Override
    public void hurt(float damage) {

    }

    @Override
    public double getInteractionDistance() {
        return interactionDistance;
    }

    @Override
    public void onInteraction() {
        if (!isInteracting()) {
            onInteraction(NonPlayerCharacterInteractionState.INTERACTION_SEL);
        } else if (isWaitingForInteractionSelection()) {
            switch (availableActions.get(selectedItem)) {
                case TALK:
                    onInteraction(NonPlayerCharacterInteractionState.TALKING);
                    break;
                case BUY:
                    onInteraction(NonPlayerCharacterInteractionState.BUYING);
                    break;
                case SELL:
                    onInteraction(NonPlayerCharacterInteractionState.SELLING);
                    break;
                case QUIT:
                default:
                    onInteraction(NonPlayerCharacterInteractionState.NONE);
                    break;
            }
        } else if (isTalking()) {
            if (getTalkTextPage() < (getTalkText().size() - 1)) {
                setTalkTextPage(getTalkTextPage() + 1);
            } else {    //If we press the interaction button and we are already interacting with the NPC...
                onStopInteraction();
                setTalkTextPage(0);
            }
        } else if (isSelling()) {
            if (selectedItem < getListOfItems().size()) {
                Item itemToBuy = getListOfItems().get(selectedItem);
                if (itemToBuy.getCost() <= Player.getInstance().getAmountOfGoldCoins()) {
                    Log.l("Buying " + itemToBuy.getName());
                    Player.getInstance().getListOfItems().add(itemToBuy);
                    Player.getInstance().setAmountOfGoldCoins(Player.getInstance().getAmountOfGoldCoins() - itemToBuy.getCost());
                    OpenALManager.playSound(OpenALManager.SOUND_CASH_01);
                    String text = "Buying " + itemToBuy.getName();
                    new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                            new Color(1f, 1f, 1f), 0.75, new double[]{0, -1});
                } else {
                    Log.l("Not enough money to buy " + itemToBuy.getName());
                    String text = "Not enough money";
                    new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                            new Color(1f, 0f, 0f), 0.75, new double[]{0, -1});
                }
            } else {
                onInteraction(NonPlayerCharacterInteractionState.NONE);
            }
        } else if (isBuying()) {
            if (selectedItem < Player.getInstance().getListOfItems().size()) {
                Item itemToSell = Player.getInstance().getListOfItems().get(selectedItem);
                Log.l("Selling " + itemToSell.getName());
                Player.getInstance().getListOfItems().remove(itemToSell);
                Player.getInstance().setAmountOfGoldCoins(Player.getInstance().getAmountOfGoldCoins() + itemToSell.getCost());
                OpenALManager.playSound(OpenALManager.SOUND_CASH_01);
                String text = "Selling " + itemToSell.getName();
                new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                        new Color(1f, 1f, 1f), 0.75, new double[]{0, -1});
            } else {
                onInteraction(NonPlayerCharacterInteractionState.NONE);
            }
        } else {    //If we press the interaction button and we are already interacting with the NPC...
            onStopInteraction();
            setTalkTextPage(0);
        }
    }

    @Override
    public void onInteraction(NonPlayerCharacterInteractionState interaction) {
        Log.l("Interacting with NPC. Interaction: " + interaction);
        selectedItem = 0;
        if (interaction == NonPlayerCharacterInteractionState.INTERACTION_SEL && availableActions.size() <= 2) {
            switch (availableActions.get(0)) {
                case TALK:
                default:
                    interactionState = NonPlayerCharacterInteractionState.TALKING;
                    break;
                case BUY:
                    interactionState = NonPlayerCharacterInteractionState.BUYING;
                    break;
                case SELL:
                    interactionState = NonPlayerCharacterInteractionState.SELLING;
                    break;
            }
        } else {
            interactionState = interaction;
        }
        switch (interaction) {
            case INTERACTION_SEL:
                Log.l("Interaction selection.");
                break;
            case TALKING:
                Log.l("Talking Text: \"" + talkText + "\"");
                break;
            case SELLING:
                Log.l("Selling");
                break;
            case BUYING:
                Log.l("Buying");
                break;
            default:
                break;
        }
    }

    @Override
    public void onStopInteraction() {
        Log.l("Stop interacting with an NPC.");
        interactionState = NonPlayerCharacterInteractionState.NONE;
    }

    @Override
    public boolean isInteracting() {
        return interactionState != NonPlayerCharacterInteractionState.NONE;
    }

    @Override
    public boolean isWaitingForInteractionSelection() {
        return interactionState == NonPlayerCharacterInteractionState.INTERACTION_SEL;
    }

    @Override
    public boolean isTalking() {
        return interactionState == NonPlayerCharacterInteractionState.TALKING;
    }

    @Override
    public boolean isSelling() {
        return interactionState == NonPlayerCharacterInteractionState.SELLING;
    }

    @Override
    public boolean isBuying() {
        return interactionState == NonPlayerCharacterInteractionState.BUYING;
    }

    public void setTalkText(String talkText) {
        this.talkText = new ArrayList<>();
        this.talkText.add(talkText);
    }

    public void setTalkText(ArrayList<String> talkText) {
        this.talkText = new ArrayList<>();
        this.talkText.addAll(talkText);
    }

    public ArrayList<String> getTalkText() {
        return talkText;
    }

    @Override
    public int getTalkTextPage() {
        return talkTextPage;
    }

    @Override
    public void setTalkTextPage(int ttp) {
        if (ttp >= talkText.size()) ttp = talkText.size() - 1;
        talkTextPage = ttp;
    }

    @Override
    public ArrayList<NonPlayerCharacterAction> getAvailableActions() {
        return availableActions;
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void setSelectedItem(int selectedItem) {
        int size = 0;
        if (isWaitingForInteractionSelection()) {
            size = availableActions.size();
            if (selectedItem < 0) selectedItem = size - 1;
            else selectedItem = selectedItem % size;
        } else if (isSelling()) {
            size = getListOfItems().size() + 1;
            if (selectedItem < 0) selectedItem = size - 1;
            else selectedItem = selectedItem % size;
        } else if (isBuying()) {
            size = Player.getInstance().getListOfItems().size() + 1;
            if (selectedItem < 0) selectedItem = size - 1;
            else selectedItem = selectedItem % size;
        }
        this.selectedItem = selectedItem;
    }

    @Override
    public ArrayList<Item> getListOfItems() {
        return listOfItems;
    }
}

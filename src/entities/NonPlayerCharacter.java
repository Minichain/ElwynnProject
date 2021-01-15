package entities;

import audio.OpenALManager;
import enums.NonPlayerCharacterAction;
import enums.NonPlayerCharacterInteractionState;
import items.*;
import main.Coordinates;
import main.Log;
import main.Strings;
import main.Texture;
import scene.Scene;
import text.FloatingTextEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class NonPlayerCharacter extends LivingDynamicGraphicEntity {
    public static byte ENTITY_CODE = 9;
    private final double interactionDistance = 25;
    private String talkTextStringName;
    private String talkTextStringArgs;
    private ArrayList<String> talkText;
    private int talkTextPage;
    private NonPlayerCharacterInteractionState interactionState = NonPlayerCharacterInteractionState.NONE;
    private ArrayList<NonPlayerCharacterAction> availableActions = new ArrayList<>();
    private int selectedItem = 0;
    private InteractionEntity interactionEntity = null;

    public ArrayList<Item> listOfItems = new ArrayList<>();

    public enum NonPlayerCharacterType {
        NPC01(0), NPC02(1), NPC03(2), NPC04(3);

        public int value;

        NonPlayerCharacterType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public Sprite getSprite() {
            switch (this) {
                case NPC01:
                default:
                    return SpriteManager.getInstance().NPC01;
                case NPC02:
                    return SpriteManager.getInstance().NPC02;
                case NPC03:
                    return SpriteManager.getInstance().NOTCH;
                case NPC04:
                    return SpriteManager.getInstance().SARA;
            }
        }
    }

    public NonPlayerCharacter(int x, int y, int t) {
        super(x, y);
        init(x, y, false, false, t);
    }

    public NonPlayerCharacter(int x, int y, boolean sells, boolean buys, int t) {
        super(x, y);
        init(x, y, sells, buys, t);
    }

    private void init(int x, int y, boolean sells, boolean buys, int t) {
        this.type = t;
        availableActions.add(NonPlayerCharacterAction.TALK);
        if (sells) availableActions.add(NonPlayerCharacterAction.SELL);
        if (buys) availableActions.add(NonPlayerCharacterAction.BUY);
        availableActions.add(NonPlayerCharacterAction.QUIT);
        setWorldCoordinates(new Coordinates(x, y));
        NonPlayerCharacterType nonPlayerCharacterType = NonPlayerCharacterType.values()[type];
        setSprite(nonPlayerCharacterType.getSprite());
        talkText = new ArrayList<>();
        talkTextPage = 0;
        Scene.getInstance().getListOfGraphicEntities().add(this);
        Scene.getInstance().getListOfNonPlayerCharacters().add(this);
        listOfItems = new ArrayList<>();
        listOfItems.add(new HealthPotion());
        listOfItems.add(new ManaPotion());
        listOfItems.add(new HastePotion());
    }

    public InteractionEntity getInteractionEntity() {
        return interactionEntity;
    }

    public void setInteractionEntity(InteractionEntity interactionEntity) {
        this.interactionEntity = interactionEntity;
    }

    public void update(long timeElapsed) {
        if (isTalking()) {
            String[] s;
            if (talkTextStringArgs != null) {
                s = Strings.getString(talkTextStringName, Strings.getString(talkTextStringArgs)).split("/nl");
            } else {
                s = Strings.getString(talkTextStringName).split("/nl");
            }
            ArrayList<String> arrayList = new ArrayList<>();
            Collections.addAll(arrayList, s);
            setTalkText(arrayList);
        }
    }

    public Texture getSpriteSheet() {
        return getSprite().getSpriteSheet();
    }

    public void drawSprite(int x, int y) {
        getSprite().draw(x, y, (int) getSpriteCoordinateFromSpriteSheetX(), (int) getSpriteCoordinateFromSpriteSheetY(), 1f);
    }

    public byte getEntityCode() {
        return ENTITY_CODE;
    }

    public void onDying() {

    }

    public void hurt(float damage) {

    }

    public double getInteractionDistance() {
        return interactionDistance;
    }

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
                    if (Player.getInstance().getInventory().isFreeSpace(itemToBuy)) {
                        Log.l("Buying " + itemToBuy.getName());
                        Player.getInstance().getInventory().storeItem(itemToBuy);
                        Player.getInstance().getInventory().removeItem(GoldCoin.class, itemToBuy.getCost());
                        OpenALManager.playSound(OpenALManager.SOUND_CASH_01);
                        String text = Strings.getString("ui_buying_npc", itemToBuy.getName());
                        new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                                new Color(1f, 1f, 1f), 0.75, new double[]{0, -1});
                    } else {
                        String text = Strings.getString("ui_not_enough_space");
                        new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                                new Color(1f, 0f, 0f), 0.75, new double[]{0, -1});
                    }
                } else {
                    Log.l("Not enough money to buy " + itemToBuy.getName());
                    String text = Strings.getString("ui_not_enough_money");
                    new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                            new Color(1f, 0f, 0f), 0.75, new double[]{0, -1});
                }
            } else {
                onInteraction(NonPlayerCharacterInteractionState.INTERACTION_SEL);
            }
        } else if (isBuying()) {
            if (selectedItem < Player.getInstance().getListOfItemsExceptGoldCoins().size()) {
                Item itemToSell = Player.getInstance().getListOfItemsExceptGoldCoins().get(selectedItem);
                Log.l("Selling " + itemToSell.getName());
                Player.getInstance().getInventory().removeItem(itemToSell.getClass());
                Player.getInstance().getInventory().storeItem(new GoldCoin(), itemToSell.getCost());
                OpenALManager.playSound(OpenALManager.SOUND_CASH_01);
                String text = Strings.getString("ui_selling_npc", itemToSell.getName());
                new FloatingTextEntity(Player.getInstance().getWorldCoordinates().x, Player.getInstance().getWorldCoordinates().y, text,
                        new Color(1f, 1f, 1f), 0.75, new double[]{0, -1});
            } else {
                onInteraction(NonPlayerCharacterInteractionState.INTERACTION_SEL);
            }
        } else {    //If we press the interaction button and we are already interacting with the NPC...
            onStopInteraction();
            setTalkTextPage(0);
        }
    }

    public void onInteraction(NonPlayerCharacterInteractionState interaction) {
        Log.l("Interacting with NPC. Interaction: " + interaction);
        selectedItem = 0;
        interactionState = interaction;
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
        }

        if (interaction == NonPlayerCharacterInteractionState.TALKING) {
            Log.l("Talking Text: \"" + talkText + "\"");
        }
    }

    public void onStopInteraction() {
        Log.l("Stop interacting with an NPC.");
        interactionState = NonPlayerCharacterInteractionState.NONE;
    }

    public boolean isInteracting() {
        return interactionState != NonPlayerCharacterInteractionState.NONE;
    }

    public boolean isWaitingForInteractionSelection() {
        return interactionState == NonPlayerCharacterInteractionState.INTERACTION_SEL;
    }

    public boolean isTalking() {
        return interactionState == NonPlayerCharacterInteractionState.TALKING;
    }

    public boolean isSelling() {
        return interactionState == NonPlayerCharacterInteractionState.SELLING;
    }

    public boolean isBuying() {
        return interactionState == NonPlayerCharacterInteractionState.BUYING;
    }

    public void setTalkTextStringName(String talkTextStringName) {
        setTalkTextStringName(talkTextStringName, null);
    }

    public void setTalkTextStringName(String talkTextStringName, String talkTextStringArgs) {
        this.talkTextStringName = talkTextStringName;
        this.talkTextStringArgs = talkTextStringArgs;
    }

    private void setTalkText(ArrayList<String> talkText) {
        this.talkText = new ArrayList<>();
        this.talkText.addAll(talkText);
    }

    public ArrayList<String> getTalkText() {
        return talkText;
    }

    public int getTalkTextPage() {
        return talkTextPage;
    }

    public void setTalkTextPage(int ttp) {
        if (ttp >= talkText.size()) ttp = talkText.size() - 1;
        talkTextPage = ttp;
    }

    public ArrayList<NonPlayerCharacterAction> getAvailableActions() {
        return availableActions;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

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
            size = Player.getInstance().getListOfItemsExceptGoldCoins().size() + 1;
            if (selectedItem < 0) selectedItem = size - 1;
            else selectedItem = selectedItem % size;
        }
        this.selectedItem = selectedItem;
    }

    public ArrayList<Item> getListOfItems() {
        return listOfItems;
    }
}
package entities;

import main.Coordinates;
import main.Log;
import main.Texture;
import scene.Scene;

import java.util.ArrayList;

public class GenericNPC01 extends NonPlayerCharacter {
    public static byte ENTITY_CODE = 81;
    private final double interactionDistance = 25;
    private ArrayList<String> talkText;
    private int talkTextPage;
    private boolean talking = false;
    private boolean selling = false;

    public GenericNPC01(int x, int y) {
        super(x, y);
        init(x, y);
    }

    private void init(int x, int y) {
        setWorldCoordinates(new Coordinates(x, y));
        setSprite(SpriteManager.getInstance().NOTCH);
        talkText = new ArrayList<>();
        talkTextPage = 0;
        Scene.getInstance().getListOfEntities().add(this);
        Scene.getInstance().getListOfNonPlayerCharacters().add(this);
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
    public void onInteraction(Interaction interaction) {
        Log.l("Interacting with NPC. Interaction: " + interaction);
        switch (interaction) {
            case TALKING:
                Log.l("Talking Text: \"" + talkText + "\"");
                talking = true;
                break;
            case SELLING:
            default:
                selling = true;
        }
    }

    @Override
    public void onStopInteraction() {
        Log.l("Stop interacting with an NPC.");
        talking = false;
        selling = false;
    }

    @Override
    public boolean isInteracting() {
        return talking || selling;
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
}

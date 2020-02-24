package menu;

import main.*;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class Menu {
    private static Menu instance = null;
    private ArrayList<MenuComponent> listOfMenuComponents;
    private boolean showing;
    private int gapBetweenComponents = 30;
    private Coordinates coordinates;

    public Menu() {
        listOfMenuComponents = new ArrayList<>();

        MenuButton resumeGame = new MenuButton("Resume Game");
        resumeGame.setButtonAction(MenuButton.ButtonAction.LEAVE_MENU);
        listOfMenuComponents.add(resumeGame);

        MenuButton fullScreen = new MenuButton("Enable/Disable FullScreen");
        fullScreen.setButtonAction(MenuButton.ButtonAction.FULL_SCREEN);
        listOfMenuComponents.add(fullScreen);

        MenuButton creativeMode = new MenuButton("Enable/Disable Creative Mode");
        creativeMode.setButtonAction(MenuButton.ButtonAction.CREATIVE_MODE);
        listOfMenuComponents.add(creativeMode);

        MenuSlideBar audioLevel = new MenuSlideBar("Audio Level");
        listOfMenuComponents.add(audioLevel);

        MenuSelector resolutionSelector = new MenuSelector("Resolution");
        listOfMenuComponents.add(resolutionSelector);

        MenuButton exitGame = new MenuButton("Exit Game");
        exitGame.setButtonAction(MenuButton.ButtonAction.EXIT_GAME);
        listOfMenuComponents.add(exitGame);
    }

    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setShowing(boolean showing) {
        if (showing) GameStatus.setStatus(GameStatus.Status.PAUSED);
        else GameStatus.setStatus(GameStatus.Status.RUNNING);
        this.showing = showing;
    }

    public ArrayList<MenuComponent> getListOfMenuComponents() {
        return listOfMenuComponents;
    }

    public void render() {
        float menuHeight = 0f;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            if (i > 0) menuHeight += gapBetweenComponents;
            menuHeight += listOfMenuComponents.get(i).height;
        }

        coordinates = new Coordinates((float) Parameters.getResolutionWidth() / 2, (float) Parameters.getResolutionHeight() / 2 - menuHeight / 2);

        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            listOfMenuComponents.get(i).update(i, gapBetweenComponents);
        }

        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLES);
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            MenuComponent component = listOfMenuComponents.get(i);
            component.renderBackground();
        }
        glEnd();

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            MenuComponent component = listOfMenuComponents.get(i);
            component.renderInfo();
        }
        glEnd();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}

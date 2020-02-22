package menu;

import main.*;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class Menu {
    private static Menu instance = null;
    private ArrayList<MenuComponent> listOfMenuComponents;
    private boolean showing;
    private int gapBetweenButtons = 50;
    private Coordinates coordinates;

    public Menu() {
        coordinates = new Coordinates(Parameters.getResolutionWidth() / 2, 500f / Window.getCameraWindowScaleFactor()[1]);
        listOfMenuComponents = new ArrayList<>();
        MenuButton resumeGame = new MenuButton("Resume");
        resumeGame.setButtonAction(MenuButton.ButtonAction.LEAVE_MENU);
        listOfMenuComponents.add(resumeGame);
        MenuButton exitGame = new MenuButton("Exit Game");
        exitGame.setButtonAction(MenuButton.ButtonAction.EXIT_GAME);
        listOfMenuComponents.add(exitGame);
        MenuSlideBar audioLevel = new MenuSlideBar("Audio Level");
        listOfMenuComponents.add(audioLevel);
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
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            listOfMenuComponents.get(i).update(i, gapBetweenButtons);
        }

        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
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

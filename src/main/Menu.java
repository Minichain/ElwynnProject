package main;

import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class Menu {
    private static Menu instance = null;
    private ArrayList<MenuComponent> listOfMenuComponents;
    private boolean showing;
    private int gapBetweenButtons = 50;

    public Menu() {
        listOfMenuComponents = new ArrayList<MenuComponent>();
        MenuComponent resumeGame = new MenuComponent("Resume");
        resumeGame.setButtonAction(MenuComponent.ButtonAction.LEAVE_MENU);
        listOfMenuComponents.add(resumeGame);
        MenuComponent exitGame = new MenuComponent("Exit Game");
        exitGame.setButtonAction(MenuComponent.ButtonAction.EXIT_GAME);
        listOfMenuComponents.add(exitGame);
        MenuComponent audioSettings = new MenuComponent("Audio Settings");
        audioSettings.setButtonAction(MenuComponent.ButtonAction.EXIT_GAME);
        listOfMenuComponents.add(audioSettings);
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
            if (component.isPressed()) {
                MyOpenGL.drawRectangle(component.x, component.y, component.width, component.height, 0.7, 0.9f);
            } else if (component.isMouseOver()) {
                MyOpenGL.drawRectangle(component.x, component.y, component.width, component.height, 0.6, 0.7f);
            } else {
                MyOpenGL.drawRectangle(component.x, component.y, component.width, component.height, 0.5, 0.5f);
            }
        }
        glEnd();

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            MenuComponent component = listOfMenuComponents.get(i);
            TextRendering.renderText(component.x, component.y, listOfMenuComponents.get(i).getText(), 2, true);
        }
        glEnd();
    }
}

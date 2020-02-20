package main;

import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class Menu {
    private static Menu instance = null;
    private ArrayList<MenuComponent> listOfMenuComponents;
    private boolean showing;

    public Menu() {
        listOfMenuComponents = new ArrayList<MenuComponent>();
        listOfMenuComponents.add(new MenuComponent("Button 01"));
        listOfMenuComponents.add(new MenuComponent("Button 02"));
        listOfMenuComponents.add(new MenuComponent("Button 03"));
        listOfMenuComponents.add(new MenuComponent("Button 04"));
        listOfMenuComponents.add(new MenuComponent("Button 05"));
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
        this.showing = showing;
    }

    public ArrayList<MenuComponent> getListOfMenuComponents() {
        return listOfMenuComponents;
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        int width = 400;
        int height = 50;
        int gapBetweenButtons = 50;
        int x = Parameters.getResolutionWidth() / 2 - width / 2;
        int y = Parameters.getResolutionHeight() / 2 - height / 2;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            if (MathUtils.isMouseInsideRectangle(x, y, x + width, y + height)) {
                MyOpenGL.drawRectangle(x, y, width, height, 0.6, 0.7f);
            } else {
                MyOpenGL.drawRectangle(x, y, width, height, 0.5, 0.5f);
            }
            y += height + gapBetweenButtons;
        }
        glEnd();

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);

        x = Parameters.getResolutionWidth() / 2 - width / 2;
        y = Parameters.getResolutionHeight() / 2 - height / 2;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            TextRendering.renderText(x, y, listOfMenuComponents.get(i).getText(), 2, true);
            y += height + gapBetweenButtons;
        }
        glEnd();
    }
}

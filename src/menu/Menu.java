package menu;

import main.*;
import text.TextRendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class Menu {
    private static Menu instance = null;
    private boolean showing;
    private Coordinates coordinates;
    private ArrayList<MenuComponent> listOfMenuComponents;
    private float gapBetweenComponents;

    private MenuScrollBar menuScrollBar;
    private float maxMenuHeight;
    private float menuHeight;

    public Menu() {
        gapBetweenComponents = 30f * Parameters.getResolutionFactor();
        maxMenuHeight = (Window.getHeight() / 2f) * Parameters.getResolutionFactor();

        listOfMenuComponents = new ArrayList<>();

        MenuButton resumeGame = new MenuButton("Resume Game", MenuButton.ButtonAction.LEAVE_MENU);
        listOfMenuComponents.add(resumeGame);

        MenuButton fullScreen = new MenuButton("Enable/Disable FullScreen", MenuButton.ButtonAction.FULL_SCREEN);
        listOfMenuComponents.add(fullScreen);

        MenuButton creativeMode = new MenuButton("Enable/Disable Creative Mode", MenuButton.ButtonAction.CREATIVE_MODE);
        listOfMenuComponents.add(creativeMode);

        MenuSlider musicSoundLevel = new MenuSlider("Music", MenuSlider.SliderAction.MUSIC_SOUND_LEVEL);
        listOfMenuComponents.add(musicSoundLevel);

        MenuSlider effectSoundLevel = new MenuSlider("Sound Effects", MenuSlider.SliderAction.EFFECT_SOUND_LEVEL);
        listOfMenuComponents.add(effectSoundLevel);

        MenuSlider ambienceSoundLevel = new MenuSlider("Ambience", MenuSlider.SliderAction.AMBIENCE_SOUND_LEVEL);
        listOfMenuComponents.add(ambienceSoundLevel);

        MenuSelector resolutionSelector = new MenuSelector("Resolution");
        listOfMenuComponents.add(resolutionSelector);

        MenuButton spawnEnemiesButton = new MenuButton("", MenuButton.ButtonAction.SPAWN_ENEMIES);
        listOfMenuComponents.add(spawnEnemiesButton);

        MenuButton exitGame = new MenuButton("Exit Game", MenuButton.ButtonAction.EXIT_GAME);
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

    public float getMaxMenuHeight() {
        return maxMenuHeight;
    }

    public float getMenuHeight() {
        return menuHeight;
    }

    public void render(long timeElapsed) {
        menuHeight = 0f;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            if (i > 0) menuHeight += gapBetweenComponents;
            menuHeight += listOfMenuComponents.get(i).height;
        }

        if (menuScrollBar == null && menuHeight > maxMenuHeight) {
            menuScrollBar = new MenuScrollBar();
        }

        double menuCoordinateY;
        if (menuHeight > maxMenuHeight) {
            menuCoordinateY = Parameters.getResolutionHeight() / 2.0 - maxMenuHeight / 2.0;
        } else {
            menuCoordinateY = Parameters.getResolutionHeight() / 2.0 - menuHeight / 2.0;
        }
        coordinates = new Coordinates(Parameters.getResolutionWidth() / 2.0,   menuCoordinateY);

        MenuComponent component;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            component = listOfMenuComponents.get(i);

            int newY = (int) (Menu.getInstance().getCoordinates().y + (component.height + gapBetweenComponents) * i);
            if (menuScrollBar != null) {
                newY += (menuScrollBar.y - menuScrollBar.getScroll().y) * menuHeight / maxMenuHeight;
//                newY *= Parameters.getResolutionFactor();
            }
            component.update((int) Menu.getInstance().getCoordinates().x - component.width / 2, (int) (newY * Parameters.getResolutionFactor()),
                    (int) (500f * Parameters.getResolutionFactor()), (int) (45f * Parameters.getResolutionFactor()));
        }

        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            component = listOfMenuComponents.get(i);
            component.renderBackground();
        }
        glEnd();

        TextRendering.fontSpriteWhite.bind();
        glEnable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_QUADS);
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            component = listOfMenuComponents.get(i);
            component.renderInfo();
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);

        if (menuScrollBar != null) {
            menuScrollBar.update((int) (Menu.getInstance().getCoordinates().x - menuScrollBar.width / 2f + 300f * Parameters.getResolutionFactor()),
                    (int) (Menu.getInstance().getCoordinates().y),
                    (int) (20f * Parameters.getResolutionFactor()),
                    (int) (maxMenuHeight * Parameters.getResolutionFactor()));
            menuScrollBar.renderBackground();
            menuScrollBar.renderInfo();
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public MenuScrollBar getMenuScrollBar() {
        return menuScrollBar;
    }
}

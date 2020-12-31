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
        listOfMenuComponents = new ArrayList<>();

        MenuButton resumeGame = new MenuButton(MenuButton.ButtonAction.LEAVE_MENU);
        listOfMenuComponents.add(resumeGame);

        MenuButton fullScreen = new MenuButton(MenuButton.ButtonAction.FULL_SCREEN);
        listOfMenuComponents.add(fullScreen);

        MenuButton creativeMode = new MenuButton(MenuButton.ButtonAction.CREATIVE_MODE);
        listOfMenuComponents.add(creativeMode);

        MenuButton useShaders = new MenuButton(MenuButton.ButtonAction.SHADERS);
        listOfMenuComponents.add(useShaders);

        MenuSlider musicSoundLevel = new MenuSlider(MenuSlider.SliderAction.MUSIC_SOUND_LEVEL);
        listOfMenuComponents.add(musicSoundLevel);

        MenuSlider effectSoundLevel = new MenuSlider(MenuSlider.SliderAction.EFFECT_SOUND_LEVEL);
        listOfMenuComponents.add(effectSoundLevel);

        MenuSlider ambienceSoundLevel = new MenuSlider(MenuSlider.SliderAction.AMBIENCE_SOUND_LEVEL);
        listOfMenuComponents.add(ambienceSoundLevel);

        MenuSelector resolutionSelector = new MenuSelector(MenuSelector.SelectorAction.RESOLUTION);
        listOfMenuComponents.add(resolutionSelector);

        MenuSelector languageSelector = new MenuSelector(MenuSelector.SelectorAction.LANGUAGE);
        listOfMenuComponents.add(languageSelector);

        MenuButton spawnEnemiesButton = new MenuButton(MenuButton.ButtonAction.SPAWN_ENEMIES);
        listOfMenuComponents.add(spawnEnemiesButton);

        MenuSlider spawnRate = new MenuSlider(MenuSlider.SliderAction.SPAWN_RATE);
        listOfMenuComponents.add(spawnRate);

        MenuSlider gameTimeSpeed = new MenuSlider(MenuSlider.SliderAction.GAME_TIME_SPEED);
        listOfMenuComponents.add(gameTimeSpeed);

        MenuSlider renderDistance = new MenuSlider(MenuSlider.SliderAction.RENDER_DISTANCE);
        listOfMenuComponents.add(renderDistance);

        MenuSlider updateDistance = new MenuSlider(MenuSlider.SliderAction.UPDATE_DISTANCE);
        listOfMenuComponents.add(updateDistance);

        MenuSlider framesPerSecond = new MenuSlider(MenuSlider.SliderAction.FRAMES_PER_SECOND);
        listOfMenuComponents.add(framesPerSecond);

        MenuButton exitGame = new MenuButton(MenuButton.ButtonAction.EXIT_GAME);
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
        gapBetweenComponents = 30f * Parameters.getResolutionFactor();
        maxMenuHeight = 600f * Parameters.getResolutionFactor();

        /** COMPUTE MENU HEIGHT **/
        menuHeight = 0f;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            if (i > 0) menuHeight += gapBetweenComponents;
            menuHeight += listOfMenuComponents.get(i).height;
        }

        /** IF THE MENU IS TOO HIGH, CREATE AN SCROLL BAR **/
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

        /** UPDATE COMPONENTS **/
        MenuComponent component;
        for (int i = 0; i < listOfMenuComponents.size(); i++) {
            component = listOfMenuComponents.get(i);
            int newXCoordinate = (int) (Menu.getInstance().getCoordinates().x);
            int newYCoordinate = (int) (Menu.getInstance().getCoordinates().y);
            if (i > 0) {
                for (int j = 0; j < i; j++) {
                    newYCoordinate += (listOfMenuComponents.get(i - j).height + gapBetweenComponents);
                }
            }
            if (menuScrollBar != null) {
                newYCoordinate += (menuScrollBar.y - menuScrollBar.getScroll().y) * menuHeight / maxMenuHeight;
            }
            component.update(newXCoordinate, newYCoordinate);
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
                    (int) maxMenuHeight);
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

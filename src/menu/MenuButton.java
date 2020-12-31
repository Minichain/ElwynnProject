package menu;

import listeners.InputListenerManager;
import main.*;
import text.TextRendering;
import utils.MathUtils;

public class MenuButton extends MenuComponent {
    private ButtonAction buttonAction;

    public enum ButtonAction {
        NONE, LEAVE_MENU, EXIT_GAME, FULL_SCREEN, CREATIVE_MODE, SPAWN_ENEMIES, SHADERS
    }

    public MenuButton(ButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }

    @Override
    public void update(int x, int y) {
        int width = (int) (500f * Parameters.getResolutionFactor());
        int height = (int) (50f * Parameters.getResolutionFactor());
        update(x, y, width, height);
    }

    @Override
    public void update(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        this.x = x - width / 2;
        this.y = y;

        switch (buttonAction) {
            case LEAVE_MENU:
                setText(Strings.RESUME_GAME);
                break;
            case FULL_SCREEN:
                if (Parameters.isFullScreen()) {
                    setText(Strings.DISABLE_FULL_SCREEN);
                } else {
                    setText(Strings.ENABLE_FULL_SCREEN);
                }
                break;
            case CREATIVE_MODE:
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                    setText(Strings.ENABLE_CREATIVE_MODE);
                } else {
                    setText(Strings.DISABLE_CREATIVE_MODE);
                }
                break;
            case SPAWN_ENEMIES:
                if (Parameters.isSpawnEnemies()) {
                    setText(Strings.DISABLE_ENEMIES_SPAWN);
                } else {
                    setText(Strings.ENABLE_ENEMIES_SPAWN);
                }
                break;
            case SHADERS:
                if (Parameters.isShadersEnabled()) {
                    setText(Strings.DISABLE_SHADERS);
                } else {
                    setText(Strings.ENABLE_SHADERS);
                }
                break;
            case EXIT_GAME:
                setText(Strings.EXIT_GAME);
                break;
            case NONE:
            default:
                setText(Strings.NONE);
                break;
        }

        setMouseOver(MathUtils.isMouseInsideRectangle(this.x, this.y, this.x + this.width, this.y + this.height));
        if (isMouseOver() && InputListenerManager.leftMouseButtonPressed) {
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) {
                performAction(buttonAction);
            }
            setPressed(false);
        }
    }

    @Override
    public void renderBackground() {
        if (isPressed()) {
            OpenGLManager.drawRectangle(x, y, width, height, 0.8, 0.2f);
        } else if (isMouseOver()) {
            OpenGLManager.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else {
            OpenGLManager.drawRectangle(x, y, width, height, 0.5, 0.6f);
        }
    }

    @Override
    public void renderInfo() {
        float scale = 2 * Parameters.getResolutionFactor();
        int textX = (int) (x + (width / 2f) - (TextRendering.CHARACTER_WIDTH * scale * getText().length() / 2f));
        int textY = (int) (y + (height / 2f) - (TextRendering.CHARACTER_HEIGHT * scale / 2f));
        TextRendering.renderText(textX, textY, getText(), scale, true);
    }

    private void performAction(ButtonAction buttonAction) {
        switch (buttonAction) {
            case FULL_SCREEN:
                Window.setFullScreen(!Parameters.isFullScreen());
                break;
            case CREATIVE_MODE:
                if (GameMode.getGameMode() == GameMode.Mode.NORMAL) {
                    GameMode.setGameMode(GameMode.Mode.CREATIVE);
                } else if (GameMode.getGameMode() == GameMode.Mode.CREATIVE) {
                    GameMode.setGameMode(GameMode.Mode.NORMAL);
                }
                break;
            case LEAVE_MENU:
                Menu.getInstance().setShowing(!Menu.getInstance().isShowing());
                break;
            case SPAWN_ENEMIES:
                Parameters.setSpawnEnemies(!Parameters.isSpawnEnemies());
                break;
            case SHADERS:
                Parameters.setShadersEnabled(!Parameters.isShadersEnabled());
                break;
            case EXIT_GAME:
                GameStatus.setStatus(GameStatus.Status.STOPPED);
                break;
            case NONE:
            default:
                break;
        }
    }
}

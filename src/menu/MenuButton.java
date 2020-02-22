package menu;

import listeners.MyInputListener;
import main.GameStatus;
import main.MyOpenGL;
import main.TextRendering;
import utils.MathUtils;

public class MenuButton extends MenuComponent {
    private ButtonAction buttonAction = ButtonAction.NONE;

    public enum ButtonAction {
        NONE, LEAVE_MENU, EXIT_GAME
    }

    public MenuButton(String text) {
        setText(text);
    }

    @Override
    public void update(int position, int gapBetweenButtons) {
        x = (int) Menu.getInstance().getCoordinates().x - width / 2;
        y = (int) Menu.getInstance().getCoordinates().y + (height + gapBetweenButtons) * position;
        setMouseOver(MathUtils.isMouseInsideRectangle(x, y, x + width, y + height));
        if (isMouseOver() && MyInputListener.leftMouseButtonPressed) {
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) performAction(buttonAction);
            setPressed(false);
        }
    }

    @Override
    public void renderBackground() {
        if (isPressed()) {
            MyOpenGL.drawRectangle(x, y, width, height, 0.8, 0.2f);
        } else if (isMouseOver()) {
            MyOpenGL.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else {
            MyOpenGL.drawRectangle(x, y, width, height, 0.5, 0.6f);
        }
    }

    @Override
    public void renderInfo() {
        int scale = 2;
        int textX = x + (width / 2) - (TextRendering.CHARACTER_WIDTH * scale * getText().length() / 2);
        int textY = y + (height / 2) - (TextRendering.CHARACTER_HEIGHT * scale / 2);
        TextRendering.renderText(textX, textY, getText(), scale);
    }

    private static void performAction(ButtonAction buttonAction) {
        switch (buttonAction) {
            case LEAVE_MENU:
                Menu.getInstance().setShowing(!Menu.getInstance().isShowing());
                break;
            case EXIT_GAME:
                GameStatus.setStatus(GameStatus.Status.STOPPED);
                break;
            case NONE:
            default:
                break;
        }
    }

    public ButtonAction getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(ButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }
}

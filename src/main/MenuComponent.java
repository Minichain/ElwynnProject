package main;

import listeners.MyInputListener;
import utils.MathUtils;

public class MenuComponent {
    private String text;
    private boolean mouseOver = false;
    private boolean pressed = false;
    public int x = 0;
    public int y = 0;
    public int width = 400;
    public int height = 50;
    private ButtonAction buttonAction = ButtonAction.NONE;

    public enum ButtonAction {
        NONE, LEAVE_MENU, EXIT_GAME
    }

    public MenuComponent(String text) {
        this.text = text;
    }

    public void update(int position, int gapBetweenButtons) {
        x = Parameters.getResolutionWidth() / 2 - width / 2;
        y = (Parameters.getResolutionHeight() / 2 - height / 2) + (height + gapBetweenButtons) * position;
        mouseOver = MathUtils.isMouseInsideRectangle(x, y, x + width, y + height);
        if (mouseOver && MyInputListener.leftMouseButtonPressed) {
            if (!pressed) performAction(buttonAction);
            pressed = true;
        } else {
            pressed = false;
        }
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

    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isPressed() {
        return pressed;
    }

    public String getText() {
        return text;
    }
}

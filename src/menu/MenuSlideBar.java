package menu;

import listeners.MyInputListener;
import main.MyOpenGL;
import main.Parameters;
import main.TextRendering;
import utils.MathUtils;

public class MenuSlideBar extends MenuComponent {
    private double progress = 0.5;    // From 0.0 to 1.0

    public MenuSlideBar(String text) {
        setText(text);
    }

    @Override
    public void update(int position, int gapBetweenButtons) {
        x = (int) Menu.getInstance().getCoordinates().x - width / 2;
        y = (int) Menu.getInstance().getCoordinates().y + (height + gapBetweenButtons) * position;
        setMouseOver(MathUtils.isMouseInsideRectangle(x, y, x + width, y + height));
        if (isMouseOver() && MyInputListener.leftMouseButtonPressed) {
            progress = (double) (MyInputListener.getMouseCameraCoordinates()[0] - x) / (double) width;
            Parameters.setSoundLevel((float) progress);
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) {
            }
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
        MyOpenGL.drawRectangle(x, y, width * (float) progress, height, 0.8, 0.8f);
    }

    @Override
    public void renderInfo() {
        String textWithPercentage = getText() + " (" + (int) (progress * 100) + "%)";
        int scale = 2;
        int textX = x + (width / 2) - (TextRendering.CHARACTER_WIDTH * scale * textWithPercentage.length() / 2);
        int textY = y + (height / 2) - (TextRendering.CHARACTER_HEIGHT * scale / 2);
        TextRendering.renderText(textX, textY, textWithPercentage, scale);
    }
}

package menu;

import listeners.MyInputListener;
import main.MyOpenGL;
import main.Parameters;
import main.TextRendering;
import utils.MathUtils;

public class MenuSelector extends MenuComponent {
    private Selector previousSelector;
    private Selector nextSelector;
    private int selectedValue;
    private Resolution selectedResolution;

    public MenuSelector(String text) {
        setText(text);
        previousSelector = new Selector(new int[]{x + 20, y + height / 2}, 18, true);
        nextSelector = new Selector(new int[]{x + width - 20, y + height / 2}, 18, false);
        selectedResolution = Resolution.getResolution(Parameters.getResolutionWidth(), Parameters.getResolutionHeight());
        selectedValue = selectedResolution.getResolutionValue();
    }

    @Override
    public void update(int position, int gapBetweenComponents) {
        x = (int) Menu.getInstance().getCoordinates().x - width / 2;
        y = (int) Menu.getInstance().getCoordinates().y + (height + gapBetweenComponents) * position;

        previousSelector.recenter(new int[]{x + 20, y + height / 2});
        nextSelector.recenter(new int[]{x + width - 20, y + height / 2});

        setMouseOver(MathUtils.isMouseInsideRectangle(x, y, x + width, y + height));
        if (isMouseOver() && MyInputListener.leftMouseButtonPressed) {
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) {
                // Do nothing
            }
            setPressed(false);
        }

        previousSelector.update();
        nextSelector.update();
    }

    @Override
    public void renderBackground() {
        if (isPressed()) {
            MyOpenGL.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else if (isMouseOver()) {
            MyOpenGL.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else {
            MyOpenGL.drawRectangle(x, y, width, height, 0.5, 0.6f);
        }
        previousSelector.render();
        nextSelector.render();
    }

    @Override
    public void renderInfo() {
        String textInfo = getText() + " (" + selectedResolution.toString() + ")";
        int scale = 2;
        int textX = x + (width / 2) - (TextRendering.CHARACTER_WIDTH * scale * textInfo.length() / 2);
        int textY = y + (height / 2) - (TextRendering.CHARACTER_HEIGHT * scale / 2);
        TextRendering.renderText(textX, textY, textInfo, scale, true);
    }

    public class Selector {
        int[] vertex1;
        int[] vertex2;
        int[] vertex3;
        boolean pressed;
        boolean mouseOver;
        int size;
        boolean leftOriented;

        public Selector(int[] center, int size, boolean leftOriented) {
            this.leftOriented = leftOriented;
            this.size = size;
            recenter(center);
        }

        public void update() {
            mouseOver = MathUtils.isPointInsideTriangle(MyInputListener.getMouseCameraCoordinates(), vertex1, vertex2, vertex3);
            if (mouseOver && MyInputListener.leftMouseButtonPressed) {
                pressed = true;
            } else {
                if (pressed && mouseOver) {
                    if (leftOriented) {
                        if (selectedValue > 0) {
                            selectedValue--;
                        }
                    } else {
                        selectedValue++;
                    }
                    selectedValue = selectedValue % Resolution.values().length;
                    selectedResolution = Resolution.values()[selectedValue];
                    Parameters.setResolution(selectedResolution);
                }
                pressed = false;
            }
        }

        public void render() {
            if (pressed) {
                MyOpenGL.drawTriangle(vertex1, vertex2, vertex3, 1.0, 0.2f);
            } else if (mouseOver) {
                MyOpenGL.drawTriangle(vertex1, vertex2, vertex3, 1.0, 0.8f);
            } else {
                MyOpenGL.drawTriangle(vertex1, vertex2, vertex3, 1.0, 1f);
            }
        }

        public void recenter(int[] center) {
            int halfSize = size / 2;
            if (leftOriented) {
                this.vertex1 = new int[]{center[0] - halfSize, center[1]};
                this.vertex2 = new int[]{center[0] + halfSize, center[1] + halfSize};
                this.vertex3 = new int[]{center[0] + halfSize, center[1] - halfSize};
            } else {
                this.vertex1 = new int[]{center[0] + halfSize, center[1]};
                this.vertex2 = new int[]{center[0] - halfSize, center[1] + halfSize};
                this.vertex3 = new int[]{center[0] - halfSize, center[1] - halfSize};
            }
        }
    }
}
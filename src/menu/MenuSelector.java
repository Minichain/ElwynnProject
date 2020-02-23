package menu;

import listeners.MyInputListener;
import main.MyOpenGL;
import main.TextRendering;
import utils.MathUtils;

public class MenuSelector extends MenuComponent {
    private Selector previousSelector;
    private Selector nextSelector;
    private int selectedValue;
    private SelectedResolution selectedResolution;

    private enum SelectedResolution {
        RESOLUTION_640_480 (0), RESOLUTION_1280_720 (1), RESOLUTION_1920_1080 (2);

        int resolutionValue;

        SelectedResolution(int resolutionValue) {
            this.resolutionValue = resolutionValue;
        }

        public int getResolutionValue() {
            return resolutionValue;
        }

        public SelectedResolution getResolution(int width, int height) {
            switch (height) {
                case 480:
                    return RESOLUTION_640_480;
                case 720:
                    return RESOLUTION_1280_720;
                case 1080:
                default:
                    return RESOLUTION_1920_1080;
            }
        }

        public String toString() {
            switch (this) {
                case RESOLUTION_640_480:
                    return "640x480";
                case RESOLUTION_1280_720:
                    return "1280x720";
                case RESOLUTION_1920_1080:
                default:
                    return "1920x1080";

            }
        }
    }

    public MenuSelector(String text) {
        setText(text);
        previousSelector = new Selector(new int[]{x + 20, y + height / 2}, 14, true);
        nextSelector = new Selector(new int[]{x + width - 20, y + height / 2}, 14, false);
        selectedResolution = SelectedResolution.RESOLUTION_1920_1080;
    }

    @Override
    public void update(int position, int gapBetweenComponents) {
        x = (int) Menu.getInstance().getCoordinates().x - width / 2;
        y = (int) Menu.getInstance().getCoordinates().y + (height + gapBetweenComponents) * position;
        previousSelector.recenter(new int[]{x + 20, y + height / 2});
        previousSelector.update();
        nextSelector.recenter(new int[]{x + width - 20, y + height / 2});
        nextSelector.update();
        setMouseOver(MathUtils.isMouseInsideRectangle(x, y, x + width, y + height));
        if (isMouseOver() && MyInputListener.leftMouseButtonPressed) {
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
        TextRendering.renderText(textX, textY, textInfo, scale);
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

        public void update() {
            mouseOver = MathUtils.isPointInsideTriangle(MyInputListener.getMouseCameraCoordinates(), vertex1, vertex2, vertex3);
            if (mouseOver && MyInputListener.leftMouseButtonPressed) {
                pressed = true;
            } else {
                if (pressed && mouseOver) {
                    if (leftOriented && selectedValue > 0) {
                        selectedValue--;
                    } else {
                        selectedValue++;
                    }
                    selectedValue = selectedValue % 3;
                    selectedResolution = SelectedResolution.values()[selectedValue];
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

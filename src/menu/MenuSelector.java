package menu;

import enums.Language;
import enums.Resolution;
import listeners.InputListenerManager;
import main.*;
import text.TextRendering;
import utils.MathUtils;

public class MenuSelector extends MenuComponent {
    private Selector previousSelector;
    private Selector nextSelector;
    private int selectedValue;

    private SelectorAction selectorAction;

    public enum SelectorAction {
        NONE, RESOLUTION, LANGUAGE
    }

    public MenuSelector(SelectorAction sa) {
        selectorAction = sa;
        previousSelector = new Selector(new int[]{x + 20, y + height / 2}, 18f, true);
        nextSelector = new Selector(new int[]{x + width - 20, y + height / 2}, 18f, false);
        switch (selectorAction) {
            case RESOLUTION:
                selectedValue = Resolution.getResolution(Parameters.getResolutionWidth(), Parameters.getResolutionHeight()).getResolutionValue();
                break;
            case LANGUAGE:
                selectedValue = Parameters.getLanguage().getValue();
                break;
            case NONE:
            default:
                break;
        }
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
        previousSelector.recenter(new int[]{this.x + 20, this.y + this.height / 2});
        nextSelector.recenter(new int[]{this.x + this.width - 20, y + this.height / 2});

        setMouseOver(MathUtils.isMouseInsideRectangle(this.x, this.y, this.x + this.width, this.y + this.height));
        if (isMouseOver() && InputListenerManager.leftMouseButtonPressed) {
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) {
                // Do nothing
            }
            setPressed(false);
        }

        switch (selectorAction) {
            case LANGUAGE:
                setText(Strings.getString(Strings.LANGUAGE, Language.values()[selectedValue].toString()));
                break;
            case RESOLUTION:
                setText(Strings.getString(Strings.RESOLUTION, Resolution.values()[selectedValue].toString()));
                break;
            case NONE:
            default:
                break;
        }

        previousSelector.update();
        nextSelector.update();
    }

    @Override
    public void renderBackground() {
        if (isPressed()) {
            OpenGLManager.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else if (isMouseOver()) {
            OpenGLManager.drawRectangle(x, y, width, height, 0.65, 0.4f);
        } else {
            OpenGLManager.drawRectangle(x, y, width, height, 0.5, 0.6f);
        }
        previousSelector.render();
        nextSelector.render();
    }

    @Override
    public void renderInfo() {
        float scale = 2 * Parameters.getResolutionFactor();
        int textX = (int) (x + (width / 2f) - (TextRendering.CHARACTER_WIDTH * scale * getText().length() / 2f));
        int textY = (int) (y + (height / 2f) - (TextRendering.CHARACTER_HEIGHT * scale / 2f));
        TextRendering.renderText(textX, textY, getText(), scale, true);
    }

    public class Selector {
        Coordinates vertex1;
        Coordinates vertex2;
        Coordinates vertex3;
        boolean pressed;
        boolean mouseOver;
        float size;
        boolean leftOriented;

        public Selector(int[] center, float size, boolean leftOriented) {
            this.leftOriented = leftOriented;
            this.size = size;
            recenter(center);
        }

        public void update() {
            mouseOver = MathUtils.isPointInsideTriangle(InputListenerManager.getMouseCameraCoordinates(), vertex1, vertex2, vertex3);
            if (mouseOver && InputListenerManager.leftMouseButtonPressed) {
                pressed = true;
            } else {
                if (pressed && mouseOver) {
                    if (leftOriented) {
                        if (selectedValue > 0) {
                            selectedValue--;
                        } else {
                            switch (selectorAction) {
                                case RESOLUTION:
                                    selectedValue = Resolution.values().length - 1;
                                    break;
                                case LANGUAGE:
                                    selectedValue = Language.values().length - 1;
                                    break;
                                case NONE:
                                default:
                                    break;
                            }
                        }
                    } else {
                        selectedValue++;
                    }
                    switch (selectorAction) {
                        case RESOLUTION:
                            selectedValue = selectedValue % Resolution.values().length;
                            Parameters.setResolution(Resolution.values()[selectedValue]);
                            break;
                        case LANGUAGE:
                            selectedValue = selectedValue % Language.values().length;
                            Parameters.setLanguage(Language.values()[selectedValue]);
                            break;
                        case NONE:
                        default:
                            break;
                    }
                    Window.setWindowSize(Parameters.getResolutionWidth(), Parameters.getResolutionHeight());
                }
                pressed = false;
            }
        }

        public void render() {
            if (pressed) {
                OpenGLManager.drawTriangle(vertex1, vertex2, vertex3, 1.0, 0.2f);
            } else if (mouseOver) {
                OpenGLManager.drawTriangle(vertex1, vertex2, vertex3, 1.0, 0.8f);
            } else {
                OpenGLManager.drawTriangle(vertex1, vertex2, vertex3, 1.0, 1f);
            }
        }

        public void recenter(int[] center) {
            int halfSize = (int) ((size / 2f) * Parameters.getResolutionFactor());
            if (leftOriented) {
                this.vertex1 = new Coordinates(center[0] - halfSize, center[1]);
                this.vertex2 = new Coordinates(center[0] + halfSize, center[1] + halfSize);
                this.vertex3 = new Coordinates(center[0] + halfSize, center[1] - halfSize);
            } else {
                this.vertex1 = new Coordinates(center[0] - halfSize, center[1] - halfSize);
                this.vertex2 = new Coordinates(center[0] - halfSize, center[1] + halfSize);
                this.vertex3 = new Coordinates(center[0] + halfSize, center[1]);
            }
        }
    }
}

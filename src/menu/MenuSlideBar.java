package menu;

import listeners.InputListenerManager;
import main.OpenGLManager;
import main.Parameters;
import text.TextRendering;
import utils.MathUtils;

public class MenuSlideBar extends MenuComponent {
    private float progress;    // From 0 to 1
    private SliderAction sliderAction;

    public enum SliderAction {
        NONE, MUSIC_SOUND_LEVEL, EFFECT_SOUND_LEVEL, AMBIENCE_SOUND_LEVEL
    }

    public MenuSlideBar(String text, SliderAction sliderAction) {
        setText(text);
        switch (sliderAction) {
            case EFFECT_SOUND_LEVEL:
                progress = Parameters.getEffectSoundLevel();
                break;
            case MUSIC_SOUND_LEVEL:
                progress = Parameters.getMusicSoundLevel();
                break;
            case AMBIENCE_SOUND_LEVEL:
                progress = Parameters.getAmbienceSoundLevel();
                break;
            case NONE:
            default:
                progress = 0f;
                break;
        }
        this.sliderAction = sliderAction;
        this.width = (int) (500f * Parameters.getResolutionFactor());
        this.height = (int) (45f * Parameters.getResolutionFactor());
        this.x = 0;
        this.y = 0;
    }

    @Override
    public void update(int position, int gapBetweenComponents) {
        this.width = (int) (500f * Parameters.getResolutionFactor());
        this.height = (int) (45f * Parameters.getResolutionFactor());
        this.x = (int) Menu.getInstance().getCoordinates().x - width / 2;
        this.y = (int) Menu.getInstance().getCoordinates().y + (height + gapBetweenComponents) * position;

        setMouseOver(MathUtils.isMouseInsideRectangle(x, y, x + width, y + height));
        if (isMouseOver() && InputListenerManager.leftMouseButtonPressed) {
            progress = (float) (InputListenerManager.getMouseCameraCoordinates().x - x) / (float) width;
            performAction(sliderAction);
            setPressed(true);
        } else {
            if (isPressed() && isMouseOver()) {
                // Do nothing
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
        OpenGLManager.drawRectangle(x, y, width * (float) progress, height, 0.8, 0.8f);
    }

    @Override
    public void renderInfo() {
        String textInfo = getText() + " (" + (int) (progress * 100) + "%)";
        float scale = 2 * Parameters.getResolutionFactor();
        int textX = (int) (x + (width / 2f) - (TextRendering.CHARACTER_WIDTH * scale * textInfo.length() / 2f));
        int textY = (int) (y + (height / 2f) - (TextRendering.CHARACTER_HEIGHT * scale / 2f));
        TextRendering.renderText(textX, textY, textInfo, scale, true);
    }

    private void performAction(SliderAction buttonAction) {
        switch (buttonAction) {
            case EFFECT_SOUND_LEVEL:
                Parameters.setEffectSoundLevel((float) progress);
                break;
            case MUSIC_SOUND_LEVEL:
                Parameters.setMusicSoundLevel((float) progress);
                break;
            case AMBIENCE_SOUND_LEVEL:
            case NONE:
            default:
                break;
        }
    }
}

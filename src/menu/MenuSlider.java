package menu;

import listeners.InputListenerManager;
import main.GameTime;
import main.OpenGLManager;
import main.Parameters;
import text.TextRendering;
import utils.MathUtils;

public class MenuSlider extends MenuComponent {
    private float progress;    // From 0 to 1
    private SliderAction sliderAction;

    public enum SliderAction {
        NONE, MUSIC_SOUND_LEVEL, EFFECT_SOUND_LEVEL, AMBIENCE_SOUND_LEVEL,
        GAME_TIME_SPEED, SPAWN_RATE, RENDER_DISTANCE, UPDATE_DISTANCE
    }

    public MenuSlider(String text, SliderAction sliderAction) {
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
            case GAME_TIME_SPEED:
                progress = GameTime.getGameTimeRealTimeFactor() / 6000f;
                break;
            case SPAWN_RATE:
                progress = Parameters.getSpawnRate() / 10f;
                break;
            case RENDER_DISTANCE:
                progress = Parameters.getRenderDistance() / 10f;
                break;
            case UPDATE_DISTANCE:
                progress = Parameters.getUpdateDistance() / 10000f;
                break;
            case NONE:
            default:
                progress = 0f;
                break;
        }
        this.sliderAction = sliderAction;
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

        setMouseOver(MathUtils.isMouseInsideRectangle(this.x, this.y, this.x + this.width, this.y + this.height));
        if (isMouseOver() && InputListenerManager.leftMouseButtonPressed) {
            progress = (float) (InputListenerManager.getMouseCameraCoordinates().x - this.x) / (float) this.width;
            setPressed(true);
        } else {
            if (isPressed()) {
                performAction(sliderAction);
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
        OpenGLManager.drawRectangle(x, y, width * progress, height, 0.8, 0.8f);
    }

    @Override
    public void renderInfo() {
        String textInfo = getText();
        switch (sliderAction) {
            case SPAWN_RATE:
                textInfo += " (" + (int) (this.progress * 10) + "x)";
                break;
            case GAME_TIME_SPEED:
                textInfo += " (" + (int) (this.progress * 6000f) + "x)";
                break;
            case EFFECT_SOUND_LEVEL:
            case MUSIC_SOUND_LEVEL:
            case AMBIENCE_SOUND_LEVEL:
            case NONE:
            default:
                textInfo += " (" + (int) (progress * 100) + "%)";
                break;
        }
        float scale = 2 * Parameters.getResolutionFactor();
        int textX = (int) (x + (width / 2f) - (TextRendering.CHARACTER_WIDTH * scale * textInfo.length() / 2f));
        int textY = (int) (y + (height / 2f) - (TextRendering.CHARACTER_HEIGHT * scale / 2f));
        TextRendering.renderText(textX, textY, textInfo, scale, true);
    }

    private void performAction(SliderAction buttonAction) {
        switch (buttonAction) {
            case EFFECT_SOUND_LEVEL:
                Parameters.setEffectSoundLevel(this.progress);
                break;
            case MUSIC_SOUND_LEVEL:
                Parameters.setMusicSoundLevel(this.progress);
                break;
            case AMBIENCE_SOUND_LEVEL:
                Parameters.setAmbienceSoundLevel(this.progress);
                break;
            case GAME_TIME_SPEED:
                GameTime.setGameTimeRealTimeFactor(this.progress * 6000f);
                break;
            case SPAWN_RATE:
                Parameters.setSpawnRate(this.progress * 10f);
                break;
            case RENDER_DISTANCE:
                Parameters.setRenderDistance(this.progress * 10f);
                break;
            case UPDATE_DISTANCE:
                Parameters.setUpdateDistance(this.progress * 10000f);
                break;
            case NONE:
            default:
                break;
        }
    }
}

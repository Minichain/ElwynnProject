package listeners;

import com.badlogic.gdx.InputProcessor;

public class MyInputListener implements InputProcessor {
    private static MyInputListener instance = null;
    private int mousePositionX;
    private int mousePositionY;
    public int mouseWheelPosition;

    private MyInputListener() {
    }

    public static MyInputListener getInstance() {
        if (instance == null) {
            instance = new MyInputListener();
        }
        return instance;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        mouseWheelPosition += i;
        return false;
    }
}

package listeners;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import entities.Camera;
import entities.Character;
import entities.Scene;
import main.Parameters;

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
        switch (i) {
            case Input.Keys.F1:
                Parameters.getInstance().setDebugMode(!Parameters.getInstance().isDebugMode());
                break;
            case Input.Keys.F2:
                Character.getInstance().resetCharacter();
                break;
            default:
                break;
        }
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
    public boolean touchDown(int x, int y, int i2, int i3) {
        Vector3 mouseCoordinates = new Vector3(x, y, 0);
        Vector3 mouseGlobalCoordinates = Camera.getInstance().unproject(mouseCoordinates);
        int xGlobal = (int) Math.floor(mouseGlobalCoordinates.x / 16);
        int yGlobal = (int) Math.floor(mouseGlobalCoordinates.y / 16);
        Scene.getInstance().setTile(xGlobal, yGlobal, (byte) 3);
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

package listeners;

import entities.Scene;
import main.Parameters;
import entities.Character;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter {
    private static MyKeyListener instance = null;

    private boolean wKeyPressed;
    private boolean aKeyPressed;
    private boolean sKeyPressed;
    private boolean dKeyPressed;
    private boolean spaceKeyPressed;

    private MyKeyListener() {
    }

    public static MyKeyListener getInstance() {
        if (instance == null) {
            instance = new MyKeyListener();
        }
        return instance;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("keyPressed = " + KeyEvent.getKeyText(e.getKeyCode()));

        switch (KeyEvent.getKeyText(e.getKeyCode())) {
            case "W":
                wKeyPressed = true;
                break;
            case "A":
                aKeyPressed = true;
                break;
            case "S":
                sKeyPressed = true;
                break;
            case "D":
                dKeyPressed = true;
                break;
            case "Space":
                Character character = Character.getInstance();
                if (!spaceKeyPressed && character.getCharacterStatus() != Character.Status.JUMPING) {
                    character.performJump();
                }
                spaceKeyPressed = true;
                break;
            case "F1":  //Debug Mode
                if (Parameters.getInstance().isDebugMode()) {
                    Parameters.getInstance().setDebugMode(false);
                } else {
                    Parameters.getInstance().setDebugMode(true);
                }
                break;
            case "F2":  //Reset
                Scene.getInstance().initEntities();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
//        System.out.println("keyReleased = " + KeyEvent.getKeyText(e.getKeyCode()));

        switch (KeyEvent.getKeyText(e.getKeyCode())) {
            case "W":
                wKeyPressed = false;
                break;
            case "A":
                aKeyPressed = false;
                break;
            case "S":
                sKeyPressed = false;
                break;
            case "D":
                dKeyPressed = false;
                break;
            case "Space":
                spaceKeyPressed = false;
                break;
            case "F1":
            case "F2":
                break;
        }
    }

    public boolean iswKeyPressed() {
        return wKeyPressed;
    }

    public boolean isaKeyPressed() {
        return aKeyPressed;
    }

    public boolean issKeyPressed() {
        return sKeyPressed;
    }

    public boolean isdKeyPressed() {
        return dKeyPressed;
    }
}

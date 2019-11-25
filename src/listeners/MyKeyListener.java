package listeners;

public class MyKeyListener {
    private static MyKeyListener instance = null;

    private MyKeyListener() {
    }

    public static MyKeyListener getInstance() {
        if (instance == null) {
            instance = new MyKeyListener();
        }
        return instance;
    }
/*
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
    }*/
}

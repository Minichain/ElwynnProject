package main;

import entities.Character;

public class Main {

    public static void main(String[] args) {
        long timeElapsed;
        long lastUpdateTime = 0;
        long currentTime;

        ElwynnGraphics elwynnGraphics = new ElwynnGraphics();
        elwynnGraphics.createJFrame();
        elwynnGraphics.createJPanel();
        elwynnGraphics.addJPanelToJFrame();

        GameStatus.getInstance().setGameRunning(true);
        System.out.println("Game initiated and running");

        //main.Main game loop
        while (GameStatus.getInstance().isGameRunning()) {
            try {
                //Compute the time elapsed since the last frame
                currentTime = System.currentTimeMillis();
                timeElapsed = currentTime - lastUpdateTime;

                Character.getInstance().updateCharacter(timeElapsed);
                elwynnGraphics.updateFrame(timeElapsed);
                lastUpdateTime = currentTime;

                //Wait time until processing next frame. FPS locked.
                Thread.sleep(1000 / Parameters.getInstance().getFramesPerSecond());
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(1);
            }
        }

        System.out.println("Game stopped running");
        System.exit(1);
    }
}
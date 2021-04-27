package board;

import entities.Player;
import entities.SpriteManager;
import main.Parameters;

public class FretBoardNote {
    private boolean dead = false;
    private int targetNote;
    private double pathTraveled;

    public FretBoardNote(int targetNote) {
        this.targetNote = targetNote;
        this.pathTraveled = 0;
    }

    public void update(long timeElapsed) {
        pathTraveled += timeElapsed * 0.001;
        dead = pathTraveled >= 2.0;
    }

    public void render(float transparency) {
        int x = (int) FretBoard.getInstance().getTargetNotes()[targetNote].x;
        int y = (int) (FretBoard.getInstance().getTargetNotes()[targetNote].y - 150.0 + 150.0 * pathTraveled);
        transparency = (1f - Math.abs((1f - (float) pathTraveled))) * transparency;
        SpriteManager.getInstance().FRET_BOARD.draw(x, y, 0, 2,
                transparency, 4f * Parameters.getHeightResolutionFactor(), Player.getInstance().getMusicalMode().getColor(), true, true);
    }

    public boolean isDead() {
        return dead;
    }

    public double getPathTraveled() {
        return pathTraveled;
    }

    public int getTargetNote() {
        return targetNote;
    }
}

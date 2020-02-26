package audio;

public class Sound {
    private int buffer;
    private int index;

    public Sound(int buffer, int index) {
        this.buffer = buffer;
        this.index = index;
    }

    public int getBuffer() {
        return buffer;
    }

    public int getIndex() {
        return index;
    }
}

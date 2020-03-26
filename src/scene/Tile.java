package scene;

public class Tile {
    private boolean collidable;
    private byte[] layerValue;
    private static int numOfLayers = 3;

    public Tile() {
        collidable = false;
        layerValue = new byte[numOfLayers];
        for (int i = 0; i < numOfLayers; i++) {
            layerValue[i] = (byte) 0;
        }
    }

    public byte getLayerValue(int layer) {
        return layerValue[layer];
    }

    public void setLayerValue(int layer, byte value) {
        layerValue[layer] = value;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public void changeCollisionBehaviour() {
        this.collidable = !this.collidable;
    }

    public static int getNumOfLayers() {
        return numOfLayers;
    }
}

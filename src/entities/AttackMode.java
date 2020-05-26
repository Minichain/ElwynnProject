package entities;

public enum AttackMode {
    MODE_01, MODE_02, MODE_03;

    public final static int numOfAttackModes = 3;

    public float[] getColor() {
        switch (this) {
            case MODE_01:
                return new float[]{194f / 255f, 72f / 255f, 72f / 255f};    //Soft Red
            case MODE_02:
                return new float[]{194f / 255f, 164f / 255f, 72f / 255f};   //Yellow
            case MODE_03:
            default:
                return new float[]{194f / 255f, 72f / 255f, 193f / 255f};   //Purple
        }
    }
}

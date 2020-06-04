package menu;

public abstract class MenuComponent {
    private String text;
    private boolean mouseOver = false;
    private boolean pressed = false;

    public int x;
    public int y;
    public int width;
    public int height;

    public abstract void update(int position, int gapBetweenComponents);

    public abstract void renderBackground();

    public abstract void renderInfo();

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

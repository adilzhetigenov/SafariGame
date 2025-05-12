package Model.Entity;


import java.awt.image.BufferedImage;

public abstract class Entity {
    private double x;
    private double y;
    private int speed;
    private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    private String direction;
    private int spriteCounter = 0;
    private int spriteNum = 1;
    protected Rectangle solidArea;

    private boolean colissionON = false;
    private boolean removed = false;

    public Entity(double x, double y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        direction = "down";
        solidArea = new Rectangle();
    }

    public int getSpriteCounter() {
        return spriteCounter;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void increaseSpriteCounter() {
        spriteCounter++;
    }

    public void setSpriteNum(int val) {
        spriteNum = val;
    }

    public void setSpriteCount(int val) {
        spriteCounter = val;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setUp1(BufferedImage up1) {
        this.up1 = up1;
    }

    public void setUp2(BufferedImage up2) {
        this.up2 = up2;
    }

    public void setDown1(BufferedImage down1) {
        this.down1 = down1;
    }

    public void setDown2(BufferedImage down2) {
        this.down2 = down2;
    }

    public void setLeft1(BufferedImage left1) {
        this.left1 = left1;
    }

    public void setLeft2(BufferedImage left2) {
        this.left2 = left2;
    }

    public void setRight1(BufferedImage right1) {
        this.right1 = right1;
    }

    public void setRight2(BufferedImage right2) {
        this.right2 = right2;
    }

    public BufferedImage getUp1() {
        return up1;
    }

    public BufferedImage getUp2() {
        return up2;
    }

    public BufferedImage getDown1() {
        return down1;
    }

    public BufferedImage getDown2() {
        return down2;
    }

    public BufferedImage getLeft1() {
        return left1;
    }

    public BufferedImage getLeft2() {
        return left2;
    }

    public BufferedImage getRight1() {
        return right1;
    }

    public BufferedImage getRight2() {
        return right2;
    }

    public void setDirectionUp() {
        this.direction = "up";
    }

    public void setDirectionRight() {
        this.direction = "right";
    }

    public void setDirectionDown() {
        this.direction = "down";
    }

    public void setDirectionLeft() {
        this.direction = "left";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void changeX(double dx) {
        x += dx;
    }

    public void changeY(double dy) {
        y += dy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setColission(boolean col) {
        colissionON = col;
    }

    public boolean getColission() {
        return colissionON;
    }

    public void setSolidAreaX(int x) {
        solidArea.setX(x);
    }
    public void setSolidAreaY(int y) {
        solidArea.setY(y);
    }
    public int getSolidAreaX() {
        return (int) solidArea.getX();
    }

    public int getSolidAreaY() {
        return (int) solidArea.getY();
    }

    public int getSoliAreaWidth() {
        return (int) solidArea.getWidth();
    }

    public boolean isColissionON() {
        return colissionON;
    }
    public int getSoliAreaHeight() {
        return (int) solidArea.getHeight();
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}

package com.company;

public class Character extends GameObject {
    protected Boolean canMoveLeft = true;
    protected Boolean canMoveRight = true;
    protected Boolean canMoveUp = true;
    protected Boolean canMoveDown = false;

    public Character(int xIN, int yIN, int wIN, int hIN){
        super(xIN, yIN, wIN, hIN);
    }

    public void moveLeft(int d){
        if (canMoveDown) {
            moveDown(5);
        }
        if (x >= 0 && canMoveLeft){
            x = x-d;
        }
    }
    public void moveRight(int d){
        if (canMoveDown) {
            moveDown(5);
        }
        if ((x+d) <= (600-w) && canMoveRight){
            x = x+d;
        }
    }

    public void moveUp(int d) {
        if ((y - d) >= 0 && canMoveUp) {
            y = y - d;
        }
    }
    public void moveDown(int d) {
        if ((y + d) <= (600 - h) && canMoveDown) {
            y = y + d;
        }
    }

    public void setCanMoveLeft(Boolean yn){
        this.canMoveLeft = yn;
    }
    public void setCanMoveRight(Boolean yn){
        this.canMoveRight = yn;
    }
    public void setCanMoveUp(Boolean yn){
        this.canMoveUp = yn;
    }
    public void setCanMoveDown(Boolean yn){this.canMoveDown = yn;}

    public Boolean getCanMoveLeft(){return canMoveLeft;}

    public Boolean getCanMoveRight() {
        return canMoveRight;
    }

    public void bounce(){
        //Here as a filler to allow Villains to bounce without needing their own collision detection
        //DO NOT DELETE UNLESS REALLY SURE!!
    }


}

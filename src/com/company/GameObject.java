package com.company;

public class GameObject {
    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected boolean hidden;

    public GameObject(int xIN, int yIN, int wIN, int hIN){
        this.x = xIN;
        this.y = yIN;
        this.w = wIN;
        this.h = hIN;
        hidden = false;
    }

    public void setTopY(int y) {
        this.y = y;
    }

    public void setLeftX(int x) {
        this.x = x;
    }

    public int getTopY(){
        return(y);
    }

    public int getBottomY(){
        return(y+h);
    }

    public int getLeftX(){
        return(x);
    }

    public int getRightX(){
        return(x+w);
    }

    public int getH() {
        return h;
    }

    public void setH(int h){this.h = h;}

    public int getW() {
        return w;
    }

    public void setW(int w){this.w = w;}

    public void setHidden(Boolean hide){
        hidden = hide;
    }

    public Boolean isHidden(){
        return hidden;
    }

    public void powerup(){
        //Filler for pipe and brick using same collision detection
    }
    public Boolean isNotCollected(){
        //Filler for box collection
        return true;
    }
}

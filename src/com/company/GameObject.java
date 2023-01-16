package com.company;

public class GameObject {
    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected boolean hidden;
    protected String image; //Just added, could cause problems or make easier who knows
    protected int totalJumped;

    public GameObject(int xIN, int yIN, int wIN, int hIN){
        this.x = xIN;
        this.y = yIN;
        this.w = wIN;
        this.h = hIN;
        hidden = false;
        totalJumped = 0;
    }

    public GameObject(int xIN, int yIN, int wIN, int hIN, String image){
        this.x = xIN;
        this.y = yIN;
        this.w = wIN;
        this.h = hIN;
        this.image = image;
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

    public Boolean powerup(){
        //Filler for pipe and brick using same collision detection
        return false;
    }
    public Boolean isNotCollected(){
        //Filler for box collection
        return true;
    }
    public String contains(){
        return "none";
    }

    public void setImage(String imageEntered){image = imageEntered;}
    public String getImage(){
        return image;
    }

    public Boolean jump(){
        //Returns whether complete
        if(totalJumped < 20){
            y = y-5;
            totalJumped++;
            return false;
        } else if (totalJumped == 41){
                hidden = true;
                return true;
        }else{
            y = y+5;
            //y = y + (20-totalJumped);
            totalJumped++;
            return false;
        }
    }
}


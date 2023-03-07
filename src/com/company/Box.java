package com.company;

public class Box extends GameObject {
    protected String contain;
    protected boolean collected;
    protected int flicker;
    protected boolean jumping;
    protected boolean animating;

    public Box(int xIN, int yIN, String inside) {
        super(xIN, yIN, 50, 50);
        contain = inside;
        collected = false;
        flicker = 0;
        totalJumped = 0;
        jumping = false;
    }

    public String image(){
        if (contain.equals("block")){
            return ("src/resources/items/block.png");
        } else if (contain.equals("none") && !hidden) {
            return ("src/resources/items/brick.png");
        } else if(!contain.equals("none") && collected){
            return ("src/resources/items/box.png");
        }else{
            if (flicker%40 > 20) {
                flicker++;
                return ("src/resources/items/box.png");
            } else {
                flicker++;
                return ("src/resources/items/questionBox.png");
            }
        }
    }

    public Boolean isAnimating(){
        return animating;
    }

    public void powerup(){
        if(contain.equals("coin")){
            contain = "empty";
        }
        if(!contain.equals("block")) {
            if (totalJumped < 10) {
                y = y - 2;
                totalJumped++;
                animating = true;
            } else if (totalJumped == 21) {
                jumping = false;
                collected = true;
                if (contain.equals("none")) {
                    hidden = true;
                }
                animating = false;
            } else {
                y = y + 2;
                //y = y + (20-totalJumped);
                totalJumped++;
                animating = true;
            }
        } else{
            animating = false;
        }
    }

    public Boolean isNotCollected(){
        return (!collected);
    }

    public String contains(){
        return contain;
    }
}
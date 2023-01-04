package com.company;

public class Box extends GameObject {
    protected String contain;
    protected boolean collected;
    protected int flicker;
    protected int totalJumped;
    protected boolean jumping;

    public Box(int xIN, int yIN, String inside) {
        super(xIN, yIN, 50, 50);
        contain = inside;
        collected = false;
        flicker = 0;
        totalJumped = 0;
        jumping = false;
    }

    public String image(){
        if (contain.equals("none") && !hidden) {
            return ("src/resources/others/brick.png");
        } else if(!contain.equals("none") && collected){
            return ("src/resources/others/box.png");
        }else{
            if (flicker%40 > 20) {
                flicker++;
                return ("src/resources/others/box.png");
            } else {
                flicker++;
                return ("src/resources/others/questionBox.png");
            }
        }
    }

    public Boolean powerup(){
        if(contain.equals("coin")){
            System.out.println("src/resources/others/coin.png");
        }
        if(totalJumped < 10){
            y = y-2;
            totalJumped++;
            return true;
        } else if (totalJumped == 21){
            jumping = false;
            collected = true;
            if(contain.equals("none")){
                hidden = true;
            }
            return false;
        }else{
            y = y+2;
            //y = y + (20-totalJumped);
            totalJumped++;
            return true;
        }

    }

    public Boolean isNotCollected(){
        return (!collected);
    }
}

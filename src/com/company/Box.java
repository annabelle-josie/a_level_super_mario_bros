package com.company;

public class Box extends GameObject {
    protected String contain;
    protected boolean collected;

    public Box(int xIN, int yIN, String inside) {
        super(xIN, yIN, 50, 50);
        contain = inside;
        collected = false;
    }

    public String image(){
        if (contain.equals("none")) {
            return ("src/resources/others/brick.png");
        } else{
            return("src/resources/others/questionBox.png");
        }
    }

    public void powerup(){
        collected = true;
        if(contain.equals("none")){
            System.out.println("Empty");
        } else {
            System.out.println("Powerup");
        }
    }

    public Boolean isNotCollected(){
        return (!collected);
    }
}

package com.company;

public class Box extends GameObject {
    String contain;

    public Box(int xIN, int yIN, String inside) {
        super(xIN, yIN, 50, 50);
        contain = inside;
    }

    public String image(){
        if (contain.equals("none")) {
            return ("src/resources/others/brick.png");
        } else{
            return("src/resources/others/questionBox.png");
        }
    }
}

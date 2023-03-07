package com.company;

public class Villain extends Character{
    private String theImage;
    private int number;
    private String[] goombaSet = {"src/resources/characters/goomba1.png", "src/resources/characters/goomba2.png"};
    private int xChange = -1;

    public Villain (int xIN, int yIN, int wIN, int hIN){
        super(xIN, yIN, wIN, hIN);
        theImage = "src/resources/characters/goomba2.png";
    }
    public Villain (String image, int xIN, int yIN, int wIN, int hIN){
        super(xIN, yIN, wIN, hIN);
        theImage = image;
    }

    public String image(){
        return theImage;
    }

    public void animate(){
        number++;
        number = number % 20;
        theImage = goombaSet[number/10];
        x = x + xChange;
    }

    public void bounce(){
        xChange = xChange * -1;
    }

    public void setChange(int value){
        xChange = value;
    }

    public void spot(){
        animate();
    }
}


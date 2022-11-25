package com.company;

public class Villain extends Character{
    private String theImage;
    private int number;
    private String[] goombaSet = {"src/resources/others/goomba1.png", "src/resources/others/goomba2.png"};
    private int xChange = -1;
    private Boolean isDead;

    public Villain (int xIN, int yIN, int wIN, int hIN){
        super(xIN, yIN, wIN, hIN);
        theImage = "src/resources/others/goomba2.png";
        isDead = false;
    }

    public String image(){
        return theImage;
    }

    public void animate(){
        number++;
        number = number % 20;
        theImage = goombaSet[number/10];
        x= x + xChange;
    }

    public void bounce(){
        xChange = xChange * -1;
    }

    public void setDead(Boolean dead){
        isDead = dead;
    }
    public Boolean isDead(){
        return isDead;
    }

}

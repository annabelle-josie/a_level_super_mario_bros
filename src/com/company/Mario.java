
package com.company;

public class Mario extends Character {
    private String theImage;
    private int number;
    private String direction = "right";
    private String[] leftMarioSet = {"src/resources/left/smallW1.png", "src/resources/left/smallW2.png", "src/resources/left/smallW3.png", "src/resources/left/smallStand.png"};
    private String[] rightMarioSet = {"src/resources/right/smallW1.png", "src/resources/right/smallW2.png", "src/resources/right/smallW3.png", "src/resources/right/smallStand.png"};
    private int jumpCount;

    public Mario (String image, int xIN, int yIN, int wIN, int hIN){
        super(xIN, yIN, wIN, hIN);
        theImage = image;
    }

    public void setJumpCount(int nums){
        jumpCount = nums;
    }
    public int getJumpCount() {
        return jumpCount;
    }
    public String getDirection(){
        return direction;
    }

    public void animate(){
        number++;
        number = number % 40;
        if (direction.equals("left")) {
            theImage = leftMarioSet[number/10];
        } else if (direction.equals("right")){
            theImage = rightMarioSet[number/10];
        }
    }

    public void setImage(String img){
        theImage = img;
    }

    public String image(){
        return theImage;
    }

    public void moveLeft(int d){
        if (canMoveLeft){
            direction = "left";
            animate();
        } else {
            theImage = "src/resources/left/smallStand.png";
        }
    }
    public void moveRight(int d){
        if (canMoveRight){
            direction = "right";
            animate();
        } else {
            theImage = "src/resources/right/smallStand.png";
        }
    }

    public void moveUp(int d){
        if ((y-d) >= 0 && canMoveUp){
            y = y-d;
            if (direction.equals("left")) {
                theImage = "src/resources/left/smallJump.png";
            } else if (direction.equals("right")){
                theImage = "src/resources/right/smallJump.png";
            }
        }

    }
    public void moveDown(int d){
        if ((y+d) <= (600-h) && canMoveDown){
            y = y+d;
            if (direction.equals("left")) {
                theImage = "src/resources/left/smallJump.png";
            } else if (direction.equals("right")){
                theImage = "src/resources/right/smallJump.png";
            }
        }
    }

    public Boolean jump(Boolean up, int height){
        if (up && jumpCount <= height){
            moveUp((20 - jumpCount));
            jumpCount++;
            return true;
        } else if (!up && canMoveDown){
            moveDown((height - jumpCount));
            if (jumpCount > 0) {
                jumpCount--;
            }
            return true;
        } else {
            return false;
        }
    }

    public void fall(){
        moveDown(20);
    }

    public void die(){
        if ((y+5) <= (600-h)){
            y = y+5;
            theImage = "src/resources/dead.png";
        } else{
            y = y+(600-h);
        }
    }

    public void spot(){
        direction = "right";
        animate();
    }

    public void resetValues(int xIn, int yIn, int wIn, int hIn){
        x = xIn;
        y = yIn;
        w = wIn;
        h = hIn;
    }
}



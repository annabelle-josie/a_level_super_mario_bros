/*
Notes for next time
- When falling off a pipe onto a Goomba, Mario dies. In the original game this does not happen, instead he kills them.
The current algorithm uses jump functions to determine whether the Goomba should be killed. This is not going to work long term
- Mario also (still) does not fall off the ground if there is none below him. This is because it checks only the Y axis not the X
 */

package com.company;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import javax.swing.*;


public class Frame extends JFrame {
    Frame.PaintSurface canvas = new Frame.PaintSurface();
    Timer t;
    /*Key Listeners*/
    private Frame.KeyLis listener;
    static Boolean lkd = false;
    static Boolean rkd = false;
    static Boolean dkd = false;

    /*To change the screen*/
    Boolean gameOver = false;
    String screen = "level1";

    /*Jumping Variables*/
    Boolean jumping = false;
    Boolean up = false; //Is moving up

    /*Things*/
    Mario mario;

    ArrayList<Character> characterArray = new ArrayList<>();

    ArrayList<Villain>  villainArray = new ArrayList<>();
    Villain goombaGary;
    Villain goombaBab;
    Villain goombaCarl;
    int[] goombaPlacement;

    //GameObject[][] square; //Ask Mr Watts, can you have a 2D ArrayList (if so, how??)
    ArrayList<ArrayList<GameObject>> square = new ArrayList<>();

    ArrayList<GameObject> pipeArray = new ArrayList<>();
    GameObject pipe;
    GameObject pipe2;
    GameObject pipe3;
    GameObject[] gameObjects;

    public static void main(String[] args) {
        new Frame();
    }

    public Frame() {
        this.setSize(600, 627); //627 to make up for bar at top
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Kills code on close
        this.setTitle("Mario");
        this.add(this.canvas); //Add the painted area
        this.setVisible(true);

        /*Timer*/
        this.t = new Timer(10, (e) -> this.tick()); //Runs variable tick - collisions tick, move, repaint
        this.t.start(); //Starts timer

        /*Key Listeners*/
        this.listener = new Frame.KeyLis();
        this.addKeyListener(this.listener);
        resetL1();
    }

    public void resetL1(){
        //In future these values could be read from a text file and a new level could be added
        goombaPlacement = new int[6];
        /*Instantiating variables*/
        mario = new Mario("src/resources/right/SmallStand.png", 100, 435, 65, 65);

        //villainArray = new Villain[2];
        goombaGary = new Villain(350, 435, 65, 65);
        goombaBab = new Villain(750, 435, 65, 65);
        goombaCarl = new Villain(815, 435, 65, 65);

        //square = new GameObject[2][41];


        /*Make array of squares*/
        for (int c = 0; c < 2; c++) {
            square.add(new ArrayList<>());
            for (int r = 0; r < 40; r++) {
                //square[c][r] = new GameObject(r * 50, 550 - (c * 50), 50, 50);
                square.get(c).add(r, new GameObject(r * 50, 550 - (c * 50), 50, 50));
            }
        }

        pipe = new GameObject(450, 400, 75, 100);
        pipe2 = new GameObject(900, 375, 75, 125);
        pipe3 = new GameObject(1200, 350, 75, 150);

        /*Arrays*/
        villainArray.clear();
        villainArray.add(goombaGary);
        villainArray.add(goombaBab);
        villainArray.add(goombaCarl);

        characterArray.clear();
        characterArray.add(mario);
        characterArray.add(goombaGary);
        characterArray.add(goombaBab);
        characterArray.add(goombaCarl);

        pipeArray.clear();
        pipeArray.add(pipe);
        pipeArray.add(pipe2);
        pipeArray.add(pipe3);

    }


    public void tick() {
        if (gameOver) { //Makes mario fall off-screen, then switch the screen to --GAME OVER--
            mario.die(); //Mario falls off-screen
            if (mario.getBottomY() >= 600) { //When Mario reaches the bottom
                screen = "gameOver"; //Screen switches
            }
            this.canvas.repaint(); //Repaints Canvas
        } else {
            collisionDetection(); //Check for all collisions and correct them
            /*Villain Animate*/
            for (Villain villain : villainArray) {
                villain.animate();
            }
            if (lkd) { //Move left
                if (mario.canMoveLeft) {
                    mario.moveLeft(5);
                    //Turn this into an array of arrays and go through all changing x

                    /*Villain Left*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() + 5);
                    }
                    for (GameObject gameObject : pipeArray) {
                        gameObject.setLeftX(gameObject.getLeftX() + 5);
                    }
                    for (int c = 0; c < 2; c++) {
                        for (int r = 0; r < 40; r++) {
                            //square[c][r].setLeftX(square[c][r].getLeftX() + 5);
                            square.get(c).get(r).setLeftX(square.get(c).get(r).getLeftX() + 5);
                        }
                    }
                }
                if (mario.canMoveDown && !jumping) { //Falls if needed - Kind of glitchy
                    mario.jump(false, 20); //Falling half of jump
                }
            } else if (rkd) { //Move right
                if (mario.canMoveRight){
                    mario.moveRight(5);
                    //Turn this into an array of arrays and go through all changing x

                    /*Villain Right*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() - 5);
                    }
                    for (GameObject gameObject : pipeArray) {
                        gameObject.setLeftX(gameObject.getLeftX() - 5);
                    }
                    for (int c = 0; c < 2; c++) {
                        for (int r = 0; r < 40; r++) {
                            //square[c][r].setLeftX(square[c][r].getLeftX() - 5);
                            square.get(c).get(r).setLeftX(square.get(c).get(r).getLeftX() - 5);
                        }
                    }
                }
                if (mario.canMoveDown && !jumping) {
                    mario.jump(false, 20 );
                }
            }
            if (jumping) {
                if (mario.getJumpCount() > 20) { //Need to switch to falling? (second half of curve)
                    up = false;
                }
                jumping = mario.jump(up, 20);
            }
            this.canvas.repaint(); //Repaints Canvas

        }

    }

    class PaintSurface extends JComponent {
        PaintSurface() {
        }

        public void paint(Graphics g) {
            if (screen.equals("level1")) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                //Background
                ImageIcon skyIcon = new ImageIcon("src/resources/others/sky.jpg");
                Image sky = skyIcon.getImage();
                g2.drawImage(sky, 0, 0, 800, 600, this);

                //Pipe
                for (GameObject gameObject : pipeArray) {
                    ImageIcon pipeIcon = new ImageIcon("src/resources/others/pipe.png");
                    Image pipeImg = pipeIcon.getImage();
                    g2.drawImage(pipeImg, gameObject.getLeftX(), gameObject.getTopY(), gameObject.getW(), gameObject.getH(), this);
                }
                //Array of blocks for ground
                ImageIcon groundIcon = new ImageIcon("src/resources/others/ground.png");
                Image ground = groundIcon.getImage();
                for (int c = 0; c < 2; c++) {
                    for (int r = 0; r < 40; r++) {
                        //g2.drawImage(ground, square[c][r].getLeftX(), square[c][r].getTopY(), square[c][r].getW(), square[c][r].getH(), this);
                        g2.drawImage(ground, square.get(c).get(r).getLeftX(), square.get(c).get(r).getTopY(), square.get(c).get(r).getW(), square.get(c).get(r).getH(), this);
                    }
                }

                /*Villain Array*/
                for (Villain villain : villainArray) {
                    if (!(villain.isDead())) {
                        ImageIcon villainIcon = new ImageIcon(villain.image());
                        Image villainImage = villainIcon.getImage();
                        g2.drawImage(villainImage, villain.getLeftX(), villain.getTopY(), villain.getW(), villain.getH(), this);
                    }
                }

                //Mario - Must always be added last so that when he dies he falls in front of everything
                ImageIcon i2 = new ImageIcon(mario.image());
                Image marioIcon = i2.getImage();
                g2.drawImage(marioIcon, mario.getLeftX(), mario.getTopY(), mario.getW(), mario.getH(), this);

            } else if (screen.equals("gameOver")) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                //White background
                Shape background = new Float(0.0F, 0.0F, 600.0F, 600.0F);
                g2.setPaint(Color.white);
                g2.fill(background);

                //Black Game Over Text
                ImageIcon ii = new ImageIcon("src/resources/gameOverScreen.png");
                Image gameOverScreen;
                gameOverScreen = ii.getImage();
                g2.drawImage(gameOverScreen, 200, 200, 200, 200, this);
            }
        }
    }

    private class KeyLis extends KeyAdapter {
        private KeyLis() {
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37: //Left Arrow Key
                    Frame.lkd = true;
                    break;
                case 38: //Down Arrow Key
                    if (!jumping) {
                        //Frame.ukd = true;
                        mario.setJumpCount(0);
                        jumping = true;
                        up = true;
                    }
                    break;
                case 39: //Right Arrow Key
                    Frame.rkd = true;
                case 40: //Down Arrow Key
                    Frame.dkd = true;
                    break;
                case 68: //D Key
                    gameOver = true;
                    break;
                case 82: //R Key
                    gameOver = false;
                    resetL1();
                    screen = "level1";
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37 -> //Left Arrow Key
                        Frame.lkd = false;
                case 39 -> //Right Arrow Key
                        Frame.rkd = false;
                case 40 -> //Down Arrow Key
                        Frame.dkd = false;

                //Left Arrow Key release not needed as handled by the jump function
            }
        }
    }

    public void collisionDetection() {
        int c = 1;

        for (int r = 0; r < 40; r++) {
            //If touching the ground
            if (mario.getBottomY() + 20 > square.get(c).get(r).getTopY()) { //If the bottom of mario after moving is further down (including by 0) than the top of the square
                mario.moveDown(square.get(c).get(r).getTopY() - mario.getBottomY()); //Move down remaining distance (between 20 and 0)
                mario.setCanMoveDown(false);
            }
            //If above ground
            else if (mario.getBottomY() < square.get(c).get(r).getTopY()){ //If marios foot is higher than the top of the square
                mario.setCanMoveDown(true);
            }
            //If ground not detected (hole?) <-- doesn't work
            //Other stuff only accounts for Y values, maybe wrap all in an x value thing for extremes?
            //Then will need multiple arrays for when there are gaps... Which would result in even more collision detection
            //Or a way of finding out which block you are on?
        }


        for (Character character : characterArray) {
            for (GameObject gameObject : pipeArray) { //This is an enhanced for loop - originally suggested by IntelliJ, loops through all items in a list
                //If below the pipe
                if (character.getBottomY() > gameObject.getTopY()) {
                    //If on the right-hand side
                    if (character.getRightX() >= gameObject.getLeftX() && character.getLeftX() < gameObject.getRightX()) {
                        if (character == mario) {
                            character.setCanMoveRight(false);
                            break;
                        } else {
                            character.bounce();
                        }
                    } else {
                            character.setCanMoveRight(true);
                    }
                    //If on left-hand side
                    if (character.getLeftX() <= gameObject.getRightX() && character.getRightX() > gameObject.getLeftX()) {
                        if (character == mario) {
                            character.setCanMoveLeft(false);
                            break;
                        } else {
                            character.bounce();
                        }
                    } else {
                        character.setCanMoveLeft(true);
                    }
                }
                //If on top of pipe
                else if ((character.getBottomY() + 20 > gameObject.getTopY()) && ((character.getRightX() > gameObject.getLeftX() && character.getLeftX() < gameObject.getRightX()))) {
                    character.moveDown(gameObject.getTopY() - character.getBottomY());
                    character.setCanMoveDown(false);
                } else { //If above pipe level
                    character.setCanMoveLeft(true);
                    character.setCanMoveRight(true);
                }
            }
        }

        /*Villain Collision with Mario*/
        for (Villain villain : villainArray) {
            if (!villain.isDead()) {
                if (mario.getRightX() > villain.getLeftX() && mario.getLeftX() < villain.getRightX()) {
                    if (mario.getBottomY() > villain.getTopY() && jumping && !up) {
                        //if jumping is true and up = false then he is falling and can squish
                        //else Mario kills Gary
                        villain.setH(20);
                        villain.setTopY(480);
                        mario.setJumpCount(10);
                        villain.setDead(true);
                    } else if (mario.getBottomY() > villain.getTopY()) {
                        //If Gary kills Mario
                        gameOver = true;
                    }
                }
            }
        }
    }
}

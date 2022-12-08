/*
Notes for next time
- When falling off a pipe onto a Goomba, Mario dies. In the original game this does not happen, instead he kills them.
The current algorithm uses jump functions to determine whether the Goomba should be killed. This is not going to work long term
 */

package com.company;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import javax.swing.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class Frame extends JFrame {
    Frame.PaintSurface canvas = new Frame.PaintSurface();
    Timer t;
    int totalMoved = 0;

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

    ArrayList<Villain> villainArray = new ArrayList<>();
    Villain goombaGary;
    Villain goombaBab;
    Villain goombaCarl;
    int[] goombaPlacement;

    ArrayList<ArrayList<GameObject>> ground = new ArrayList<>();

    ArrayList<GameObject> pipeArray = new ArrayList<>();
    GameObject pipe;
    GameObject pipe2;
    GameObject pipe3;
    GameObject[] gameObjects;

    public static void main(String[] args) {
        new Frame();
    }

    public Frame() {
        this.setSize(800, 627); //627 to make up for bar at top
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Kills code on close
        this.setTitle("Mario");
        resetL1();
        this.add(this.canvas); //Add the painted area
        this.setVisible(true);

        /*Timer*/
        this.t = new Timer(10, (e) -> this.tick()); //Runs variable tick - collisions tick, move, repaint
        this.t.start(); //Starts timer

        /*Key Listeners*/
        this.listener = new Frame.KeyLis();
        this.addKeyListener(this.listener);
    }


    public void resetL1(){
        /*All values cleared*/
        totalMoved = 0;
        ground.clear();
        villainArray.clear();
        characterArray.clear();
        pipeArray.clear();

        /*Instantiating each item*/
        //In future these values could be read from a text file and a new level could be added
        //Using code similar to:
        //goombaPlacement ArrayList, brick placement ArrayList, hidden ground placement ArrayList
        //Where the ints are read from the txt file
        //I tried it, but it caused random problems that I couldn't track properly

        mario = new Mario("src/resources/right/SmallStand.png", 100, 450, 50, 50);
        characterArray.add(mario);

        /*Make array of  grounds*/
        for (int r = 0; r < 2; r++) {
            ground.add(new ArrayList<>());
            for (int c = 0; c < 80;  c++) {
                ground.get(r).add( c , new GameObject( c * 50, 550 - (r * 50), 50, 50));
            }
        }

        int current = 0;
        try {
            File inputFile = new File ("src/resources/level1.txt");
            Scanner myReader = new Scanner(inputFile);
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();
                if(data.equals("")){
                    current++;
                } else {
                    if (current == 0) {
                        //Goombas
                        String[] values = data.split(", ");
                        villainArray.add(new Villain(Integer.parseInt(values[0]), Integer.parseInt(values[1]), 50, 50));
                        characterArray.add(villainArray.get(villainArray.size()-1));
                    } else if (current == 1) {
                        //Pipes
                        String[] values = data.split(", ");
                        pipeArray.add(new GameObject(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3])));
                    } else if (current == 2){
                        //Holes
                        for (int j = 0; j < 2 ; j++) {
                            for (int i = 0; i < 2; i++) {
                                ground.get(i).get(Integer.parseInt(data) + j).setHidden(true);
                            }
                        }
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e){
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
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
                if (mario.canMoveLeft && (totalMoved -5 > 0)) {
                    mario.moveLeft(5);
                    totalMoved = totalMoved - 5;
                    //Turn this into an array of arrays and go through all changing x

                    /*Villain Left*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() + 5);
                    }
                    for (GameObject gameObject : pipeArray) {
                        gameObject.setLeftX(gameObject.getLeftX() + 5);
                    }
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 80;  c++) {
                            // ground[c][r].setLeftX( ground[c][r].getLeftX() + 5);
                            ground.get(r).get(c).setLeftX( ground.get(r).get(c).getLeftX() + 5);
                        }
                    }
                }
                if (mario.canMoveDown && !jumping) { //Falls if needed - Kind of glitchy
                    mario.jump(false, 20); //Falling half of jump
                }
            } else if (rkd) { //Move right
                if (mario.canMoveRight && (totalMoved +5 < 3900)){ //This value must be changed if length of level changes
                    mario.moveRight(5);
                    totalMoved = totalMoved + 5;
                    //Turn this into an array of arrays and go through all changing x

                    /*Villain Right*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() - 5);
                    }
                    for (GameObject gameObject : pipeArray) {
                        gameObject.setLeftX(gameObject.getLeftX() - 5);
                    }
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 80;  c++) {
                            // ground[c][r].setLeftX( ground[c][r].getLeftX() - 5);
                            ground.get(r).get(c).setLeftX( ground.get(r).get(c).getLeftX() - 5);
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
                Image groundImg = groundIcon.getImage();
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 80;  c++) {
                        if(!ground.get(r).get(c).isHidden()) {
                            g2.drawImage(groundImg, ground.get(r).get(c).getLeftX(), ground.get(r).get(c).getTopY(), ground.get(r).get(c).getW(), ground.get(r).get(c).getH(), this);
                        }
                    }
                }

                //Villain Array
                for (Villain villain : villainArray) {
                    if (!(villain.isHidden())) {
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
                Shape background = new Float(000.0F, 0.0F, 800.0F, 600.0F);
                g2.setPaint(Color.white);
                g2.fill(background);

                //Black Game Over Text
                ImageIcon ii = new ImageIcon("src/resources/gameOverScreen.png");
                Image gameOverScreen;
                gameOverScreen = ii.getImage();
                g2.drawImage(gameOverScreen, 300, 200, 200, 200, this);

                JLabel label1 = new JLabel("Test");
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
        //Mushrooms must bounce against one another as well as the pipes, maybe this could extend the mario villain collision part?
            int r = 1;
            int c = totalMoved/50 + 2;
            //Broken :( Only works when moving left
            if (mario.getBottomY() + 20 > ground.get(r).get(c).getTopY()) { //If the bottom of mario after moving is further down (including by 0) than the top of the  ground
                if (ground.get(r).get(c).isHidden()) { //If there is a hole
                    gameOver = true;
                } else {
                    mario.moveDown(ground.get(r).get(c).getTopY() - mario.getBottomY()); //Move down remaining distance (between 20 and 0)
                    mario.setCanMoveDown(false);
                }
            }
            //If above ground
            else if (mario.getBottomY() < ground.get(r).get(c).getTopY()) { //If marios foot is higher than the top of the  ground
                mario.setCanMoveDown(true);
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
            if (!villain.isHidden()) {
                if (mario.getRightX() > villain.getLeftX() && mario.getLeftX() < villain.getRightX()) {
                    if (mario.getBottomY() > villain.getTopY() && jumping && !up) {
                        //if jumping is true and up = false then he is falling and can squish
                        //else Mario kills Gary
                        villain.setH(20);
                        villain.setTopY(480);
                        mario.setJumpCount(10);
                        villain.setHidden(true);
                    } else if (mario.getBottomY() > villain.getTopY()) {
                        //If Gary kills Mario
                        gameOver = true;
                    }
                }
            }
        }
    }
}
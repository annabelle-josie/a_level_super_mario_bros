package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Frame extends JFrame {
    Frame.PaintSurface canvas = new Frame.PaintSurface();
    Timer t;
    int score = 0;
    int level = 1;
    int coins = 0;
    int totalMoved = 0; //totalMoved and currentGround are both used for detecting holes
    int currentGround = 0;
    boolean boxAnimating = false; //For the completion of movement of boxes
    int transparentPlacement = 360; //For level select
    Villain bowser;

    /*Key Listeners*/
    private Frame.KeyLis listener;
    static Boolean lkd = false;
    static Boolean rkd = false;

    /*To change the screen*/
    Boolean gameOver = false;
    String screen = "startScreen";

    /*Jumping Variables*/
    Boolean jumping = false;
    Boolean up = false; //Is it moving up?

    /*Things*/
    Mario mannequinMario; //Mannequins are for the moving Mario and Goomba on the startScreen
    Villain mannequinGoomba;
    Mario mario;
    GameObject castle;
    ImageIcon bowserIcon = new ImageIcon("src/resources/characters/bowser.png");
    //^Set as global so can be changed in tick and accessed in paint
    ArrayList<Character> characterArray = new ArrayList<>();
    ArrayList<Villain> villainArray = new ArrayList<>();
    ArrayList<ArrayList<GameObject>> ground = new ArrayList<>();
    ArrayList<GameObject> pipeArray = new ArrayList<>();
    ArrayList<GameObject> objectArray = new ArrayList<>();
    ArrayList<Box> boxArray = new ArrayList<>();
    ArrayList<GameObject> collisionArray = new ArrayList<>();
    ArrayList<GameObject> tutorialArray = new ArrayList<>();
    ArrayList<GameObject> extraItems = new ArrayList<>();

    public static void main(String[] args) {
        new Frame(); //Instantiates the frame, everything is then run through this
    }

    public Frame() {
        this.setSize(800, 627); //627 to make up for bar at top
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Kills code on close
        this.setTitle("Mario");
        resetLevel(); //This resets all values, taking information from level files if needed
        this.add(this.canvas); //Add the painted area (where all components lie)
        this.setVisible(true); //Make the window visible

        /*Timer*/
        this.t = new Timer(10, (e) -> this.tick()); //Runs variable tick - collisions tick, move, repaint
        this.t.start(); //Starts timer

        /*Key Listeners*/
        this.listener = new Frame.KeyLis();
        this.addKeyListener(this.listener);
    }

    public void resetLevel(){
        /*All values cleared*/
        totalMoved = 0;
        currentGround = 2;
        ground.clear();
        villainArray.clear();
        characterArray.clear();
        pipeArray.clear();
        boxArray.clear();
        objectArray.clear();
        collisionArray.clear();
        extraItems.clear();
        tutorialArray.clear();

        /*Instantiating the items*/
        mario = new Mario("src/resources/right/SmallStand.png", 100, 450, 50, 50);
        characterArray.add(mario);
        bowserIcon = new ImageIcon("src/resources/characters/bowser.png");

        /*Make array of  ground*/
        for (int r = 0; r < 2; r++) {
            ground.add(new ArrayList<>());
            for (int c = 0; c < 200;  c++) {
                ground.get(r).add(c , new GameObject( c * 50, 550 - (r * 50), 50, 50));
            }
        }

        String file = ("src/resources/level"+level+".txt"); //Each level can be made from an imported txt file
        int current = 0; //A counter to go through each object adding them to the correct arrays
        try {
            File inputFile = new File(file);
            Scanner myReader = new Scanner(inputFile);
            while (myReader.hasNextLine()){
                String data = myReader.nextLine();
                if(data.equals("")){
                    //Files are built so a line is left between the different objects
                    current++;
                } else {
                    if (current == 0) {
                        //Goombas - Inputs x, y
                        String[] values = data.split(", ");
                        try {
                            villainArray.add(new Villain(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                                    50, 50));
                            //Set width/height of 50/50
                            characterArray.add(villainArray.get(villainArray.size() - 1));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.out.println("Level txt file incorrect formatting. Source: Goomba");
                        }
                    } else if (current == 1) {
                        //Pipes - Inputs x, y, w, h
                        String[] values = data.split(", ");
                        try {
                            pipeArray.add(new GameObject(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                                    Integer.parseInt(values[2]), Integer.parseInt(values[3])));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.out.println("Level txt file incorrect formatting. Source: Pipes");
                        }
                    } else if (current == 2){
                        //Holes - Inputs brick number to be hidden.
                        // Creates width of 2, so if 5 inputted, 5 and 6 will be the hole
                        try {
                            if (Integer.parseInt(data) < 200) {
                                for (int j = 0; j < 2; j++) {
                                    for (int i = 0; i < 2; i++) {
                                        ground.get(i).get(Integer.parseInt(data) + j).setHidden(true);
                                    }
                                }
                            } else {
                                System.out.println("Level txt file incorrect formatting. Source: Holes");
                            }
                        } catch (NumberFormatException e){
                            System.out.println("Level txt file incorrect formatting. Source: Holes");
                        }
                    } else if (current == 3){
                        //Boxes - Inputs x, y, and contents (coin, none, powerup, block)
                        String[] values = data.split(", ");
                        try{
                            if (values[2].equals("block")) {
                                //This means that the blocks can be added downwards until they meet the ground.
                                // This reduces the size of the txt files and makes them far easier to write
                                for (int i = 0; (Integer.parseInt(values[1]) + i * 50) < 500; i++) {
                                    boxArray.add(new Box(Integer.parseInt(values[0]),
                                            Integer.parseInt(values[1]) + i * 50, values[2]));
                                }
                            } else {
                                boxArray.add(new Box(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                                        values[2]));
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.out.println("Level txt file incorrect formatting. Source: Boxes");
                        }
                    } else if (current == 4){
                        //Castle - Inputs x, y for castle aka end of the level
                        String[] values = data.split(", ");
                        try{
                            castle = new GameObject(Integer.parseInt(values[0]), Integer.parseInt(values[1]), 400,
                                    400, "src/resources/items/castle.png");
                            tutorialArray.add(castle);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.out.println("Level txt file incorrect formatting. Source: Castle");
                        }
                    } else if (current == 5){
                        //Others (mostly for tutorial images) - Inputs x, y, w, h, Image file
                        String[] values = data.split(", ");
                        try{
                            tutorialArray.add(new GameObject(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
                                    Integer.parseInt(values[2]), Integer.parseInt(values[3]), values[4]));
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.out.println("Level txt file incorrect formatting. Source: Other");
                        }
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e){ //If the file doesn't exist
            //This is most likely (almost certainly) because the level is too high
            //Therefore they must have run out of levels aka won so screen is changed to winner
            totalMoved = 0;
            currentGround = 2;
            ground.clear();
            villainArray.clear();
            characterArray.clear();
            pipeArray.clear();
            boxArray.clear();
            objectArray.clear();
            collisionArray.clear();
            extraItems.clear();
            tutorialArray.clear();

            jumping = false;
            mario.resetValues(0,450,50,50);
            bowser = new Villain( "src/resources/characters/bowser.png", 500, 350, 150, 150);
            screen="winner";
        }
        /*All objects added to larger arrays for later use*/
        objectArray.addAll(pipeArray);
        objectArray.addAll(ground.get(0));
        objectArray.addAll(ground.get(1));
        objectArray.addAll(boxArray);
        objectArray.addAll(tutorialArray);
        collisionArray.addAll(boxArray);
        collisionArray.addAll(pipeArray);
    }

    public void tick() {
        if (screen.equals("startScreen")) { //Mario and Goomba with animations for start screen
            mannequinGoomba.setChange(0);
            mannequinGoomba.spot();
            mannequinMario.spot();
        }
        else if (screen.equals("winner")) {
            if(mario.getLeftX() < 450) { //Whilst Mario has not gone past Peach
                // Check for all collisions and correct them
                if (lkd) {
                    mario.moveLeft(5);
                } else if (rkd) {
                    mario.moveRight(5);
                }
                if (mario.getLeftX() > 250) { //If next to Bowser
                    bowserIcon = new ImageIcon("src/resources/defeat.png"); //Bowser is defeated
                }
            } else{ //After past Peach, restart the game
                try {
                    Thread.sleep(500); //Wait stops the game looking like it has ended too suddenly.
                    // Gives the player a chance to see what they did
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                screen="startScreen";
            }
            canvas.repaint();
        }
        else if (gameOver || mario.getBottomY() + 20 >= 600) {
            //Makes mario fall off-screen, then switch the screen to --GAME OVER--
            mario.die(); //Mario falls off-screen
            if (mario.getBottomY() >= 600) { //When Mario reaches the bottom
                try {
                    Thread.sleep(100); //Wait stops the game looking like it has ended too suddenly.
                    // Gives the player a chance to see what they did
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                screen = "gameOver"; //Screen switches
            }
        } else {
            if(!screen.equals("startScreen") && !screen.equals("levelSelect")) {
                collisionDetection(); //Check for all collisions and correct them
            }
            /*Animations*/
            if (!extraItems.isEmpty()) {
                if (extraItems.get(0).jump()) {
                    score = score + 1;
                    extraItems.remove(0);
                }
            }
            for (Box box : boxArray) {
                if (box.isAnimating()) { //Allow boxes to animate even when not underneath them
                    box.powerup();
                }
            }
            /*Villain Animate*/
            for (Villain villain : villainArray) {
                villain.animate();
                if (villain.canMoveDown) {
                    villain.moveDown(5);
                }
            }
            if (lkd) { //Move left
                if (mario.canMoveLeft && (totalMoved - 5 > 0)) {
                    mario.moveLeft();
                    totalMoved = totalMoved - 5;
                    /*Villain Left*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() + 5);
                    }
                    /*Objects Left*/
                    for (GameObject gameObject : objectArray) {
                        gameObject.setLeftX(gameObject.getLeftX() + 5);
                    }
                }
                if (mario.canMoveDown && !jumping) { //Falls if needed
                    mario.jump(false, 20); //Runs only falling half of jump
                }
            } else if (rkd) { //Move right
                if (screen.equals("level") && mario.getLeftX() + 5 > castle.getLeftX()+180) {
                    //If reached the door of the castle (i.e. passed the level)
                    mario.moveRight(5);
                    totalMoved = totalMoved + 5;
                    screen = "levelUp";
                    canvas.repaint();
                } else if (mario.canMoveRight) {
                    mario.moveRight();
                    totalMoved = totalMoved + 5;
                    /*Villain Right*/
                    for (Villain villain : villainArray) {
                        villain.setLeftX(villain.getLeftX() - 5);
                    }
                    /*Objects Right*/
                    for (GameObject gameObject : objectArray) {
                        gameObject.setLeftX(gameObject.getLeftX() - 5);
                    }
                }
                if (mario.canMoveDown && !jumping) { //Falls if needed
                    mario.jump(false, 20); //Runs only falling half of jump
                }
            }
            if (jumping) {
                if (mario.getJumpCount() > 20) { //Need to switch to falling? (second half of curve)
                    up = false;
                }
                jumping = mario.jump(up, 20);
            }
        }
        this.canvas.repaint(); //Repaints Canvas
    }

    public void mannequins(){
        //Instantiating the variables for mannequins
        if(mannequinMario == null) {
            mannequinMario = new Mario("src/resources/right/SmallStand.png", 450, 405, 100,
                    100);
            mannequinGoomba = new Villain(250, 405, 100, 100);
        }
    }

    class PaintSurface extends JComponent {
        PaintSurface() {
        }
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            switch (screen) {
                case "startScreen" -> {
                    mannequins();
                    //Start Screen
                    ImageIcon ii = new ImageIcon("src/resources/screens/startScreen.png");
                    Image gameOverScreen = ii.getImage();
                    g2.drawImage(gameOverScreen, 0, 0, 800, 600, this);

                    //Goomba
                    ImageIcon villainIcon = new ImageIcon(mannequinGoomba.image());
                    Image villainImage = villainIcon.getImage();
                    g2.drawImage(villainImage, mannequinGoomba.getLeftX(), mannequinGoomba.getTopY(),
                            mannequinGoomba.getW(), mannequinGoomba.getH(), this);

                    //Mario
                    ImageIcon i2 = new ImageIcon(mannequinMario.image());
                    Image marioIcon = i2.getImage();
                    g2.drawImage(marioIcon, mannequinMario.getLeftX(), mannequinMario.getTopY(), mannequinMario.getW(),
                            mannequinMario.getH(), this);
                }
                case "levelSelect"-> {
                    //Start Screen
                    ImageIcon ii = new ImageIcon("src/resources/screens/levelSelect.png");
                    Image gameOverScreen = ii.getImage();
                    g2.drawImage(gameOverScreen, 0, 0, 800, 600, this);

                    //Highlight
                    Shape highlight = new Float(200, transparentPlacement, 400, 40);
                    g2.setPaint(Color.white);
                    AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2.setComposite(alcom);
                    g2.fill(highlight);
                }
                case "level" -> {
                    //Background
                    ImageIcon skyIcon;
                    if(level == 0) {
                        skyIcon = new ImageIcon("src/resources/screens/noCloudBackground.png");
                    } else{
                        skyIcon = new ImageIcon("src/resources/screens/levelBackground.png");
                    }
                    Image sky = skyIcon.getImage();
                    g2.drawImage(sky, 0, 0, 800, 600, this);

                    //Coin Count
                    ImageIcon coinIcon = new ImageIcon("src/resources/numbers/" + coins + ".png");
                    Image coinImg = coinIcon.getImage();
                    g2.drawImage(coinImg, 295, 30, 50, 50, this);

                    //Score
                    ImageIcon scoreIcon = new ImageIcon("src/resources/numbers/" + score +"00.png");
                    Image scoreImg = scoreIcon.getImage();
                    g2.drawImage(scoreImg, 483, 30, 75, 50, this);

                    //Level
                    ImageIcon levelIcon = new ImageIcon("src/resources/numbers/"+level+".png");
                    Image levelImg = levelIcon.getImage();
                    g2.drawImage(levelImg, 695, 30, 50, 50, this);

                    //Additional images (information for tutorial)
                    for (GameObject gameObject : tutorialArray) {
                        ImageIcon theIcon = new ImageIcon(gameObject.image());
                        Image theImage = theIcon.getImage();
                        g2.drawImage(theImage, gameObject.getLeftX(), gameObject.getTopY(), gameObject.getW(),
                                gameObject.getH(), this);
                    }

                    //Pipe
                    for (GameObject gameObject : pipeArray) {
                        ImageIcon pipeIcon = new ImageIcon("src/resources/items/pipe.png");
                        Image pipeImg = pipeIcon.getImage();
                        g2.drawImage(pipeImg, gameObject.getLeftX(), gameObject.getTopY(), gameObject.getW(),
                                gameObject.getH(), this);
                    }

                    //Array of blocks for ground
                    ImageIcon groundIcon = new ImageIcon("src/resources/items/ground.png");
                    Image groundImg = groundIcon.getImage();
                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 200; c++) {
                            if (!ground.get(r).get(c).isHidden()) {
                                g2.drawImage(groundImg, ground.get(r).get(c).getLeftX(), ground.get(r).get(c).getTopY(),
                                        ground.get(r).get(c).getW(), ground.get(r).get(c).getH(), this);
                            }
                        }
                    }

                    for (Box box : boxArray) {
                        if (!box.isHidden()) {
                            ImageIcon villainIcon = new ImageIcon(box.image());
                            Image villainImage = villainIcon.getImage();
                            g2.drawImage(villainImage, box.getLeftX(), box.getTopY(), box.getW(), box.getH(),
                                    this);
                        }
                    }

                    //Villain Array
                    for (Villain villain : villainArray) {
                        if (!(villain.isHidden())) {
                            ImageIcon villainIcon = new ImageIcon(villain.image());
                            Image villainImage = villainIcon.getImage();
                            g2.drawImage(villainImage, villain.getLeftX(), villain.getTopY(), villain.getW(),
                                    villain.getH(), this);
                        }
                    }

                    for (GameObject gameObject : extraItems) {
                        ImageIcon theIcon = new ImageIcon(gameObject.image());
                        Image theImage = theIcon.getImage();
                        g2.drawImage(theImage, gameObject.getLeftX(), gameObject.getTopY(), gameObject.getW(),
                                gameObject.getH(), this);
                    }

                    //Mario - Must always be added last so that when he dies he falls in front of everything
                    ImageIcon i2 = new ImageIcon(mario.image());
                    Image marioIcon = i2.getImage();
                    g2.drawImage(marioIcon, mario.getLeftX(), mario.getTopY(), mario.getW(), mario.getH(),
                            this);
                }
                case "levelUp" ->{
                    ImageIcon backgroundIcon = new ImageIcon("src/resources/screens/levelUp.png");
                    Image background = backgroundIcon.getImage();
                    g2.drawImage(background, 0, 0, 800, 600, this);

                    //Coin Count
                    ImageIcon coinIcon = new ImageIcon("src/resources/numbers/" + coins + ".png");
                    Image coinImg = coinIcon.getImage();
                    g2.drawImage(coinImg, 295, 30, 50, 50, this);

                    //Score
                    ImageIcon scoreIcon = new ImageIcon("src/resources/numbers/" + score +"00.png");
                    Image scoreImg = scoreIcon.getImage();
                    g2.drawImage(scoreImg, 483, 30, 75, 50, this);

                    //Level
                    ImageIcon levelIcon = new ImageIcon("src/resources/numbers/"+level+".png");
                    Image levelImg = levelIcon.getImage();
                    g2.drawImage(levelImg, 695, 30, 50, 50, this);
                }
                case "gameOver" -> {
                    //White background
                    Shape background = new Float(000.0F, 0.0F, 800.0F, 600.0F);
                    g2.setPaint(Color.white);
                    g2.fill(background);

                    //Black Game Over Text
                    ImageIcon ii = new ImageIcon("src/resources/screens/gameOverScreen.png");
                    Image gameOverScreen = ii.getImage();
                    g2.drawImage(gameOverScreen, 100, -50, 600, 600, this);

                }
                case "winner" ->{
                    ImageIcon backgroundIcon = new ImageIcon("src/resources/screens/winnerBackground.png");
                    Image background = backgroundIcon.getImage();
                    g2.drawImage(background, 0, 0, 800, 600, this);

                    //Coin Count
                    ImageIcon coinIcon = new ImageIcon("src/resources/numbers/" + coins + ".png");
                    Image coinImg = coinIcon.getImage();
                    g2.drawImage(coinImg, 295, 30, 50, 50, this);

                    //Score
                    ImageIcon scoreIcon = new ImageIcon("src/resources/numbers/" + score +"00.png");
                    Image scoreImg = scoreIcon.getImage();
                    g2.drawImage(scoreImg, 483, 30, 75, 50, this);

                    //Level
                    ImageIcon levelIcon = new ImageIcon("src/resources/numbers/"+level+".png");
                    Image levelImg = levelIcon.getImage();
                    g2.drawImage(levelImg, 695, 30, 50, 50, this);

                    //Peach
                    ImageIcon peachIcon = new ImageIcon("src/resources/characters/peach.png");
                    Image peachImage = peachIcon.getImage();
                    g2.drawImage(peachImage, 500, 450, 35, 50, this);

                    //Bowser
                    Image bowserImage = bowserIcon.getImage(); //bowserIcon is universal so it can be removed
                    g2.drawImage(bowserImage, 300, 350, 150, 150, this);

                    //Mario
                    ImageIcon i2 = new ImageIcon(mario.image());
                    Image marioIcon = i2.getImage();
                    g2.drawImage(marioIcon,mario.getLeftX(), mario.getTopY(), 50, 50, this);
                }
            }
        }
    }

    private class KeyLis extends KeyAdapter {
        private KeyLis() {
        }
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                /*Movement*/
                case 37 -> { //Left Arrow Key - Move Left
                    Frame.lkd = true;
                    System.out.println("Left Arrow Key Pressed");
                }
                case 38 -> { //Up Arrow Key - Jump (if not already jumping)
                    if (!jumping) {
                        //Frame.ukd = true;
                        mario.setJumpCount(0);
                        jumping = true;
                        up = true;
                    }
                    System.out.println("Up Arrow Key Pressed");
                }
                case 39 -> { //Right Arrow Key - Move Right
                    Frame.rkd = true;
                    System.out.println("Right Arrow Key Pressed");
                }
                case 32 -> { //Space Key - Change screen and jump
                    System.out.println("Space Key Pressed");
                    switch (screen) {
                        case "startScreen" -> screen = "levelSelect";
                        case "levelSelect" -> {
                            if (transparentPlacement == 360) {
                                //The placement of the box determines which level should be loaded
                                level = 0;
                            } else {
                                level = 1;
                            }
                            screen = "level";
                            resetLevel();
                        }
                        case "levelUp" -> {
                            level++;
                            screen = "level";
                            resetLevel();
                        }
                        case "level" -> {
                            //Can be used to make character jump after stakeholder feedback (see notes)
                            if (!jumping) {
                                //Frame.ukd = true;
                                mario.setJumpCount(0);
                                jumping = true;
                                up = true;
                            }
                        }
                    }
                }
                /*Other Controls*/
                case 48 -> { //0 key
                    System.out.println("0 Key Pressed");
                    if (screen.equals("levelSelect")) {
                        transparentPlacement = 360;
                    }
                }
                case 49 -> { //1 key
                    System.out.println("1 Key Pressed");
                    if (screen.equals("levelSelect")) {
                        transparentPlacement = 400;
                    }
                }
                case 77 -> { //M Key - Load the menu
                    System.out.println("M Key Pressed");
                    screen = "levelSelect";
                    score = 0;
                    coins = 0;
                }
                case 82 -> { //R Key - Restart
                    System.out.println("R Key Pressed");
                    gameOver = false;
                    resetLevel();
                    screen = "level";
                    score = 0;
                    coins = 0;
                }
                /*Testing Tools - Would be deleted before publishing game
                case 16: //Shift - For testing
                    level++;
                    screen="level";
                    resetLevel();
                    break;
                case 68: //D Key - Testing tool to force Mario to die
                    gameOver = true;
                    break;
                case 87: //W key - Testing Tool loads winner sequence
                    level=500;
                    resetLevel();
                    break;

                 */
            }
        }
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37 -> //Left Arrow Key
                        Frame.lkd = false;
                case 39 -> //Right Arrow Key
                        Frame.rkd = false;
                //Up Arrow Key release not needed as handled by the jump function
            }
        }
    }

    public void collisionDetection() {
        /*Holes*/
        int r = 1; //Only need to look at top row
        currentGround = totalMoved / 50 + 2;
        if (mario.getBottomY() + 20 > ground.get(r).get(currentGround).getTopY()) { //If below ground withing 1 fall
            if (!ground.get(r).get(currentGround).isHidden()) { //If it is not a hole
                mario.moveDown(ground.get(r).get(currentGround).getTopY() - mario.getBottomY());
                mario.canMoveDown = false;
            }
            if (rkd || lkd) { //If moving left or right
                if ((ground.get(r).get(totalMoved / 50 + 2).isHidden())
                        && (ground.get(r).get((totalMoved + 50) / 50 + 2).isHidden())) {
                    mario.canMoveDown = true;
                }
            }
        } else {
            mario.canMoveDown = true;
        }

        /*Pipes & bricks*/
        for (Character character : characterArray) {
            for (GameObject gameObject : collisionArray) {
                if(!gameObject.isHidden()) {
                    //If below the pipe
                    if (character.getBottomY() > gameObject.getTopY()) {
                        //If underneath
                        if (character.getTopY() >= gameObject.getBottomY() &&
                                ((character.getRightX() >= gameObject.getLeftX() &&
                                        character.getLeftX() <= gameObject.getRightX()))) {
                            character.setCanMoveLeft(true);
                            character.setCanMoveRight(true);
                            character.setCanMoveUp(true);
                            //This is detecting the part underneath only
                            if((character.getTopY() - gameObject.getBottomY()) < 30) { //If hit the bottom of the object
                                if (character.getTopY() - 20 < gameObject.getBottomY() || boxAnimating) {
                                    character.moveUp(character.getTopY() - gameObject.getBottomY());
                                    character.setCanMoveUp(false);
                                    if (gameObject.isNotCollected()) {
                                        if (gameObject.contains().equals("coin")) {
                                            //Collect and animate coin
                                            extraItems.add(new GameObject(gameObject.getLeftX() + 10,
                                                    gameObject.getTopY() - 40, 30, 40));
                                            extraItems.get(0).setImage("src/resources/items/coin.png");
                                            objectArray.add(extraItems.get(extraItems.size() - 1));
                                            coins++;
                                            if (level == 0) {
                                                //Switches "see what happens" with "you got a coin"
                                                tutorialArray.get(7).setImage("src/resources/welcome/coin.png");
                                            }
                                        }
                                        gameObject.powerup();
                                    }
                                }
                            }
                        } else {
                            //If on the right-hand side
                            if (character.getRightX() >= gameObject.getLeftX() &&
                                    character.getLeftX() < gameObject.getRightX()) {
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
                            if (character.getLeftX() <= gameObject.getRightX() &&
                                    character.getRightX() > gameObject.getLeftX()) {
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
                    }
                    //If on top of object
                    else if ((character.getBottomY() + 20 > gameObject.getTopY()) &&
                            ((character.getRightX() > gameObject.getLeftX() &&
                                    character.getLeftX() < gameObject.getRightX()))) {
                        character.moveDown(gameObject.getTopY() - character.getBottomY());
                        character.setCanMoveDown(false);
                    } else { //If above pipe level
                        character.setCanMoveLeft(true);
                        character.setCanMoveRight(true);
                    }
                }
            }
        }

        /*Villain Collision with Mario*/
        for (Villain villain : villainArray) {
            if (!villain.isHidden()) {
                if (mario.getRightX() > villain.getLeftX() && mario.getLeftX() < villain.getRightX()) {
                    if (mario.getBottomY()+20 > villain.getTopY() && mario.getBottomY() < villain.getTopY()) {
                        //if falling he can kill
                        villain.setHidden(true);
                        score++;
                    } else if (mario.getBottomY() > villain.getTopY()) {
                        //If Goomba kills Mario
                        gameOver = true;
                    }
                }
            }
        }
    }
}













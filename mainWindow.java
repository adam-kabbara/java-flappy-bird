import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.*;

class mainWindow extends JComponent implements KeyListener{
    // Instance variables that define the current characteristics
    // of the animated objecs.
    final int WIDTH = 375;
    final int HEIGHT = 600;
    final int PAUSE = 40;
    static JFrame frame = new JFrame();

    Bird bird;
    Base base;
    BufferedImage bg;
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    private int pressedKeyCode;
    private int releasedKeyCode;
    private int score;
    private int highscore;
    private String highscoreFile = "highscore.txt";

    public mainWindow(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT)); // we set the size to gameWindow 
        frame.addKeyListener(this); // and not frame since frame contains the window properties 
                                   // tab (minimize; full screen; close)
        
        highscore = readHighscore();
        this.bg = utils.loadImage("sprites\\bg.png");
        this.bg = utils.scaleImage(this.bg, 1.3, 1.25);
    }

    public static void main(String[] args) {
        mainWindow gameWindow = new mainWindow();
        frame.add(gameWindow);         
        frame.pack(); // set frame size to content pane

        frame.setTitle("Snake Game");
        //frame.setResizable(false); todo
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Launch your animation!
        gameWindow.start();
    }
    
    // This special method is automatically called when the scene needs to be drawn.
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.drawImage(this.bg, 0, 0, this);
        bird.paintComponent(g);

        for (Pipe p : pipes){
            p.paintComponent(g);
        }
        
        base.paintComponent(g);
        drawScore(g);
    }

    private void drawScore(Graphics g){
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("score: "+score, 0, 15);
        g.drawString("highscore: "+highscore, 0, 30);

    }

    public void start() {
        base = new Base(550, 5);
        bird = new Bird(WIDTH/2, HEIGHT/2, 15); //error is thrown as the bird takes time to load img
        pipes.add(new Pipe(450));
        System.out.println(score);
        System.out.println(highscore);
        beforeStart();

        while (true) {
            bird.fall();
            base.move();

            if (pressedKeyCode == KeyEvent.VK_SPACE){
                bird.jump();
                pressedKeyCode = -1;
            }

            for (int i=0; i<pipes.size(); i++){
                Pipe p = pipes.get(i);

                p.move();
                if (p.x < bird.x && ! p.passed){
                    pipes.add(new Pipe(450));
                    p.passed = true;
                    score++;
                    if(score > highscore){
                        highscore = score;
                    }
                }
                else if(p.x + p.width < 0){
                    pipes.remove(p);
                }
            }
            
            if (checkCollision()){
                System.out.println(bird.y);
                repaint();
                showLostPopup();
                break;
            }

            repaint();
            utils.pause(PAUSE);
        }
    }

    private void beforeStart(){ // wait until player presses key to start game
        repaint();
        while (true){
            utils.pause(100);
            if (pressedKeyCode == KeyEvent.VK_SPACE){
                pressedKeyCode = -1;
                break;
            }
        }
    }

    private void showLostPopup() {
        saveHighscore();
        System.out.println("youuuuu loooose");
        JOptionPane.showMessageDialog(frame, "AHAHAHAHA YOU LOSE");
        frame.dispose();
    }

    private boolean checkCollision(){
        Pipe p;
        if (bird.y + bird.rotatedImage.getHeight() > base.y){
            return true;
        }    
        for (int i=0; i<pipes.size(); i++){
            p = pipes.get(i);
            if (bird.x + bird.rotatedImage.getWidth() > p.x && p.x + p.width > bird.x){
                if (bird.y + bird.rotatedImage.getHeight() > p.bottomY){
                    return true; //bottom pip collision
                }
                else if(p.topY + p.topHeight -40> bird.y){
                    return true; //top pip collision
                }
            }
        }
        return false;
    }

    private void saveHighscore(){
        // write to file
        try {
            FileWriter myWriter = new FileWriter(highscoreFile);
            myWriter.write(String.valueOf(highscore)); //wierd value if i dont convert to string
            myWriter.close();
            System.out.println("Successfully saved highscore to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    private int readHighscore(){
        int hs = 0;
        boolean created = false;

        // create file
        try {
            File file = new File(highscoreFile);
            if (file.createNewFile()) { //true if file created; false if it already exists
                System.out.println("File created: " + file.getName());
                created = true;
            }
            else {
                System.out.println("File already exists.");
            }
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        if (!created){
            // read from file
            try {
                File file = new File(highscoreFile);
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                hs = Integer.parseInt(data);
                }
                myReader.close();
            } 
            catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        return hs;
    }


    /*private boolean checkCollision(){
        if (snake.head.x == snack.x && snake.head.y == snack.y)
            return true;
        return false;
    }*/

    @Override
    public void keyPressed(KeyEvent e){
        if (releasedKeyCode == KeyEvent.VK_SPACE)
            pressedKeyCode = e.getKeyCode();
            releasedKeyCode = -1;
    }
    @Override
    public void keyReleased(KeyEvent e) {
        releasedKeyCode = e.getKeyCode();
    }
    
    //useless
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
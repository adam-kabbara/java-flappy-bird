import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Bird extends JComponent{
    int x;
    int y;
    int width;
    int rotation;
    int jumpPos;
    boolean isJumping;
    BufferedImage image;
    BufferedImage rotatedImage;
    double gravity;
    final double gravityConst = 1.5;

    public Bird(int x, int y, int width){
        this.x = x;
        this.y = y;
        this.width = width;
        this.gravity = 0;
        this.rotation = 0;
        this.jumpPos = this.y;
        this.isJumping = false;
        this.image = utils.loadImage("assets\\yellowbird.png");
        
    }

    public void paintComponent(Graphics g) {
        //g.setColor(Color.RED);
        //g.fillOval(this.x, this.y, this.width, this.width);

        if (this.isJumping){
            this.isJumping = false;
            if (this.rotation > -30)
                this.rotation = -30;
        }
        else if (this.y > this.jumpPos){
            if (this.rotation < 80)
                this.rotation += 10;
        }

        this.rotatedImage = utils.rotateImage(this.image, this.rotation);
        g.drawImage(rotatedImage, this.x, this.y, this);
    }

    public void jump() {
        this.gravity = gravityConst * 8 * -1;
        this.isJumping = true;
        this.jumpPos = this.y;
    }

    public void fall(){
        this.y += gravity;
        gravity += gravityConst;
    }


}

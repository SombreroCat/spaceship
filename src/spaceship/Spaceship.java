
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    Image rocketFly;
    int rocketXPos;
    int rocketYPos;
    int rocketXspeed;
    int rocketYspeed;
    boolean rocketRight;
    int hpleft;
    boolean rocketexplosion;
    int bombxpos;
    int bombypos;
    int scalebomb;
    int bombradius;
    
    int score;
    int highscore;
    boolean gameover;

    Laser laser[];
    
    int numStars = 7;
    int starXPos[];
    int starYPos[];
    boolean starhit[];
    int whichstarhit;
    int starradius;

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                    

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();
                    if (gameover)
                        return;
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        if (gameover)
            return;
        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    if(rocketYspeed<=10)
                        rocketYspeed+=1;
                } 
                else if (e.VK_DOWN == e.getKeyCode()) {
                    if(rocketYspeed>=-10)
                        rocketYspeed-=1;
                } 
                else if (e.VK_LEFT == e.getKeyCode()) {
                    if(rocketXspeed>=-10)
                        rocketXspeed-=1;
                } 
                else if (e.VK_RIGHT == e.getKeyCode()) {
                    if(rocketXspeed<=10)
                        rocketXspeed+=1;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    rocketexplosion=true;
                    
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                repaint();
                
                laser[Laser.current].xpos = rocketXPos;
                    laser[Laser.current].ypos = rocketYPos;
                    laser[Laser.current].active = true;
                    Laser.current++;
                     if(Laser.current>=laser.length)
                        Laser.current=0;
                     
                }
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
//Image Background
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        if(rocketexplosion)
        {
            g.setColor(Color.white);
            drawCircle(getX(bombxpos),getYNormal(bombypos),0,scalebomb,scalebomb);
        }
//draw Star        
         for (int index=0;index<numStars;index++)
        {
            if(!starhit[index])
            {
                g.setColor(Color.yellow);
                drawCircle(getX(starXPos[index]),getYNormal(starYPos[index]),0,1,1);
            }
        }
//draw Laser         
        for (int index=0;index<laser.length;index++)
        {
            if (laser[index].active)
                {
                    g.setColor((Color.blue));
                    drawCircle(getX(laser[index].xpos), getYNormal(laser[index].ypos),0, .5, .5);
                }
        }
//draw Rocket
        if(rocketXspeed>0 || rocketXspeed<0 || rocketYspeed>0 || rocketYspeed<0)
        {
            if(rocketRight)
                drawRocketRun(rocketFly,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
             else
                drawRocketRun(rocketFly,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
        else
        {
            if(rocketRight)
                drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
             else
                drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
        }
        
            
        g.setColor(Color.red);
        g.setFont(new Font("Impact",Font.ITALIC,15));
        g.drawString("Score: " + score, 20, 45);         
        g.setFont(new Font("Impact",Font.ITALIC,15));
        g.drawString("High Score: " + highscore, 315, 45);
        if(hpleft>=0)
        {
            g.setFont(new Font("Impact",Font.ITALIC,15));
            g.drawString("Lives Left: " + hpleft, 150, 45);  
        }
        else
        {
            g.setFont(new Font("Impact",Font.ITALIC,15));
            g.drawString("Lives Left: " + 0, 150, 45);  
        }
         if (gameover)
        {
            g.setColor(Color.red);
            g.setFont(new Font("Impact",Font.ITALIC,50));
            g.drawString("Game Over", 110, 250);             
        }
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);
        g.scale( 1.0/xscale,1.0/yscale);  
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocketRun(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketFly.getWidth(this);
        int height = rocketFly.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);
        g.scale( 1.0/xscale,1.0/yscale);  
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        starXPos = new int[numStars];
        starYPos = new int[numStars];
        starhit = new boolean[numStars];
        for (int index=0;index<starXPos.length;index++)
        {
            starXPos[index] = (int)(Math.random()*getWidth2());
            starYPos[index] = (int)(Math.random()*getHeight2());
            starhit[index]=false;
        }    
        gameover=false;
        rocketXspeed=0;
        rocketYspeed=0;
        rocketRight=true;
        hpleft=3;
        scalebomb=1;
        bombxpos=rocketXPos;
        bombypos=rocketYPos;
        rocketexplosion=false;
        
        Laser.current=0;
        laser= new Laser[Laser.numlaser];
        for (int index=0;index<laser.length;index++)
        {
            laser[index]= new Laser();
        }
        whichstarhit=-1;
        starradius=10;

        score=0;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketFly = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            highscore=0;
            reset();
//            bgSound = new sound("starwars.wav");
        }
//        if(bgSound.donePlaying)
//        {
//            bgSound = new sound("starwars.wav");
//        }
        if (gameover)
        {
            if (highscore < score)
                highscore = score;
            return;
        }
        for (int index=0;index<numStars;index++)
        starXPos[index]-=rocketXspeed;
        
        rocketYPos+=rocketYspeed;
        bombypos+=rocketYspeed;
        
        if(rocketYPos <= 0)
        {
            rocketYspeed=0;
            rocketYPos = 0;
        }
        if(rocketYPos >= getHeight2())
        {
            rocketYspeed=0;
            rocketYPos = getHeight2();
        }
        
        for (int index=0;index<numStars;index++)
        {
            if(starXPos[index]< getX(0))
            {
                starXPos[index] = getWidth2();
                starYPos[index] = (int)(Math.random()*getWidth2());
                starhit[index]=false;
            }
           if(starXPos[index]> getX(getWidth2()))
           { 
                starXPos[index] = getX(0);
                starYPos[index] = (int)(Math.random()*getWidth2());
                starhit[index]=false;
           }
        }
        if(rocketXspeed<=-1)
        {
            rocketRight=false;
            for (int index=0;index<laser.length;index++)
            {
                if(!laser[index].active)
                laser[index].right=false;
            }
        }
        if(rocketXspeed>=1)
        {
            rocketRight=true;
            for (int index=0;index<laser.length;index++)
                {
                if(!laser[index].active)
                    laser[index].right=true;
                }
        }
        
        for (int index=0;index<numStars;index++)
        {
                 if(rocketXPos>starXPos[index]-10 &&
                    rocketXPos<starXPos[index]+10 &&  
                    rocketYPos>starYPos[index]-10 &&
                    rocketYPos<starYPos[index]+10 && !starhit[index])
                 {
                     if(whichstarhit!=index)
                     {
                        zsound = new sound("ouch.wav");  
                        whichstarhit=index;
                        hpleft--;
                     }
                 }
                 else if (whichstarhit==index)
                     whichstarhit=-1;
            }
        
        if(hpleft<=0)
            gameover=true;
        for (int index=0;index<laser.length;index++)
        {
            if (laser[index].active)
            {
                if(laser[index].right)
                    laser[index].xpos+=7;
                else
                    laser[index].xpos-=7;
                
                if (laser[index].xpos >= getWidth2() || laser[index].xpos <= 0)
                    laser[index].active=false;
            }
        }
        for (int index=0;index<laser.length;index++)
        {
            for (int index2=0;index2<numStars;index2++)
            {
                if (laser[index].xpos>starXPos[index2]-10 &&
                    laser[index].xpos<starXPos[index2]+10 &&  
                    laser[index].ypos>starYPos[index2]-10 &&
                    laser[index].ypos<starYPos[index2]+10 && !starhit[index2] 
                    )
                {                   
                    laser[index].active = false;
                    starhit[index2]=true;
                    score++;
                 }
            }
        }
        if(rocketexplosion)
        {
            scalebomb+=1;
            if(scalebomb>=10)
            {
                rocketexplosion=false;
                scalebomb=1;
    //             if(rocketXPos+(7*scalebomb)>starXPos[index] &&
    //                rocketXPos-(7*scalebomb)<starXPos[index] &&  
    //                rocketYPos+(7*scalebomb)>starYPos[index] &&
    //                rocketYPos-(7*scalebomb)<starYPos[index] && !starhit[index] && rocketexplosion)
                   
            
                int radius = 100;
                for (int index=0;index<numStars;index++)
                {
                    double dist = Math.sqrt(((rocketXPos-starXPos[index])*(rocketXPos-starXPos[index])) + ((rocketYPos-starYPos[index])*(rocketYPos-starYPos[index])));
                    if(radius>dist)
                    {
                        starYPos[index] = (int)(Math.random()*getHeight2());
                        starXPos[index] = getWidth2();
                    }
                }
                
        }
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("laser"))
                {
                    String numLaserString = newLine.substring(6);
                    Laser.numlaser = Integer.parseInt(numLaserString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }

}

class Laser {
    public static int numlaser = 30;
    public static int current;
    public int xpos;
    public int ypos;
    public boolean active;
    public boolean right;
    Laser()
    {
            right=true;
            active=false;
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

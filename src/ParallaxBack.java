import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;


/*
This is just a cool visual trick for the background so that it looks 3d
*/
public class ParallaxBack{
	
	//1000 x 500
	public static BufferedImage stars;
	
	public static void load(){
		try{
			stars = ImageIO.read(new File("back images/stars.png"));
		}
		catch (IOException e){
			System.out.println("stars image does not exist!");
		}
	}
	
	
	public static void draw(Graphics2D g2d, int screenx, int screeny){
		
		
		double tempx = (double)screenx;
		double tempy = (double)screeny;
		
		//image is 1000x500, scrolls at a different rate than screenx, screeny (/6.5)
		int drawx = (int)(tempx/6.5) % 1000;
		int drawy = (int)(tempy/6.5) % 500;
		
		//just brute force the surrounding regions so its always covering what you can see
		
		g2d.drawImage(stars,drawx , drawy, null);
		g2d.drawImage(stars,drawx , drawy + 500, null);
		g2d.drawImage(stars,drawx , drawy - 500, null);
		
		g2d.drawImage(stars,drawx - 1000 , drawy, null);
		g2d.drawImage(stars,drawx - 1000, drawy + 500, null);
		g2d.drawImage(stars,drawx - 1000, drawy - 500, null);
		
		
		
		
		
		
		
		
	}
	
	
}

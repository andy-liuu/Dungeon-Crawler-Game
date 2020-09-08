import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;


/*
holds all utilities images like
- game over text
- heart icon
- button images (pending)


also coded to draw the hearts and score






*/
public class MenuSpriteContainer {
	
	//all of these are just seperate images so i can just use them as fields to get the image
	public static BufferedImage gameOver;
	public static BufferedImage heart;
	public static BufferedImage noheart;
	
	
	public static void load(){
		
		try{
			gameOver = ImageIO.read(new File("menu images/gameover.png"));
			heart = ImageIO.read(new File("menu images/heart.png"));
			noheart = ImageIO.read(new File("menu images/emptyheart.png"));
			
		}
		catch(IOException e){
			System.out.println("a meu image is not found!");
		}
		
		
	}
	
	public static void displayHearts(Graphics2D g, int curhealth, int maxhealth){
		
		int y = 20;
		int x = 20;
		for(int i = 0; i<maxhealth; i++){
			BufferedImage temp = i<curhealth ? heart : noheart;
			
			g.drawImage(temp,x,y,null);
			x += 42;
			
			
		}
		
	}
	
	
	
	
}

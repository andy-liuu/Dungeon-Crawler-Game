import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;

//holds all enemy sprites


/*
works the same way as playerspritecontainer
offsets are all centerd to the actual image dimentions


*/
public class EnemySpriteContainer {
	
	
	private static BufferedImage[] batImages;//batimages[i] = frame #i of animation
	private static BufferedImage[] lasereyeImages;//lasereyeImages[i] = it has rounded angle of 15i, 24 tot for 360 degs
	
	private static BufferedImage[] deathEffect;//all enemies will go thru this animation when they die
	
	public static void load(){
		
		//load and init bats
		try{
			batImages = new BufferedImage[9];
			
			for(int i = 0; i<5; i++){
				String path = "bat images/bat";
				path += "" + (i+1) + ".png";
				//System.out.println(path);
				
				batImages[i] = batImages[9-1-i] = ImageIO.read(new File(path));
				
				
			}
			
			
		}
		catch (IOException e){
			System.out.println("bat image not found!");
		}
		
		//load death images
		try{
			deathEffect = new BufferedImage[4];
			
			for(int i = 0; i<4; i++){
				String path = "death images/dead" + (i+1) + ".png";
				deathEffect[i] = ImageIO.read(new File(path));
			}
				
		}
		catch(IOException e){
			System.out.println("deadth image not found!");
		}
		
		//load and init lasereyes
		try{
			
			lasereyeImages = new BufferedImage[24];
			for(int i = 0; i<24; i++){
				
				String path = "eye images/eye" + Integer.toString(i*15) + ".png";
				lasereyeImages[i] = ImageIO.read(new File(path));
	
			}
			
		}
		catch(IOException e){
			System.out.println("laser eye image not found");
		}
		
		
		
	}
	
	//bat
	public static BufferedImage getBatImage(int frame){
		return batImages[frame];
	}
	
	public static Point getBatOffset(int frame){
		BufferedImage temp = batImages[frame];
		return new Point(temp.getWidth()/2, temp.getHeight()/2);	
	}
	
	
	//death
	public static BufferedImage getDeathImage(int frame){
		return deathEffect[frame];
	}
	
	public static Point getDeathOffset(int frame){
		BufferedImage temp = deathEffect[frame];
		return new Point(temp.getWidth()/2, temp.getHeight()/2);	
	}
	
	//lasereye
	public static BufferedImage getEyeImage(int angle){
		return lasereyeImages[angle/15];
	}
	
	public static Point getEyeOffset(int angle){
		BufferedImage temp = lasereyeImages[angle/15];
		
		//collum is 16 pix wide and 32 pix high but the eyeball shifts proportions in certain directions
		int tx = 16;
		
		//eyeball is to the left
		if (angle > 90 && angle < 270){
			tx = temp.getWidth() - 14;
		}
		
		return new Point(tx, temp.getHeight()/2);
		
	}
	
	
}

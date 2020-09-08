import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;

/*
holds all player sprites
 4 directions x 8 frames of walking/running
 4 directions x 4 frames of sword slash
 6 frames of ending cutscene for falling
 
 offsets:
  - walking and death images are perfectly centered already so you draw with the offset of the actual center of the image
  - sword images in 4 directions are not actually perfectly centered on the player
 	so load in values that is the pixel coordinate of the center of the player
 
  - all offset values avalable from get_offset();
 
*/
public class PlayerSpriteContainer {
	//[direction][frame]
	//up = 0	left = 1	down = 2	right = 3
	private static BufferedImage[][] walkImages;
	private static BufferedImage[][] swordImages;
	private static BufferedImage[] deathEffect;//[frame]
	
	
	
	
	private static int[][][] swordOffset;//[direction][frame][x or y]
	//dir, frame, offsetxy
	
	
	//types of images here
	public static final String MOVE = "MOVE";
	public static final String SWORD = "SWORD";
	
	
	//jsut a bunch of copypaste here
	public static void load(){
		//load sword offset
		try{
			swordOffset = new int[4][4][2];
			Scanner infile = new Scanner(new BufferedReader(new FileReader("offset files/sword offset.txt")));
			for(int dir = 0; dir < 4; dir++){
				for(int frame = 0; frame < 4; frame++){
					swordOffset[dir][frame][0] = infile.nextInt();
					swordOffset[dir][frame][1] = infile.nextInt();
				}
			}
		}
		catch (IOException e){
			System.out.println("file for sword offset not found!!!!");
		}
		
		//all walking/running images share
		try{
			walkImages = new BufferedImage[4][8];
			for(int dir = 0; dir < 4; dir++){
				for(int frame = 0; frame < 8; frame++){
					//make file path
					String filePath = "player sprites/walk";
					filePath += getDirLetter(dir);
					filePath += Integer.toString(frame + 1);
					filePath += ".png";
					//set image in array
					walkImages[dir][frame] = ImageIO.read(new File(filePath));
				}
			}	
		}
		catch (IOException e){
			System.out.println("walking sprite not found!");
		}
		
		
		//all sword images share
		try{
			swordImages = new BufferedImage[4][4];
			for(int dir = 0; dir < 4; dir++){
				for(int frame = 0; frame < 4; frame++){	
					//make file path
					String filePath = "sword images/sword";
					filePath += getDirLetter(dir);
					filePath += Integer.toString(frame + 1);
					filePath += ".png";	
					//set image in array
					swordImages[dir][frame] = ImageIO.read(new File(filePath));			
				}	
			}	
		}
		catch (IOException e){
			System.out.println("sword sprite not found!");
		}
		
		//all death animations
		try{
			deathEffect = new BufferedImage[6];
			for(int i = 0; i<6; i++){
				String filePath = "death images/fall" + (i+1) + ".png";
				deathEffect[i] = ImageIO.read(new File(filePath));
			}
		}
		catch (IOException e){
			System.out.println("player death image not found!");
		}
		
		
		
	}
	
	//sword and moving both here because they both use dir and frame
	public static BufferedImage getImage(String type, int dir, int frame){
		
		if (type.equals(MOVE)){
			return walkImages[dir][frame];	
		}
		
		
		if (type.equals(SWORD)){
			return swordImages[dir][frame];	
		}
		
		return null;
		
	}
	
	
	//images aren't exactly centered, so this will let drawing center on head
	//move images are fine
	
	public static Point getImageOffset(String type, int dir, int frame){
		
		if (type.equals(MOVE)){
			
			BufferedImage temp = getImage(type, dir, frame);
			return new Point(temp.getWidth()/2, temp.getHeight()/2);
		}
		
		//each sword sprite is uncentered so i counted and recorded pixels for the correct centering
		if (type.equals(SWORD)){
			return new Point(swordOffset[dir][frame][0],swordOffset[dir][frame][1] );
		}
		
		
		return null;
	}
	
	private static String getDirLetter(int d){
		if (d == 0){
			return "U";
		}
		else if (d == 1){
			return "L";
		}
		else if (d == 2){
			return "D";
		}
		else if (d == 3){
			return "R";
		}
		else{
			return "this should never happen";//will throw IOException later
		}
	}
	
	//these images are special so i make a new method for only these
	public static BufferedImage getDeathImage(int frame){
		return deathEffect[frame];
	}
	
	public static Point getDeathImageOffset(int frame){
		BufferedImage temp = deathEffect[frame];
		return new Point(temp.getWidth()/2, temp.getHeight()/2);
	}
	
	
	
	
}

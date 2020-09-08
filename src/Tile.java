import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;



/*
CLASS for an individual tile without location, the draw(g) will include location
all the images and metadata (isObstacle, doesDamage, etc.) will be in the room as static + in sprite containerd
//each tile occupies a 50x50 space but wall pictures are bigger to simulate 2.5D
*/
public class Tile {
	
	private String type;//can ONLY be "WALL" or "FLOOR"
	private boolean isObstacle;
	private boolean doesDamage;
	
	private BufferedImage back;
	
	
	//all images will be static and pre-loaded here
	
	
	
	public Tile(String filePath, String type, boolean isObstacle, boolean doesDamage){
		
		try{
			this.back = ImageIO.read(new File(filePath));
			this.type = type;
			this.isObstacle = isObstacle;
			this.doesDamage = doesDamage;	
		}
		catch(IOException e){
			System.out.println(filePath + " does not exist!");
		}
		
			
	}
	
	
	
	public void draw(Graphics2D g2d, int x, int y){
		
		if (type.equals("FLOOR")){
			g2d.drawImage(back, x,y,null);
		}
		else if (type.equals("WALL")){
			g2d.drawImage(back, x, y-31, null);
			//all walls have a 2.5D illusion so need to draw it offset by a bit
			//all images are same size
		}
		
		
		
	}
	
	
	
	
	public String getType(){
		return type;
	}
	
	public boolean getObstacle(){
		return isObstacle;
	}
	
	public boolean getDamage(){
		return doesDamage;
	}
	
	
	
	
	
	
}

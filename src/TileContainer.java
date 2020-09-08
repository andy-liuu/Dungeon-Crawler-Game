import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;


/*
Since there are alot of tiles with very repetetive implementations then I put all the tiles in a tileContainer
and can use a getTile(int magicnum) to get a tile from an array
 */
public class TileContainer {
	

	public static void load(){
		allTiles = new Tile[16];
		blueFloor = new Tile("tile images/blue-floor-min.png","FLOOR", false, false );
		blueWall = new Tile("tile images/blue-wall-min.png", "WALL", true, false);
		boneWall = new Tile("tile images/bone-wall-min.png", "WALL", true, false);
		darkFloor = new Tile("tile images/dark-floor-min.png","FLOOR", false, false );
		goldFloor = new Tile("tile images/gold-floor-min.png","FLOOR", false, false );
		goldWall = new Tile("tile images/gold-wall-min.png", "WALL", true, false);
		grassWall = new Tile("tile images/grass-wall-min.png", "WALL", true, false);
		hotFloor = new Tile("tile images/hot-floor-min.png", "FLOOR", false, true);//does damage
		iceFloor = new Tile("tile images/ice-floor-min.png","FLOOR", false, false );
		iceWall = new Tile("tile images/ice-wall-min.png", "WALL", true, false);
		oozeFloor = new Tile("tile images/ooze-floor-min.png","FLOOR", false, false );
		pinkFloor = new Tile("tile images/pink-floor-min.png","FLOOR", false, false );
		pinkWall = new Tile("tile images/pink-wall-min.png","WALL", true, false );
		rockFloor = new Tile("tile images/rock-floor-min.png","FLOOR", false, false );
		rockWall = new Tile("tile images/rock-wall-min.png", "WALL", true, false);
		allTiles[1] = blueFloor;
		allTiles[2] = blueWall;
		allTiles[3] = boneWall;
		allTiles[4] = darkFloor;
		allTiles[5] = goldFloor;
		allTiles[6] = goldWall;
		allTiles[7] = grassWall;
		allTiles[8] = hotFloor;
		allTiles[9] = iceFloor;
		allTiles[10] = iceWall;
		allTiles[11] = oozeFloor;
		allTiles[12] = pinkFloor;
		allTiles[13] = pinkWall;
		allTiles[14] = rockFloor;
		allTiles[15] = rockWall;		
	}
	
	//array with all the tiles
	private static Tile[] allTiles;
	
	public static Tile get(int magicNum){
		//code for no tile here
		if (magicNum == 0){
			return null;
		}
		//out of range = no tile
		else if (magicNum > 15){
			return null;
		}
		else{
			return allTiles[magicNum];
		}
	}
	
	
	/*
	All images used + the tiles that correspond thanks CtrlC+CtrlV
	all static in jsut cuz i dont wanna reload all these images in case i make more than one tileContainer
	*/
	
	
	private static Tile blueFloor;	
	private static Tile blueWall;		
	private static Tile boneWall;		
	private static Tile darkFloor;		
	private static Tile goldFloor;		
	private static Tile goldWall;		
	private static Tile grassWall;		
	private static Tile hotFloor;	
	private static Tile iceFloor;		
	private static Tile iceWall;		
	private static Tile oozeFloor;		
	private static Tile pinkFloor;			
	private static Tile pinkWall;		
	private static Tile rockFloor;		
	private static Tile rockWall;
	
	
	
	
	
	
	
	
	
	
}

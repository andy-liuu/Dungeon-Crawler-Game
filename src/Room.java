import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;


/*
Class that:
- holds tileset data (collision with tiles, position) but not the actual tiles themselves
	- with access to collision data thru checkCollision methods
- draws all the floor tiles/all the wall tiles seperately

*/
public class Room {
	
	
	/*
	ALL MAGIC NUMBS/IDs for the tiles in tileocntainer
	i think i have to do something like this in order for the data file to contain only numbers
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
	*/
	
	//ID's of tiles that are walls
	private ArrayList<Integer> numsThatCollide = new ArrayList<Integer>(Arrays.asList(0,2,3,6,7,10,13,15));

	public Room(String fileName){
		
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName)));
			//first 2 lines are the #tiles for width, then height
			roomTileWidth = inFile.nextInt();
			roomTileHeight = inFile.nextInt();
			
			roomWidth = roomTileWidth * 50;
			roomHeight = roomTileHeight * 50;
			
			//scanGraphics();
				
			//initialize grid and get inputs
			tileGrid = new int[roomTileHeight][roomTileWidth];
			collisionGrid = new boolean[roomTileHeight][roomTileWidth];
			
			for(int i = 0; i<roomTileHeight; i++){
				for(int j = 0; j<roomTileWidth; j++){
					int temp = inFile.nextInt();
					
					//set up the data grids
					tileGrid[i][j] = temp;
					collisionGrid[i][j] = numsThatCollide.contains(temp);
					
				
						
				}
			}
			
			
		}
		catch (IOException e){
			System.out.println(e);
		}		
	}
	
	
	
	
	
	
	
	
	private int roomWidth;
	private int roomHeight;
	
	
	public int getRoomWidth(){
		return roomWidth;
	}
	public int getRoomHeight(){
		return roomHeight;
	}
	
	
	private int roomTileWidth;
	private int roomTileHeight;
	
	private int[][] tileGrid;//[height][width], tileGrid[a][b] --> the tile TileContainer.get(this number) at spot a*50,b*50
	private boolean[][] collisionGrid; //makes searchup of collision faster
	
	
	
	//checks for collision for a new intended position
	public boolean checkCollision(double x, double y, int halfsize, String type){
		
		//if one of the 4 corners have a collide then the entire position is invalid
		
		//only 2 things 
		if (type.equals("PLAYER")){
			return (collideHelper(x-halfsize,y-halfsize) 
				|| collideHelper(x+halfsize,y-halfsize) 
					|| collideHelper(x-halfsize,y+halfsize) 
						|| collideHelper(x+halfsize,y+halfsize));
		}
		if (type.equals("LASER")){
			return (collideLaserHelper(x-halfsize,y-halfsize) 
				|| collideLaserHelper(x+halfsize,y-halfsize) 
					|| collideLaserHelper(x-halfsize,y+halfsize) 
						|| collideLaserHelper(x+halfsize,y+halfsize));
		}
		
		return false;//default in case type is wierd
		
		
	}
	
	
	//overloaded checkCollision without special string type will jsut go with defaulf 
	public boolean checkCollision(double x, double y, int halfsize){
		return checkCollision(x, y, halfsize, "PLAYER");
	}
	
	
	
	//checks collision with only one point, true is collides, false if clear
	private boolean collideHelper(double x, double y){
		//(int) rounds down, which is fine for knowing which box were in (on the array of tiles)
		int tileX = (int)x/50;
		int tileY = (int)y/50;
		
		
		//out of bounds = collide
		if (x < 0 || x >= roomWidth || y < 0 || y >= roomHeight){
			return true;
		}
		//no tile = collide
		else if (tileGrid[tileY][tileX] == 0){
			return true;
		}
		//check the tile's collisiongrids
		else{
			return (boolean)collisionGrid[tileY][tileX];
		}
	}
	
	//same as other helper but lasers can go over gaps
	private boolean collideLaserHelper(double x, double y){
		
		int tileX = (int)x/50;
		int tileY = (int)y/50;
		
		
		//out of bounds = no collide
		if (x < 0 || x >= roomWidth || y < 0 || y >= roomHeight){
			return false;
		}
		//no tile = no collide
		else if (tileGrid[tileY][tileX] == 0){
			return false;
		}
		//check the tile's collisiongrids
		else{
			return (boolean)collisionGrid[tileY][tileX];
		}
	}
	
	

	//seperate floor draw and wall draw so that Player can "be behind walls"
	
	public void drawFloor(Graphics2D g, int screenx,int screeny){
		
		//calculate range of tiles needed
		//screen is 800x600 aka 16 tiles x 12 tiles, go one above and one below cuz walls "peek" over other tiles
		int arrX = Math.max(screenx/50 - 1,0);
		int arrXLast = Math.min(arrX + 17, roomTileWidth - 1);
		
		int arrY = Math.max(screeny/50-1, 0);
		int arrYLast = Math.min(arrY + 13, roomTileHeight - 1);
		
		
		//gridline color
		g.setColor(new Color(255,255,255, 200));
		
		//loop thru visible range and draw the things
		for (int i = arrX; i <= arrXLast ; i++){
			for(int j = arrY; j <= arrYLast; j++ ){
				
				boolean isWall = collisionGrid[j][i];
				
				//its a floor if no collision
				if (!isWall){
					int ID = tileGrid[j][i];
					Tile currentDraw = TileContainer.get(ID);
					
					//null if there is no tile aka its a gap, so dont draw anything
					if (currentDraw != null){	
						currentDraw.draw(g, i*50 - screenx, j*50 - screeny);//tile
						g.drawRect(i*50 - screenx, j*50 - screeny, 50, 50);//gridline
								
					}	
				}
				
				
			}
		}
		
		
	}
	
	
	//same as drawfloor but instead only draws walls with collision
	public void drawWall(Graphics2D g, int screenx,int screeny){
		int arrX = Math.max(screenx/50 - 1,0);
		int arrXLast = Math.min(arrX + 17, roomTileWidth - 1);	
		int arrY = Math.max(screeny/50-1, 0);
		int arrYLast = Math.min(arrX + 13, roomTileHeight - 1);	
		g.setColor(new Color(255,255,255, 200));
		for (int i = arrX; i <= arrXLast ; i++){
			for(int j = arrY; j <= arrYLast; j++ ){
				
				
				boolean isWall = collisionGrid[j][i];
				//its wall if there is collision
				if (isWall){
					int ID = tileGrid[j][i];
					Tile currentDraw = TileContainer.get(ID);
					
					if (currentDraw != null){
						currentDraw.draw(g, i*50 - screenx, j*50 - screeny);
								
					}	
				}				
			}
		}				
	}
	
	
	
	
	
	
	
}

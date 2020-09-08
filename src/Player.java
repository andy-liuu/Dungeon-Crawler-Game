import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;
import java.math.*;

/*the main character, has stuff like:
walking
charging by duoble tapping WASD key
sword attack by clicking in direction

player will have a hitbox of 30x30 pixels


*/
public class Player {
	
	//where you are facing
	private int direction;
	public static final int UP = 0; //W
	public static final int LEFT = 1; //A
	public static final int DOWN = 2; //S
	public static final int RIGHT = 3; //D
	
	public Player(int x, int y, int direction, int hp){
		isAlive = true;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.hp = hp;
		hurtbox = new Rectangle2D.Double(x-15,y-15,30,30);
	}
	
	//location
	private double x;
	private double y;
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	
	
	//health and are you living
	//0hp != not living, because there should also be a death animation before gameover screen
	private int hp;
	private boolean isAlive;
	public int getHP(){
		return hp;
	}
	public boolean getLiving(){
		return isAlive;
	}
	
	
	private int timeSinceLastDamage = 100;//max time is 100 only for safety of integer overflow
	private final int intangebleTime = 50;//# ticks that damageMe doesn't do anything
	
	
	public int getTimeSinceLastDamage(){
		return timeSinceLastDamage;
	}
	
	public void updateCounters(){
		timeSinceLastDamage++;
		timeSinceLastDamage = Math.min(100, timeSinceLastDamage);
	}
	
	//only "tries" to damage you but intangibility is factored here
	public void damageMe(int dmg){
		
		if (timeSinceLastDamage > intangebleTime){
			//subtract but not less than 0
			hp = Math.max(0, hp - dmg);
			timeSinceLastDamage = 0;		
		}	
	}
	
	
	
	//hitbox/hurtboxes (use rectangle class for its .intersects(Rectangle2D) with other entities)
	private Rectangle2D.Double hurtbox;
	public Rectangle2D.Double getHurtbox(){
		return hurtbox;
	}
	
	private Rectangle2D.Double swordbox = null;
	//sometimes returns null if no sword action exists
	public Rectangle2D.Double getSwordbox(){
		return swordbox;
	}
	
	
	
	//current room (needed for collisions)
	private Room currentRoom;
	//call this just after you create new player
	public void setRoom(Room currentRoom){
		this.currentRoom = currentRoom;
	}
	
	
	
	
	//gateway to do either attack or move 
	//--> passed in all inputs here, seperates inputs into whatever's needed for each specific action
	public void doAction(boolean[] keys, boolean[] isDoublePressed, boolean[] mouseHeld, boolean[] mouseFirstReleased, int mx, int my, int screenx, int screeny){

		
		//right click start to sword slash
		if (mouseFirstReleased[1] && !isAttacking){
			startAttackAction(mouseHeld, mouseFirstReleased, mx, my, screenx, screeny);
		
			//actual hitbox only lasts 1 frame even tho animation is longer
			resetSwordbox();
		}
		//cannot move when attack animation is going on
		else if (isAttacking){
			continueAttackAction();
			swordbox = null;
			
		}
		//last case scenario is move, all other inputs take priority
		else{
			doMovementAction(keys, isDoublePressed);
		}
		
		
		
		
	}
	
	/*					All swording							*/

	
	private boolean isAttacking = false;//true if animation currentlt playing
	
	private void startAttackAction(boolean[] mouseHeld, boolean[] mouseFirstReleased, int mx, int my, int sx, int sy){
		
		//determine new direction based on mouse
		//mx asnd my are in screen coordiates
		
		//find angle between player's relative screen position, and mouse pos,
		
		double ang = Math.toDegrees(Math.atan2(my - (y - sy), mx - (x - sx)));
		ang = (ang + 360.0)%360.0;//always get angle 0<=ang<=360
		
		/*due to y axis pointing down, 
		UP = 270
		LEFT = 180
		DOWN = 90
		RIGHT = 0/360
		*/
			
		//change direction based on angle of mouse (also determines what side the swordbox is on
		
		if (ang < 45.0 || ang > 315.0){
			direction = RIGHT;
		}
		else if (45<=ang && ang<=135){
			direction = DOWN;
		}
		else if (135 < ang && ang< 225){
			direction = LEFT;
		}
		else if (225 <= ang && ang <= 315){
			direction = UP;
		}
		
		isAttacking = true;
		
	
	}
	
	//continues the sword animation and stops u from moving for the time elapsed by incrementSwordFrame
	private void continueAttackAction(){
		incrementSwordFrame();
		
		//back to where u started aka looped thru the entier counter
		if (swordframe == 0 && swordbufferframe == 0){
			isAttacking = false;
		}
		
		
	}
	
	//used when on first frame of attack, subsequent frames this goes back to null
	private final double swordH = 35.0;//forward distance from x,y that the sword reaches
	private final double swordW = 25.0;//sideways distance
	
	private void resetSwordbox(){
		
		if (direction == UP){
			swordbox = new Rectangle2D.Double(x - swordW - 15, y - swordH - 15, 2*swordW + 30, swordH);
		}
		
		if (direction == LEFT){
			swordbox = new Rectangle2D.Double(x - swordH - 15, y - swordW - 15, swordH, 2*swordW + 30);
		}
		
		if (direction == DOWN){
			swordbox = new Rectangle2D.Double(x - swordW - 15, y + 15, 2*swordW + 30, swordH);
		}
		
		if (direction == RIGHT){
			swordbox = new Rectangle2D.Double(x + 15, y - swordW - 15, swordH, 2*swordW + 30);
		}
		
		
	}
	
	
	
	
	/*					All movement							*/
	
	/*gateway to different types of movement:
	- dash/charge (doublepress)
	- walk (no doublepress)
	
	*/
	
	private void doMovementAction(boolean[] keys, boolean[] isDoublePressed){
		
		double oldX, oldY;
		
		oldX = x;
		oldY = y;
		
		
		determineChargingState(isDoublePressed);
		
		//you are already charging or have started charging
		if (isCharging){
			charge(keys);
		}
	
		//you are walking
		else if (!isCharging && (keys[getCode('W')] || keys[getCode('A')] || keys[getCode('S')] || keys[getCode('D')] )){
			walk(keys);
		}
		
		
		//you actually moved
		if (oldX != x || oldY != y){
			//only updte frame (for sprite) if you moved
			incrementMoveFrame();
			
			//update hurtbox
			hurtbox.setRect(x-15,y-15,30,30);
		}
		
	
	}
	
	
	
	/*
	WHAT I WANT DASHING TO BE
	-double tap a direction to start dashing, but you can only move in that one direction(not diagonally)
	*/
	private boolean isCharging = false;
	
	//updates isCharging based on your inputs (double presses always take priority with movement)
	private void determineChargingState(boolean[] isDoublePressed){
		
		if (isDoublePressed[getCode('W')]){
			isCharging = true;
			direction = UP;
		}
		else if (isDoublePressed[getCode('D')]){
			isCharging = true;
			direction = RIGHT;
		}
		else if (isDoublePressed[getCode('S')]){
			isCharging = true;
			direction = DOWN;
		}
		else if (isDoublePressed[getCode('A')]){
			isCharging = true;
			direction = LEFT;
		}
		else{
			isCharging = false;
		}
		
		
	}
	
	
	//actually dash fast, but only in one direction
	private void charge(boolean[] keys){
		
		double changeX = 0;
		double changeY = 0;
		
		if (direction == UP && keys[getCode('W')]){
			changeY -= 4;
		}
		else if (direction == DOWN && keys[getCode('S')]){
			changeY += 4;
		}
		else if (direction == RIGHT && keys[getCode('D')]){
			changeX += 4;
		}
		else if (direction == LEFT && keys[getCode('A')]){
			changeX -= 4;
		}
		else{
			isCharging = false;
		}
		
		//actually a wierd solution to charging at a wall and always going as close as possible
		//checks incrememnts of 100%, 75%, 50%, 25% of changeX, changeY to see which one works first
		//doesn't affect changeX or changeY if they are 0
		//doest change sign of changeX, changeY
		
		//checks collision as well

		//no obstruction whatsoever fully
		//increment to "hug" the wall because you go 4px at a time, so there might be 3px space
		if (! currentRoom.checkCollision(x + changeX, y + changeY, 15, "PLAYER")){
			x += changeX;
			y += changeY;		
		}
		//4 * 0.75 = 3
		else if (! currentRoom.checkCollision(x + 0.75 * changeX, y + 0.75 * changeY, 15, "PLAYER")){
			x += 0.75 * changeX;
			y += 0.75 * changeY;
		}
		//4 * 0.5 = 2
		else if (! currentRoom.checkCollision(x + 0.5 * changeX, y + 0.5 * changeY, 15, "PLAYER")){
			x += 0.5 * changeX;
			y += 0.5 * changeY;
		}
		//4 * 0.25 = 1
		else if (! currentRoom.checkCollision(x + 0.25 * changeX, y + 0.25 * changeY, 15, "PLAYER")){
			x += 0.25 * changeX;
			y += 0.25 * changeY;
		}
		//bonk on wall
		else{
			
			isCharging = false;
			
		}
			
		
	}
		
	
	

	/*
	WHAT I WANT WALKING TO BE
	-kinda like normal walking, you can turn instantly, direction gets updated whenever for sprite and attack purposes
	-if you click your own directiona and another perpendicular, you keep your direction but move diagonally
	-ill decide later what happens when you click opposite directions
	*/
	private void changeDirs( double changeX, double changeY){
		if (changeX > 0){
			direction = RIGHT;
		}
		else if (changeX < 0){
			direction = LEFT;
		}
		else if (changeY > 0){
			direction = DOWN;
		}
		else if (changeY < 0){
			direction = UP;
		}
	}
	
	private void walk(boolean[] keys){
		
		//buttons clicked
		boolean clickUp = keys[getCode('W')];
		boolean clickRight = keys[getCode('D')];
		boolean clickDown = keys[getCode('S')];
		boolean clickLeft = keys[getCode('A')];
		
		boolean[] allClicked = {clickUp, clickLeft, clickDown, clickRight};//indexes are UP,Right, D, L
		

		//do xy shifts first, then determine which direciton you are facing
		//here to compare so if you just dont move at all your direction downs;t change
		//maximum "diagonal walk" is 45 degrees here
		//when opposite directions are clicked then up and right are prioritized
		double changeX = 0, changeY = 0;
		
		if (clickUp){
			changeY -= 2;		
		}
		else if (clickDown){
			changeY += 2;
		}
		
		if (clickRight){
			changeX += 2;
		}
		else if (clickLeft){
			changeX -= 2;
		}
		
		
		//if you move diagonally then you cant keep the same cx,cy cux hypot would be longer
		if (changeX != 0 && changeY != 0){
			changeX *= 0.8;
			changeY *= 0.8;
		}
		
		
		
		//maybe you have to refresh the direction, maybe not
		int prevDirection = direction;
		direction = -1;
		
		//you are still moving in the direction you were previusly moving
		//OR didn't move
		if (allClicked[prevDirection] || (changeX == 0 && changeY == 0)){
			direction = prevDirection;
		}
		//you changed direction based on your new intended position
		//order of priority here
		else{
			changeDirs(changeX, changeY);
			
			
		}
		
		
		//this is where i would check for collisions and not move if theres a collision
		//but i need a room opbject with the blocks/tiles first
		
		//how this works:
		//if you can move with both x and y then do it
		//otherwise if only moving with the x or only with y works, then do that instead
		//arbitrarily prioritize x over y because screen is wider than tall
		
		//no need to perfectly "hug" the wall because sprites are pretty rough for collision anyways
		//and you go pretty slow, max is a space of 1px that you can't get to a wall closer

		
		//no obstruction whatsoever
		if (! currentRoom.checkCollision(x + changeX, y + changeY, 15, "PLAYER")){
			x += changeX;
			y += changeY;		
		}
		//vert is obstructed but not y
		else if (! currentRoom.checkCollision(x + changeX, y, 15, "PLAYER")){
			x += changeX;
		}
		//hor "" "" "" "" x
		else if (! currentRoom.checkCollision(x, y + changeY, 15, "PLAYER")){
			y += changeY;
		}
		
		

	
	}
	
	








	/*						Drawing								*/

	//all pictures are static in PlayerSpriteContainer
	
	
	//these frame counters work jsut like all the other frame counters in gamepanel, bat, lasereye
	
	private int moveframe = 0;
	private int movebufferframe = 0;
	private final int movetime = 10;//number of actionPerformed calls between frame change	
	private void incrementMoveFrame(){
		movebufferframe++;
		movebufferframe = movebufferframe%movetime;	
		if (movebufferframe == 0){
			moveframe++;
			moveframe = moveframe%8;	
		}
		
	}
	
	
	
	private int swordframe = 0;
	private int swordbufferframe = 0;
	private final int swordtime = 5;
	private void incrementSwordFrame(){
		swordbufferframe++;
		swordbufferframe = swordbufferframe%swordtime;
		if (swordbufferframe == 0){
			swordframe++;
			swordframe = swordframe%4;	
		}
		
	}

	public void draw(Graphics2D g, int screenx, int screeny){
		
		//get type of image (just makes it easier for PlayerSpriteContainer to know what I want)
		String imageType;
		int frame;
		if (isAttacking){
			imageType = PlayerSpriteContainer.SWORD;
			frame = swordframe;
		}
		else{
			imageType = PlayerSpriteContainer.MOVE;
			frame = moveframe;
		}
		
		//draw the image according to the offset values to center it
		BufferedImage temp = PlayerSpriteContainer.getImage(imageType, direction, frame);
		Point offset = PlayerSpriteContainer.getImageOffset(imageType, direction, frame);
		g.drawImage(temp, (int)x - (int)offset.getX() - screenx, (int)y - (int)offset.getY() - screeny, null);
		
		
		/*
		//right now a visualization for the guy's collisionbox
		g.setColor(new Color (255,0,0));
		g.drawRect((int)x - screenx - 15,(int)y - screeny - 15,30,30);
		
		//testing aggro range for bats
		g.setColor(new Color (255,0,255));
		g.drawOval((int)x - screenx - 300,(int)y - screeny - 300,600,600);
		*/
		
		
		
		
	}
	
	
	/*IMPORTANT RESEARCH*/
	//keycodes for letters 'A' to 'Z' are same as ascii codes for those letters
	//ONLY for capital letters and space bar
	//used to interpret the boolean[] keys and boolean[] isDoublePressed easier
	private int getCode(char letter){
		return (int)Character.toUpperCase(letter);
	}
	
	
	
	
	
	
	
	
	
	
	
}

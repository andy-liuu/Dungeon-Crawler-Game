import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.util.*;

/*
This is the actual game loop

Functionality:
KeyListener --> boolean[] keys, boolean[] isDoublePressed (true if you released then pressed a key within a short time)
MouseListener --> boolean[] mouseHeld, boolean[] mouseFirstReleased (true for 1 timer tick after you release mouse aka value in mouseHeld goes from tru to false
MouseMotionListener --> int mousex, mousey (faster than MouseInfo.getPointerInfo())

these arrays and ints all get passes into Player's .doAction() then the logic for that is done in player class

Enemies:
bat --> speeds up towards you if you are in certain range(with velocity and accellaration), not affected by walls, knockback if hit,
lasereye --> stationary turret that shoots laser balls if you are in range, lasers affected by wall collision but can go over gaps

all enemies contained in arraylists, next level will happen when both arraylists .isEmpty()







*/
public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener{
	
	private zeldaFSE mainFrame;
	
	
	
	
	
	public GamePanel(zeldaFSE mainFrame){
		
		//include the overarch class
		this.mainFrame = mainFrame;
		
		
		
		setSize(800,600);
		setDoubleBuffered(true);
		RepaintManager.currentManager(mainFrame).setDoubleBufferingEnabled(true);
		
		
		
		
		//init key data structs
		keys = new boolean[KeyEvent.KEY_LAST+1];//keys[KeyEvent.code] is true means its pressed
		timeSinceLastPress = new int[KeyEvent.KEY_LAST + 1];
		Arrays.fill(timeSinceLastPress,200);//all values to 20 aka too big for a double press
		isDoublePressed = new boolean[KeyEvent.KEY_LAST + 1];
		
		
		//init mouse data structs
		mouseHeld = new boolean[4];
		Arrays.fill(mouseHeld,false);
		mouseFirstReleased = new boolean[4];
		Arrays.fill(mouseFirstReleased,false);
		
		//enemy arraylist inits
		allBats = new ArrayList<Bat>();
		allEyes = new ArrayList<LaserEye>();
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
					
	}
	
	public void addNotify(){
		super.addNotify();
		requestFocus();
		mainFrame.start();
	}
	
	
	

	
	/*						Game mechanics			*/
	
	
	
	
	
	/*			enemies containers						*/
	private ArrayList<Bat> allBats;
	
	//get text file and put the data into allBats
	private void loadBats(String filepath){
		try{
			allBats.clear();
			Scanner infile = new Scanner(new BufferedReader(new FileReader(filepath)));
			int n = infile.nextInt();
			for(int qwe = 0; qwe<n; qwe++){
				double x,y;
				int hp;
				x = infile.nextDouble();
				y = infile.nextDouble();
				hp = infile.nextInt();
				allBats.add(new Bat(x,y,hp));
			}
			

		}
		catch (IOException e){
			System.out.println(filepath + " not found");
		}
		
		
	}
	
	private ArrayList<LaserEye> allEyes;
	//same as loadbats
	private void loadEyes(String filepath, Room c){
		LaserEye.setRoom(c);
		
		try{
			allEyes.clear();
			Scanner infile = new Scanner(new BufferedReader(new FileReader(filepath)));
			int n = infile.nextInt();
			for(int qwe = 0; qwe<n; qwe++){
				double x,y;
				int hp;
				x = infile.nextDouble();
				y = infile.nextDouble();
				hp = infile.nextInt();
				allEyes.add(new LaserEye(x,y,hp));
			}
		}
		catch(IOException e){
			System.out.println(filepath + " not found");
		}
	}
	
	
	//just packages the 2 enemy action methods, makes gamepanel.move() less cluttered
	private void doEnemyAction(){
		Rectangle2D.Double sword = Link.getSwordbox();//can be null if no sword is clicked yet
		
		doBatAction(sword);
		doEyeAction(sword);

		
		
	}
	
	//bat movement + hit the bat + bat hits you
	//collision done with built in Rectangle2D methods
	private void doBatAction(Rectangle2D.Double hitbox){
		
		for(int i = 0; i<allBats.size(); i++){
			
			
			//only if alive (sometimes it is "dead" but still playing its death animation aka not removed yet
			Bat temp = allBats.get(i);
			if (temp.getHP() != 0){
				
				//move
				temp.moveTowards(Link.getX(), Link.getY());
				temp.adjustPos(test.getRoomWidth(), test.getRoomHeight());
				
				//hit the bat
				if (hitbox != null){
					if (hitbox.intersects(temp.getHurtbox())){
						temp.getHit(1,Link.getX(), Link.getY());
					}
				}
				
				//bat hits you
				if (temp.getHurtbox().contains(Link.getX(), Link.getY())){
					
					Link.damageMe(1);
				}
				
			}
		}
		
	}
	
	//adjust angle on eye + try shooting laser (there is a timer and range restriction) + move current lasers
	private void doEyeAction(Rectangle2D.Double hitbox){
		
		for(int i = 0; i<allEyes.size(); i++){
			LaserEye temp = allEyes.get(i);
			if (temp.getHP() != 0){
				
				//adjust angle
				temp.adjustAngle(Link.getX(), Link.getY());
				
				//try to make a new laser + continue laser path
				temp.tryCreateLaser(Link.getX(), Link.getY());
				temp.adjustLasers();
				
				//hit the eye
				if (hitbox != null){
					if (hitbox.intersects(temp.getHurtbox())){
						temp.getHit(1);
					}
				}
				
				//eye/laser hits you
				if (temp.collidesWith(Link.getHurtbox())){
					Link.damageMe(1);
				}
				
				
				
			}
		}
		
		
	}
	
	/////////STARTING VALUES//////////
	
	//levels system
	private int currentLevel = 0;
	private final int MAXLEVEL = 3;
	private void incrementLevel(){
		currentLevel++;
		currentLevel = Math.min(currentLevel, MAXLEVEL);
	}
	
	//prefixes for rooms, bats, lasereyes
	private final String ROOMPRE = "levels/level";
	private final String BATPRE =  "enemy files/bat";
	private final String EYEPRE = "enemy files/eye";
	
	//starting locations for player based on location
	//[level][x or y]
	private final int[][] PLAYERSTART = new int[][]{{200,300}, {275, 500}, {625, 625}, {375, 375}};
	
	
	
	
	
	
	private boolean firstRoomLoad = true;
	
	//these are starting values for level 1, will be changed at every FirstRoomLoad
	private Player Link = new Player(500,500,Player.UP, 10);;
	private Room test = new Room("levels/test.txt");;
	
	//screen manipulaiton
	private int screenx = (int)Link.getX() - 500;
	private int screeny = (int)Link.getY() - 375;
	
	
	//screen position follows Link's global position, but loosely within a box so its not always centered
	public void updateScreenPos(){
		
		//px distance between link screen position and (0,0) screen position
		int diffX = (int)Link.getX() - screenx;
		int diffY = (int)Link.getY() - screeny;
		
		
		//update screenx, screeny if he's about to leave the "box" that he can be in 
		//box is (320, 240, 160, 120) 
		
		//link is too far up
		if (diffY < 240){
			screeny = (int)Link.getY() - 240;
		}
		//link is too far down
		else if (diffY > 360){
			screeny = (int)Link.getY() - 360;
		}
		
		
		if (diffX < 320){
			screenx = (int)Link.getX() - 320;
		}
		//link is too far down
		else if (diffX > 480){
			screenx = (int)Link.getX() - 480;
		}
		
	
	}
	
	//works with all the global variables + packages all the code for getting new people here
	private void resetLevel(){
		firstRoomLoad = false;
				
		//file strings and values according to level
		String nextRoom = ROOMPRE + currentLevel + ".txt";
		String nextBat = BATPRE + currentLevel + ".txt";
		String nextEye = EYEPRE + currentLevel + ".txt";
		int nextX = PLAYERSTART[currentLevel][0];
		int nextY = PLAYERSTART[currentLevel][1];
		
		
		//reset room
		test = new Room(nextRoom);
		
		
		//reset all player stuff
		int oldHP = Link.getHP();
		Link = new Player(nextX,nextY,Player.UP, oldHP);
		Link.setRoom(test);	
		screenx = (int)Link.getX() - 500;
		screeny = (int)Link.getY() - 375;
		
		//load enemies
		loadBats(nextBat);
		loadEyes(nextEye, test);	
	}
	
	
	
	/*actual game loop seperated into 
	  1) you are not dead
	  - move enemies, you, handle interactions, update screen coords, etc
	  2) you are dead
	  - play cutscene and listen for click to return to start menu
	*/
	public void move(){
		
		//not dead yet
		
		if (Link.getHP() != 0){
			
			//load and init everything that is needed
			if (firstRoomLoad){
				resetLevel();
			}
			
			//remove all dead bats (after their death animation)
			for (int i = allBats.size() - 1; i>=0; i--){
				if (!allBats.get(i).getLiving()){
					allBats.remove(i);
				}
			}
			
			//remove all dead eyes (after their death animation)
			for (int i = allEyes.size() - 1; i>=0; i--){
				if (!allEyes.get(i).getLiving()){
					allEyes.remove(i);
				}
			}
			
			//your action (pass in all the values you need, then seperate for logic later
			Link.updateCounters();
			Link.doAction(keys,isDoublePressed, mouseHeld,mouseFirstReleased, mousex, mousey, screenx, screeny);
			
			//not your actions
			doEnemyAction();
			
			//update functionality
			updateScreenPos();
			incrementKeyCounters();
			resetMouseFirstReleased();
			
			
			//this is where a transition to the next level would be
			if (allBats.isEmpty() && allEyes.isEmpty()){
				firstRoomLoad = true;
				incrementLevel();
			}
		}
		//haha u died
		else{
			
			
			incrementDeadFrame();
			
			//i want to show the entire death animation before you can click to leave
			if (deathFinished){

				//click to exit program (might want to push you back into main menu again if possible
				if (mouseHeld[1] || mouseHeld[2] || mouseHeld[3]){
					StartMenu.main(new String[0]);
					mainFrame.kill();
					System.out.println("dead");
						
			}
			}
			
		}
		
			
	}
	
	
	//death animation counters
	//6 images in death animation
	private int deadframe = 0;
	private int deadbufferframe = 0;//#timer ticks between each frame increment 
	private final int deadtime = 20;//max # for bufferframe counter
	
	private boolean deathFinished = false;//true if the entire loop follows thru
	
	private void incrementDeadFrame(){
		deadbufferframe++;
		deadbufferframe = deadbufferframe%deadtime;
		if (deadbufferframe == 0){//went thru entire loop
			deadframe++;
			deadframe = Math.min(deadframe, 5);//safety net (only 6 frames in death animarion)

		}
		if (deadframe == 5 && deadbufferframe == deadtime - 1){//last values before it resets
			deathFinished = true;
		}
	}
	
	
	@Override
	/*
	drawing also split into you are dead and you are not
	1) not dead
	- just call .draw(g, screenx, screeny) on all objects, and drawFloor, drawWall, and #hearts and such
	2) dead
	- draw game over screen and death animation
	*/
	
	public void paintComponent(Graphics g){
		
		Graphics2D g2d = (Graphics2D) g;//typecast for more functionality (eg fonts)
		
		
		//not dead
		if (Link.getHP() != 0){

			//this clears screen + draws paralax background
			ParallaxBack.draw(g2d, screenx, screeny);
			
	
			//draw floor, player on floor (+ enemies when they come), walls in front of player
			//lets you "hide" behind walls but also your head can look like its under the wall sometimes aka ok tradeoff
			
			
			test.drawFloor(g2d,screenx,screeny);
			Link.draw(g2d, screenx, screeny);
			test.drawWall(g2d,screenx,screeny);
			
			
			//draw all bats
			for (Bat temp : allBats){
				temp.draw(g2d,screenx, screeny);
			}
			
			//draw all eyes
			for( LaserEye temp : allEyes){
				temp.draw(g2d, screenx, screeny);
			}
			
			
			
			
			//display hearts
			MenuSpriteContainer.displayHearts(g2d, Link.getHP(), 10);
			
			//display level
			String levtext = "Level " + (currentLevel+1) + "/" + (MAXLEVEL + 1);
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font("Serif", Font.BOLD, 20));
			g2d.drawString(levtext, 650, 550);
			
			/*
			//testing the screenx/y scroll box
			g2d.setColor(new Color(235,152,206));
			g2d.drawRect(320,240,160,120);
			*/
			
			
			
			
			//make getting damaged "flicker" the screen
			if (Link.getTimeSinceLastDamage() < 5){
				g2d.setColor(new Color(255,0,0,124));
				g2d.fillRect(0,0,800,600);	
			}
		
		}
		//dead cutscene
		else{
			
			drawDeadCutscene(g2d);
			
		}
		
	
	}
	
	//seperated the dead drawing into this method to make it more organized
	private void drawDeadCutscene(Graphics2D g2d){
		//clear screen with red
		g2d.setColor(new Color(255,0,0));
		g2d.fillRect(0,0,800,600);
		
		//draw link's death animation
		BufferedImage temp = PlayerSpriteContainer.getDeathImage(deadframe);
		Point offset = PlayerSpriteContainer.getDeathImageOffset(deadframe);
		g2d.drawImage(temp, (int)Link.getX() - (int)offset.getX() - screenx, (int)Link.getY() - (int)offset.getY() - screeny, null);
		
		//other text and stuff
		BufferedImage GO = MenuSpriteContainer.gameOver;
		g2d.drawImage(GO, 189,40,null);
		
		//cool outline the text illusion
		String endtext = "Click to RESTART";
		
		if (!deathFinished){
			endtext += " (not yet)";
		}
		else{
			endtext += " (now)";
		}
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Serif", Font.BOLD, 50));
		g2d.drawString(endtext, 48,500);
		g2d.drawString(endtext, 52,500);
		g2d.drawString(endtext, 50,502);
		g2d.drawString(endtext, 50,498);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Serif", Font.BOLD, 50));
		g2d.drawString(endtext, 50,500);
	}
	
	
	
	
	/*				Mouse stuff			*/
	
	private int mousex, mousey;
	
	
	private boolean mouseHeld[];
	//mouseHeld[i] == true if mousebutton #i is currently held down
	
	private boolean mouseFirstReleased[];
	//mouseFirstReleased[i] == true if mousebutton #i was just released, will turn back into false next 
	
	private void resetMouseFirstReleased(){
		for(int i = 0; i<4; i++){
			
			mouseFirstReleased[i] = false;	
			
			
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e){
		
	}
	
	public void mousePressed(MouseEvent e){
		mouseHeld[e.getButton()] = true;
		
		
			
	}
	
	public void mouseReleased(MouseEvent e){
		mouseHeld[e.getButton()] = false;
		mouseFirstReleased[e.getButton()] = true;
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseMoved(MouseEvent e){
		mousex = e.getX();
		mousey = e.getY();
	}
	
	public void mouseDragged(MouseEvent e){
		mousex = e.getX();
		mousey = e.getY();
	}
	
	
	

	
	/*				Keyboard stuff			*/
	
	
	
	
	
	//override the abstract keyListner
	//also a bit of stuff here to 
	
	//keyboard datastructure
	private boolean[] keys;
	private int[] timeSinceLastPress;
	private boolean[] isDoublePressed;
	
	
	public void incrementKeyCounters(){
		
		for(int i = 0; i<KeyEvent.KEY_LAST+1; i++){
			timeSinceLastPress[i] = Math.min(timeSinceLastPress[i] + 1,200);
			
		}		
	}
	
	
	@Override
	public void keyTyped(KeyEvent e){
		
	}
	
	private final int minTime = 10;
	public void keyPressed(KeyEvent e){
		keys[e.getKeyCode()] = true;
		

		if (timeSinceLastPress[e.getKeyCode()] < minTime){
			isDoublePressed[e.getKeyCode()] = true;
		}
		
		
		
	}
	
	public void keyReleased(KeyEvent e){
		
		timeSinceLastPress[e.getKeyCode()] = 0;
		
		//let go :. not pressed or doublepressed
		keys[e.getKeyCode()] = false;
		isDoublePressed[e.getKeyCode()] = false;
	}
	
	
	
}
